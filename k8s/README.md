# Kubernetes
The `/k8s` directory is used for storing the manifests used for deploying all resource required by the system.

## Manifest - `namespace.yaml`
This manifest can be used to apply the `medial-record` namespace to a Kubernetes cluster.

To apply the manifest, you can run the following `kubectl` command from the project root:
  - `kubectl apply -f k8s/namespace.yaml`