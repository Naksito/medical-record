#!/usr/bin/env python3
import os
import re
import sys
from pathlib import Path

version = os.environ.get("VERSION") or (sys.argv[1] if len(sys.argv) > 1 else None)
service_env_map = {
    "auth": "AUTH_VERSION",
    "users": "USERS_VERSION",
    "appointments": "APPOINTMENTS_VERSION",
    "api": "API_VERSION",
    "frontend": "FRONTEND_VERSION",
}

# If no global version provided, require per-service env vars to be present
if not version and not any(os.environ.get(v) for v in service_env_map.values()):
    raise SystemExit("Usage: python scripts/update_versions.py <version> or set per-service env vars")

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
    env_key = service_env_map.get(service_name)
    tag = os.environ.get(env_key) or version
    if not tag:
        raise SystemExit(f"No version supplied for {service_name}; set VERSION or {env_key}")
    pattern = rf"(?m)^  {service_name}:\n    image: .*\n    tag: .*$"
    replacement = f"  {service_name}:\n    image: naksito03/{image_name}\n    tag: {tag}"
    text, count = re.subn(pattern, replacement, text)
    if count == 0:
        raise SystemExit(f"Could not find service entry for {service_name} in {versions_file}")

versions_file.write_text(text, encoding="utf-8")
print(f"Updated {versions_file} to version {version}")
