# Medical Record DevOps Platform

This project documents the DevOps implementation around an existing distributed medical-record application. The application services are the workload. The work described here is the automated process that builds, versions, packages, publishes, deploys, and verifies that workload.

Its main subject is the delivery lifecycle:

```text
Git repository
    -> Maven build
    -> Per-service semantic versions
    -> Docker images
    -> Docker Hub
    -> Terraform resources and secrets
    -> Rendered Kubernetes manifests
    -> Kubernetes deployment
    -> Rollout and runtime verification
```

The application itself is described only where that context is necessary to understand the infrastructure and release process.

## DevOps Objectives

The implementation has the following objectives:

- Build the Java services consistently through one Maven parent project.
- Give every service an independent semantic version in the root `pom.xml`.
- Convert those versions into immutable Docker image tags.
- Publish versioned images to Docker Hub automatically.
- Keep Kubernetes deployment versions in a tracked manifest.
- Provision the Kubernetes namespace and secrets with Terraform.
- Render and apply Kubernetes manifests automatically.
- Verify that every Deployment becomes ready after release.
- Produce diagnostics when scheduling, image-pull, secret, or rollout problems occur.
- Make a service-specific release possible without rebuilding unrelated services.

## Contribution Boundary

The backend and frontend business logic already provide the application functionality. The DevOps contribution is the automation and infrastructure around it:

- Maven release configuration
- Per-service version propagation
- Docker build configuration
- Docker Hub image publishing
- GitHub Actions workflows
- Terraform Kubernetes resources
- Kubernetes manifests and deployment strategy
- Runtime version metadata
- Rollout verification and troubleshooting

This distinction is important: the application is the deployed workload, while the project contribution is the repeatable delivery platform.

## System Under Deployment

The workload contains five application images and one database dependency:

| Component | Role | Runtime technology | Internal port |
|---|---|---|---:|
| `auth-service` | Authentication and authorization | Java gRPC | 9091 |
| `users-service` | User and specialty data | Java gRPC | 9090 |
| `appointments-service` | Appointments and medical records | Java gRPC | 9092 |
| `api-service` | HTTP API gateway | Spring Boot | 8080 |
| `frontend-service` | Browser application | Angular and Nginx | 80 |
| `postgres` | Shared database server | PostgreSQL | 5432 |

The deployment architecture is:

```text
Browser
   |
   | HTTP
   v
Frontend Service / Nginx
   |
   | /api proxy
   v
API Gateway / Spring Boot
   |
   | gRPC
   +------------------+---------------------+
   v                  v                     v
Auth Service      Users Service      Appointments Service
   |                  |                     |
   +------------------+----------+----------+
                                  v
                              PostgreSQL
```

The DevOps work does not require changing this architecture. It makes the architecture buildable and deployable in a controlled way.

## Repository Layout

```text
pom.xml                       Maven parent and service version properties
api/                          API gateway source and Dockerfile
auth/                         Authentication service source and Dockerfile
users/                        Users service source and Dockerfile
appointments/                 Appointments service source and Dockerfile
frontend/                     Angular source, Dockerfile, and Nginx config
k8s/                          Kubernetes manifests and selected image versions
terraform/                    Kubernetes namespace and secret resources
scripts/                      Version update and manifest rendering tools
.github/workflows/ci.yml      Build, publish, and deployment workflow
.github/workflows/deploy.yml  Manifest-driven deployment workflow
.github/workflows/dry-run.yml Render-only validation workflow
```

## Technology Stack

| Area | Technology | DevOps purpose |
|---|---|---|
| Build | Maven and Java 17 | Compile modules, generate gRPC code, package JARs |
| Frontend build | Angular and npm | Create the production browser bundle |
| Packaging | Docker | Produce reproducible service images |
| Registry | Docker Hub | Store and distribute versioned images |
| Infrastructure | Terraform | Manage namespace and Kubernetes secrets |
| Orchestration | Kubernetes | Run, expose, and replace workloads |
| Automation | GitHub Actions | Connect source changes to deployment |
| Runtime checks | kubectl | Inspect resources and verify rollouts |

