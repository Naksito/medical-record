#!/usr/bin/env python3
import argparse
import os
import subprocess
import sys
from pathlib import Path

try:
    from .bump_service_versions import SERVICES, get_property
except ImportError:
    from bump_service_versions import SERVICES, get_property


def manifest_tags(text):
    tags = {}
    service = None
    for line in text.splitlines():
        if line.startswith("  ") and not line.startswith("    ") and line.rstrip().endswith(":"):
            service = line.strip()[:-1]
        elif service and line.startswith("    tag:"):
            tags[service] = line.split(":", 1)[1].strip()
    return tags


def changed_release_services(base_pom, current_pom, base_manifest, current_manifest):
    old_tags = manifest_tags(base_manifest)
    new_tags = manifest_tags(current_manifest)
    changed = []
    for service, (_, property_name) in SERVICES.items():
        current_version = get_property(current_pom, property_name)
        if new_tags.get(service) != current_version:
            raise ValueError(
                f"Version mismatch for {service}: pom.xml has {current_version}, "
                f"k8s/versions.yaml has {new_tags.get(service)}"
            )
        if (
            get_property(base_pom, property_name) != get_property(current_pom, property_name)
            or old_tags.get(service) != new_tags.get(service)
        ):
            changed.append(service)
    return sorted(changed)


def git_show(repo_root, ref, path):
    return subprocess.run(
        ["git", "-C", str(repo_root), "show", f"{ref}:{path}"],
        check=True,
        capture_output=True,
        text=True,
    ).stdout


def main():
    parser = argparse.ArgumentParser(
        description="Select services whose release versions changed."
    )
    parser.add_argument("--base-ref", help="Commit before the triggering push")
    parser.add_argument("--all", action="store_true", help="Select every service")
    parser.add_argument("--repo-root", type=Path, default=Path(__file__).resolve().parent.parent)
    args = parser.parse_args()

    if not args.all and not args.base_ref:
        parser.error("provide --base-ref or --all")

    if args.all:
        selected = sorted(SERVICES)
    else:
        repo_root = args.repo_root.resolve()
        selected = changed_release_services(
            git_show(repo_root, args.base_ref, "pom.xml"),
            (repo_root / "pom.xml").read_text(encoding="utf-8"),
            git_show(repo_root, args.base_ref, "k8s/versions.yaml"),
            (repo_root / "k8s" / "versions.yaml").read_text(encoding="utf-8"),
        )

    outputs = {service: str(service in selected).lower() for service in SERVICES}
    outputs["images_changed"] = str(bool(selected)).lower()
    output_file = os.environ.get("GITHUB_OUTPUT")
    output = "".join(f"{key}={value}\n" for key, value in outputs.items())
    if output_file:
        with open(output_file, "a", encoding="utf-8") as stream:
            stream.write(output)
    else:
        sys.stdout.write(output)
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, subprocess.CalledProcessError, ValueError) as error:
        print(f"Release selection failed: {error}", file=sys.stderr)
        raise SystemExit(1)