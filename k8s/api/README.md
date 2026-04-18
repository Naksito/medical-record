# API Gateway manifest
This directory contains the `api.yaml` file, which describes how a Kubernetes cluster should create the
API Gateway containers. The file is a bundle of all different resources needed for the sake of simplicity.

The resources that the file creates are:
- `Deployment` - For describing how Kubernetes should create the Pod.
- `Service` - For describing how Kubernetes should handle the traffic to the Pod.

This manifest file requires `kubectl` to be installed, as well as configured and connected to a Kubernetes cluster. Also,
the cluster needs to have the `medical-record` namespace applied beforehand.

To apply the manifest to a Kubernetes cluster, use the following command from the project root:
- `kubectl apply -n medical-record -f k8s/api/api.yaml`

## NodePort Service
Since the frontend is packaged into a build file and send to the client's browser, we need a way to be able to make
requests to the API Gateway from outside the Kubernetes cluster. This is why the API Service manifest is of type
`NodePort`. It assigns a public port to the Service, in this case `31000`, and forwards traffic from outside of the
cluster, to the API.