## Version Management

### Root POM properties

The root `pom.xml` is the release configuration source. It contains a shared revision and independent service properties:

```xml
<revision>2.0.0</revision>
<auth.version>2.0.0</auth.version>
<users.version>2.0.0</users.version>
<appointments.version>2.0.0</appointments.version>
<api.version>2.0.0</api.version>
<frontend.version>2.0.2</frontend.version>
```

Each service version follows semantic versioning in the form `major.minor.patch`.

The Java module POMs inherit the parent version property. The CI workflow evaluates each service property individually with Maven, so the frontend can be released as `2.0.3` while the backend services remain at their current versions.

### Version propagation

The value flows through the system as follows:

```text
pom.xml property
    -> Maven help:evaluate
    -> GitHub Actions output
    -> Docker image tag and SERVICE_VERSION build argument
    -> k8s/versions.yaml
    -> rendered Kubernetes manifest
    -> running container
    -> runtime version endpoint or UI footer
```

### Deployment manifest

`k8s/versions.yaml` records the image repository and tag selected for each service:

```yaml
services:
  auth:
    image: naksito03/auth-service
    tag: 2.0.0
  users:
    image: naksito03/users-service
    tag: 2.0.0
  appointments:
    image: naksito03/appointments-service
    tag: 2.0.0
  api:
    image: naksito03/api-service
    tag: 2.0.0
  frontend:
    image: naksito03/frontend-service
    tag: 2.0.2
```

The manifest is the bridge between image publication and Kubernetes deployment. It also provides an auditable record of the intended release.

## Docker Image Construction

Each deployable component has a Dockerfile. The Java images copy the Maven-produced JAR into a small runtime image. The gRPC services use `jlink` to create a reduced Java runtime. The frontend image builds Angular files and serves them through Nginx.

The backend images receive their version at build time:

```bash
docker build \
  --build-arg SERVICE_VERSION=2.0.0 \
  -t naksito03/auth-service:2.0.0 \
  ./auth
```

The frontend image creates `public/version.json` before the Angular build:

```text
SERVICE_VERSION=2.0.2
        |
        v
public/version.json -> {"version":"2.0.2"}
```

The deployment uses explicit version tags rather than relying on `latest`. The workflow also pushes `latest` for convenience, but the explicit tag is the reproducible release reference.

## Docker Hub Publishing

The configured image repositories are:

```text
naksito03/auth-service
naksito03/users-service
naksito03/appointments-service
naksito03/api-service
naksito03/frontend-service
```

For every service, CI pushes:

- an immutable semantic version tag, such as `2.0.2`
- a convenience `latest` tag

The Docker Hub login uses `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` stored as GitHub Secrets. No registry credentials should be committed to source control.

## Terraform Infrastructure

Terraform is used for the Kubernetes resources that are infrastructure-level concerns:

- the `medical-record` namespace
- `auth-secret` containing `JWT_SECRET`
- `users-secret` containing the users database password
- `appointments-secret` containing the appointments database password

The Kubernetes provider reads the kubeconfig path from `terraform/variables.tf`. Sensitive variables are marked `sensitive` and supplied by CI.

The workflow imports existing resources before applying Terraform. This is important because the target cluster may already contain the namespace and secrets. Importing makes repeated workflow runs idempotent instead of failing because a resource already exists.

Typical Terraform commands are:

```bash
terraform -chdir=terraform init -upgrade
terraform -chdir=terraform plan
terraform -chdir=terraform apply
```

Never commit real passwords, JWT keys, kubeconfig files, or unprotected Terraform state. Terraform state may contain sensitive infrastructure information and requires appropriate access control.

## Kubernetes Deployment

The `k8s` directory contains manifests for:

- namespace
- PostgreSQL
- auth service
- users service
- appointments service
- API gateway
- frontend

Each application workload has a Deployment and a Service. Internal services use ClusterIP. The frontend uses a NodePort so it can be accessed externally through port `32000`.

