#!/usr/bin/env python3
import os
import re
import sys
from pathlib import Path

version = os.environ.get("VERSION") or (sys.argv[1] if len(sys.argv) > 1 else None)
if not version:
    raise SystemExit("Usage: python scripts/update_versions.py <version>")

repo_root = Path(__file__).resolve().parent.parent
versions_file = repo_root / "k8s" / "versions.yaml"
text = versions_file.read_text(encoding="utf-8")

services = [
    ("auth", "auth-service"),
    ("users", "users-service"),
    ("appointments", "appointments-service"),
    ("api", "api-service"),
    ("frontend", "frontend-service"),
]

for service_name, image_name in services:
    pattern = rf"(?m)^  {service_name}:\n    image: .*\n    tag: .*$"
    replacement = f"  {service_name}:\n    image: naksito03/{image_name}\n    tag: {version}"
    text, count = re.subn(pattern, replacement, text)
    if count == 0:
        raise SystemExit(f"Could not find service entry for {service_name} in {versions_file}")

versions_file.write_text(text, encoding="utf-8")
print(f"Updated {versions_file} to version {version}")
