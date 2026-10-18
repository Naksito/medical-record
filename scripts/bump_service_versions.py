#!/usr/bin/env python3
import argparse
import re
import subprocess
import sys
from pathlib import Path


SERVICES = {
    "api": ("api", "api.version"),
    "auth": ("auth", "auth.version"),
    "appointments": ("appointments", "appointments.version"),
    "users": ("users", "users.version"),
    "frontend": ("frontend", "frontend.version"),
}
SEMVER_PATTERN = re.compile(r"^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$")


def parse_version(version):
    match = SEMVER_PATTERN.fullmatch(version.strip())
    if not match:
        raise ValueError(f"Expected semantic version x.y.z, got {version!r}")
    return tuple(int(part) for part in match.groups())


def choose_next_version(base_version, current_version):
    base = parse_version(base_version)
    current = parse_version(current_version)
    if current != base:
        if current <= base:
            raise ValueError(
                f"Manually selected version {current_version} must be greater than "
                f"the previous version {base_version}"
            )
        return current_version
    return f"{current[0]}.{current[1]}.{current[2] + 1}"


def changed_services(paths):
    changed = set()
    for path in paths:
        first_part = Path(path).parts[0] if Path(path).parts else ""
        for service, (directory, _) in SERVICES.items():
            if first_part == directory:
                changed.add(service)
    return sorted(changed)


def get_property(text, property_name):
    pattern = re.compile(
        rf"(?m)^\s*<{re.escape(property_name)}>([^<]+)</{re.escape(property_name)}>\s*$"
    )
    matches = pattern.findall(text)
    if len(matches) != 1:
        raise ValueError(f"Expected exactly one {property_name} property in root pom.xml")
    return matches[0].strip()


def set_property(text, property_name, version):
    pattern = re.compile(
        rf"(?m)(^\s*<{re.escape(property_name)}>)[^<]+(</{re.escape(property_name)}>\s*$)"
    )
    updated, count = pattern.subn(rf"\g<1>{version}\g<2>", text)
    if count != 1:
        raise ValueError(f"Expected exactly one {property_name} property in root pom.xml")
    return updated


def set_manifest_tag(text, service, version):
    lines = text.splitlines(keepends=True)
    current_service = None
    match_count = 0
    for index, line in enumerate(lines):
        if line.startswith("  ") and not line.startswith("    ") and line.rstrip().endswith(":"):
            current_service = line.strip()[:-1]
        elif current_service == service and line.startswith("    tag:"):
            newline = "\r\n" if line.endswith("\r\n") else "\n" if line.endswith("\n") else ""
            lines[index] = f"    tag: {version}{newline}"
            match_count += 1
    if match_count != 1:
        raise ValueError(f"Expected exactly one tag for {service} in k8s/versions.yaml")
    return "".join(lines)


def git_show(repo_root, ref, path):
    return subprocess.run(
        ["git", "-C", str(repo_root), "show", f"{ref}:{path}"],
        check=True,
        capture_output=True,
        text=True,
    ).stdout


def read_text_preserving_newlines(path):
    with path.open("r", encoding="utf-8", newline="") as stream:
        return stream.read()


def write_text_preserving_newlines(path, text):
    with path.open("w", encoding="utf-8", newline="") as stream:
        stream.write(text)


def main():
    parser = argparse.ArgumentParser(
        description="Increment patch versions for services changed in a push."
    )
    parser.add_argument("--base-ref", required=True, help="Commit before the triggering push")
    parser.add_argument("--repo-root", type=Path, default=Path(__file__).resolve().parent.parent)
    args = parser.parse_args()

    repo_root = args.repo_root.resolve()
    diff = subprocess.run(
        ["git", "-C", str(repo_root), "diff", "--name-only", args.base_ref, "HEAD"],
        check=True,
        capture_output=True,
        text=True,
    ).stdout
    changed_paths = diff.splitlines()
    services = set(changed_services(changed_paths))
    base_pom = git_show(repo_root, args.base_ref, "pom.xml")

    pom_path = repo_root / "pom.xml"
    manifest_path = repo_root / "k8s" / "versions.yaml"
    pom_text = read_text_preserving_newlines(pom_path)
    manifest_text = read_text_preserving_newlines(manifest_path)
    for service, (_, property_name) in SERVICES.items():
        if get_property(base_pom, property_name) != get_property(pom_text, property_name):
            services.add(service)

    services = sorted(services)
    if not services:
        print("No service directories changed; no version PR changes are needed.")
        return 0

    for service in services:
        _, property_name = SERVICES[service]
        previous = get_property(base_pom, property_name)
        current = get_property(pom_text, property_name)
        next_version = choose_next_version(previous, current)
        pom_text = set_property(pom_text, property_name, next_version)
        manifest_text = set_manifest_tag(manifest_text, service, next_version)
        print(f"{service}: {previous} -> {next_version}")

    write_text_preserving_newlines(pom_path, pom_text)
    write_text_preserving_newlines(manifest_path, manifest_text)
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, subprocess.CalledProcessError, ValueError) as error:
        print(f"Version bump failed: {error}", file=sys.stderr)
        raise SystemExit(1)