The manifests contain placeholders such as `__AUTH_IMAGE__` and `__AUTH_TAG__`. `scripts/render_manifests.py` reads `k8s/versions.yaml`, substitutes the image values, and writes rendered manifests to `/tmp/medical-record-k8s-rendered`.

The development cluster is a constrained single-node cluster. Earlier rollouts failed because a temporary second pod could not be scheduled due to insufficient memory. The application Deployments use the `Recreate` strategy and reduced resource requests to avoid that rollout overlap. This is appropriate for the demonstration cluster but causes a short replacement gap and should be reconsidered for production.

Useful commands:

```bash
kubectl get pods -n medical-record -o wide
kubectl get deployments -n medical-record
kubectl get services -n medical-record
kubectl describe pod -n medical-record <pod-name>
kubectl logs -n medical-record <pod-name>
kubectl rollout status deployment/frontend-service -n medical-record --timeout=300s
```

## GitHub Actions Pipeline

The release process uses three workflows. `.github/workflows/service-version-bump.yml` reacts to service-directory changes (and direct service-version edits in the root POM), then opens or updates a pull request for the version metadata. `.github/workflows/ci.yml` runs after version metadata is merged to `main`, builds the application, publishes only services whose POM version or manifest tag changed, and then deploys after publishing succeeds. `.github/workflows/deploy.yml` remains available for manual redeploys.

### Pipeline stages

1. A push changing files under a service directory starts `service-version-bump.yml`.
2. The workflow runs unit tests for the version automation.
3. It increments the changed service's patch version, unless a higher version was explicitly selected in the POM.
4. It updates the matching service tag in `k8s/versions.yaml` and opens a pull request.
5. Review and merge that pull request to approve the release version.
6. The merge triggers `ci.yml` because the deployment manifest changed.
7. CI builds all Maven modules, reads each service version, and identifies which image tags changed.
8. CI builds and pushes only the affected service images.
9. A dependent deployment job starts only after image publishing succeeds.
10. The deployment job configures kubeconfig, imports and applies Terraform resources, renders and applies the Kubernetes manifests, and verifies all rollouts.

The `ci.yml` workflow can also be manually dispatched; that path selects all services for image publication and then runs deployment. The separate `deploy.yml` workflow is retained for a manual redeploy without rebuilding images. The `dry-run.yml` workflow validates version extraction and manifest rendering without changing the cluster.

