#!/usr/bin/env python3
import os
import re
import shutil
from pathlib import Path

repo_root = Path(__file__).resolve().parent.parent
source_dir = repo_root / "k8s"
versions_file = source_dir / "versions.yaml"
output_dir = Path(os.environ.get("OUTPUT_DIR", "/tmp/medical-record-k8s-rendered"))

if output_dir.exists():
    shutil.rmtree(output_dir)
output_dir.mkdir(parents=True, exist_ok=True)

versions = {}
current_service = None
for line in versions_file.read_text(encoding="utf-8").splitlines():
    stripped = line.strip()
    if not stripped or stripped.startswith("#"):
        continue
    if stripped == "services:":
        continue
    service_match = re.match(r"^([A-Za-z0-9_-]+):$", stripped)
    if service_match:
        current_service = service_match.group(1)
        versions[current_service] = {}
        continue
    if current_service is None:
        continue
    if stripped.startswith("image:"):
        versions[current_service]["image"] = stripped.split(":", 1)[1].strip()
    elif stripped.startswith("tag:"):
        versions[current_service]["tag"] = stripped.split(":", 1)[1].strip()

manifest_map = {
    "auth": source_dir / "auth" / "auth.yaml",
    "users": source_dir / "users" / "users.yaml",
    "appointments": source_dir / "appointments" / "appointments.yaml",
    "api": source_dir / "api" / "api.yaml",
    "frontend": source_dir / "frontend" / "frontend.yaml",
}

for service_name, source_path in manifest_map.items():
    if not source_path.exists():
        raise SystemExit(f"Missing manifest: {source_path}")

    content = source_path.read_text(encoding="utf-8")
    service_versions = versions.get(service_name, {})
    if not service_versions:
        raise SystemExit(f"Missing version info for service: {service_name}")

    service_upper = service_name.upper()
    content = content.replace(f"__{service_upper}_IMAGE__", service_versions["image"])
    content = content.replace(f"__{service_upper}_TAG__", service_versions["tag"])

    relative_path = source_path.relative_to(source_dir)
    target_path = output_dir / relative_path
    target_path.parent.mkdir(parents=True, exist_ok=True)
    target_path.write_text(content, encoding="utf-8")

for path in source_dir.rglob("*.yaml"):
    if path.name == "versions.yaml":
        continue
    if path in manifest_map.values():
        continue
    relative_path = path.relative_to(source_dir)
    target_path = output_dir / relative_path
    target_path.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(path, target_path)

for path in source_dir.rglob("*.yml"):
    relative_path = path.relative_to(source_dir)
    target_path = output_dir / relative_path
    target_path.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(path, target_path)

print(f"Rendered manifests to {output_dir}")
