# Deploy Scripts
This directory hosts different scripts that automate the deployment to Kubernetes.

## Script - `apply_namespace.py`
This script is used to apply the `medical-record` namespace to the Kubernetes cluster.

It is useful, applying a namespace is idempotent. Because of that the script can be used modularly in other, more
advanced deployment scripts.

Run the script using the following command from the project root:
  - `python3 scripts/deploy/apply_namespace.py`