### Required GitHub Secrets

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN
KUBE_CONFIG
JWT_SECRET
USERS_DB_PASSWORD
APPOINTMENTS_DB_PASSWORD
```

`KUBE_CONFIG` must contain a valid kubeconfig with a current context and access to the target cluster. `JWT_SECRET` must meet the application's minimum length requirement.

The repository must allow GitHub Actions to create pull requests. Enable **Settings > Actions > General > Workflow permissions > Allow GitHub Actions to create and approve pull requests**. The version-bump workflow uses `GITHUB_TOKEN` with `contents: write` and `pull-requests: write` permissions.

## Release Procedure

### Service release

1. Commit and push a service change to `main`.
2. `service-version-bump.yml` detects the changed service and proposes a patch-version increase in a pull request.
3. Review the proposed `pom.xml` and `k8s/versions.yaml` changes. If desired, set a higher semantic version in the POM; the automation preserves it.
4. Merge the version pull request.
5. CI builds and publishes only images whose version changed.
6. The dependent deployment job applies the new manifest after image publishing succeeds.
7. Confirm the rollout and runtime version.

### Frontend-only release

For a frontend-only source change, push the frontend change to `main`. The automation proposes the next patch version. Alternatively, set a higher version explicitly in the root POM, for example:

```xml
<frontend.version>2.0.3</frontend.version>
```

After the version PR is merged, the pipeline should create `naksito03/frontend-service:2.0.3` without publishing new backend image tags. After deployment, verify:

```text
http://<node-ip>:32000/version.json
```

Expected response:

```json
{"version":"2.0.3"}
```

Then use a hard browser refresh to load the new Angular bundle.

## Runtime Verification

Verification must check both infrastructure state and application-visible state.

### Kubernetes state

```bash
kubectl get deployments -n medical-record
kubectl get pods -n medical-record
kubectl rollout status deployment/postgres -n medical-record --timeout=300s
kubectl rollout status deployment/auth-service -n medical-record --timeout=300s
kubectl rollout status deployment/users-service -n medical-record --timeout=300s
kubectl rollout status deployment/appointments-service -n medical-record --timeout=300s
kubectl rollout status deployment/api-service -n medical-record --timeout=300s
kubectl rollout status deployment/frontend-service -n medical-record --timeout=300s
```

### Public endpoints

The deployed frontend is exposed through the NodePort:

```text
http://<node-ip>:32000/
```

The backend status endpoint should return backend versions:

```text
http://<node-ip>:32000/api/status
```

Example:

```json
{"api":"2.0.0","auth":"2.0.0","users":"2.0.0","appointments":"2.0.0"}
```

The frontend version endpoint should return:

```text
http://<node-ip>:32000/version.json
```

Example:

```json
{"version":"2.0.2"}
```

The frontend global footer combines these values and displays them on all routes.

## Troubleshooting

### Pipeline does not run

Confirm the commit was pushed to `main`, the workflow file is under `.github/workflows`, and the workflow was not skipped by its path filters. A manual dispatch can be used from the GitHub Actions page.

### Docker image is not published

Check Docker Hub credentials and inspect the image build step. Confirm that the extracted Maven property is not empty and that the repository name is correct.

### Kubernetes still uses an old image

Check:

```bash
kubectl get deployment frontend-service -n medical-record -o wide
```

Restarting a Deployment does not rebuild an image. A new image tag must be built, published, selected in `k8s/versions.yaml`, and applied to Kubernetes.

### Frontend version endpoint returns HTML

If `/version.json` returns the Angular `index.html`, the running image is old or the Nginx static-file route is missing. Inspect the running container:

```bash
kubectl get deployment frontend-service -n medical-record -o wide
kubectl exec -n medical-record <frontend-pod> -- \
  find /usr/share/nginx/html/frontend -name version.json
```

Deploy a newly built frontend image and verify the endpoint again.

### Version shows `unknown`

The footer uses `unknown` as a safe initial fallback. Check both `/api/status` and `/version.json` in the browser Network panel. If `/api/status` fails, inspect the API pod and Nginx proxy. If `/version.json` fails, inspect the frontend image and Nginx configuration.

### Rollout times out

Inspect scheduling and container events:

```bash
kubectl describe deployment <deployment> -n medical-record
kubectl describe pod <pod-name> -n medical-record
kubectl logs <pod-name> -n medical-record
```

The development cluster previously reported `Insufficient memory`. Increase cluster capacity, remove unused workloads, or adjust resource requests deliberately. Increasing the timeout alone does not solve a scheduling failure.

### Auth service does not start

Verify the secret exists and that the pod reads it:

```bash
kubectl get secret auth-secret -n medical-record
kubectl describe pod <auth-pod> -n medical-record
kubectl logs <auth-pod> -n medical-record
```

Do not print secret values in logs or documentation.

## Security and Operational Limits

This is an educational DevOps implementation, not a production healthcare platform. Important limitations are:

- Secrets depend on GitHub Secrets and Kubernetes Secret resources.
- The cluster configuration is suitable for demonstration, not high availability.
- The development cluster uses a single node and has limited memory.
- The deployment strategy can cause a short service interruption during replacement.
- The workflow uses a registry account and requires correctly configured credentials.
- Terraform state requires protection because infrastructure state can contain sensitive information.
- The application uses development credentials and should not be exposed as a real healthcare service.
- TLS, image signing, vulnerability scanning, observability, and formal disaster recovery are not fully implemented.


## Final Summary

The project delivers a repeatable DevOps process for a multi-service application. Maven defines and exposes independent service versions. GitHub Actions builds the modules, creates Docker images, publishes them to Docker Hub, updates the deployment manifest, provisions Kubernetes resources through Terraform, applies rendered manifests, and checks rollout readiness. Kubernetes then runs the selected versions, while runtime endpoints and the frontend footer provide evidence of what is actually deployed.
