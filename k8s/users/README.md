# Users manifest
This directory contains the `users.yaml` file, which describes how a Kubernetes cluster should create the
`Users` service containers. The file is a bundle of all different resources needed for the sake of simplicity.

The resources that the file creates are:
- `Deployment` - For describing how Kubernetes should create the Pod.
- `Service` - For describing how Kubernetes should handle the traffic to the Pod.

This manifest file requires `kubectl` to be installed, as well as configured and connected to a Kubernetes cluster. Also,
the cluster needs to have the `medical-record` namespace applied beforehand.

To apply the manifest to a Kubernetes cluster, use the following command from the project root:
- `kubectl apply -n medical-record -f k8s/users/users.yaml`