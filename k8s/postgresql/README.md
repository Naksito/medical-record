# PostgreSQL manifest
This directory contains the `postgresql.yaml` file, which describes how a Kubernetes cluster should create the
PostgreSQL instance. The file is a bundle of all different resources needed for the sake of simplicity.

The resources that the file creates are:
  - `PersistentVolumeClaim` - For claiming persistent storage on the cluster.
  - `ConfigMap` - For running an `init.sql` script which will create the different databases for the microservices
  - `Deployment` - For describing how Kubernetes should create the Pod.
  - `Service` - For describing how Kubernetes should handle the traffic to the Pod.

In the `Deployments` you may also see that the PostgreSQL environment variables are hardcoded. Of course, this is a bad
practice and is only done here for convenience and exhaustiveness of the manifest, so that it will apply all needed
resources without the need of additional configuration.

This manifest file requires `kubectl` to be installed, as well as configured and connected to a Kubernetes cluster. Also,
the cluster needs to have the `medical-record` namespace applied beforehand.

To apply the manifest to a Kubernetes cluster, use the following command from the project root:
  - `kubectl apply -n medical-record -f k8s/postgresql/postgresql.yaml`