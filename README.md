# Medical Record System

This repository contains a distributed medical-record application developed for the CSCB869 Java Web Services course at New Bulgarian University. It allows patients and doctors to use different workflows around users, appointments, diagnoses, sick leaves, and medical information. An administrator can execute reporting queries through the application.

This README is both the operational guide for the repository and a documentation source for a longer academic report. It explains the motivation, requirements, architecture, implementation, data flow, security model, versioning, containerization, CI/CD, Kubernetes deployment, testing approach, limitations, and suggested material for expanding the project into a Word document of approximately 60 pages.

## Contents

1. [Project purpose](#project-purpose)
2. [Requirements and actors](#requirements-and-actors)
3. [Architecture](#architecture)
4. [Repository structure](#repository-structure)
5. [Technology choices](#technology-choices)
6. [Service responsibilities](#service-responsibilities)
7. [Communication and data flow](#communication-and-data-flow)
8. [Authentication and authorization](#authentication-and-authorization)
9. [Frontend](#frontend)
10. [REST API](#rest-api)
11. [gRPC contracts](#grpc-contracts)
12. [Persistence](#persistence)
13. [Versioning](#versioning)
14. [Docker images](#docker-images)
15. [Kubernetes](#kubernetes)
16. [Terraform](#terraform)
17. [CI/CD](#cicd)
18. [Local development](#local-development)
19. [Deployment procedure](#deployment-procedure)
20. [Verification and troubleshooting](#verification-and-troubleshooting)
21. [Security considerations](#security-considerations)
22. [Limitations and future work](#limitations-and-future-work)
23. [Suggested 60-page report](#suggested-60-page-report)

## Project Purpose

The project models a healthcare record system with separate services for authentication, users, appointments, and an HTTP API gateway. The frontend is a browser application served by Nginx. The system is intentionally implemented as a microservice architecture to study service boundaries, synchronous service communication, container images, Kubernetes resources, and automated deployment.

The architecture is educational rather than production-ready. A smaller monolithic application would be easier to operate for this domain and project size. The distributed design exists to demonstrate how independently running processes cooperate through explicit network contracts.

## Requirements and Actors

### Patient

- Register and authenticate.
- View the patient-specific menu.
- View or manage appointments through the available workflows.
- Access medical information associated with the patient.

### Doctor

- Register and authenticate.
- View doctor-specific data.
- Manage appointments and diagnoses according to the available permissions.
- Work with patient records associated with the doctor.

### Administrator

- Authenticate through the administrator flow.
- Access administrator-only queries and reporting screens.
- Inspect aggregate data such as patient, doctor, appointment, ICD, and sick-leave statistics.

### System requirements

- A browser for the Angular frontend.
- Java 17 and Maven for backend development.
- Node.js and npm for frontend development.
- Docker for image construction.
- A Kubernetes cluster and kubeconfig for deployment.
- Terraform for namespace and secret provisioning.
- GitHub Actions and Docker Hub credentials for the automated pipeline.

## Architecture

The system has five application services and one database dependency:

```text
Browser
	|
	| HTTP/JSON
	v
Nginx / Angular frontend
	|
	| /api proxy
	v
Spring Boot API gateway
	|
	| gRPC
	+--------------------+---------------------+------------------+
	v                    v                     v                  |
Auth service       Users service       Appointments service     |
	|                    |                     |                  |
	+--------------------+---------- PostgreSQL -----------------+
```

The frontend does not call the gRPC services directly. It sends HTTP requests to the API gateway. The gateway translates those requests into calls to the appropriate backend service. The auth, users, and appointments services expose gRPC endpoints generated from Protocol Buffer definitions. PostgreSQL is shared as a server instance, while separate databases are used for service data.

The repository originally referenced `docs/system-design.png`. If that diagram is present in the working copy, it can be included in the final report. If it is not present, create an updated diagram based on the architecture above and place it under `docs/`.

## Repository Structure

```text
pom.xml                       Parent Maven project and version properties
api/                          Spring Boot REST API gateway
auth/                         Authentication and authorization gRPC service
users/                        User and specialty gRPC service
appointments/                 Appointment and medical-record gRPC service
frontend/                     Angular application and Nginx image
k8s/                          Kubernetes manifests and image version manifest
terraform/                    Kubernetes namespace and secret resources
scripts/                      Versioning, rendering, and build helpers
.github/workflows/ci.yml      Build, image publishing, and deployment pipeline
```

Each Java service has its own `pom.xml`, source tree, Protocol Buffer definitions where needed, resource configuration, and Dockerfile. The root Maven project is the aggregator and parent for the four Java modules: `api`, `auth`, `users`, and `appointments`.

## Technology Choices

### Java and Maven

Java 17 is used for the backend services. Maven provides dependency management, compilation, generated gRPC code, packaging, and the multi-module build. Shared dependency versions are maintained in the root `pom.xml` through `dependencyManagement`.

### Spring Boot

The API gateway uses Spring Boot to expose REST controllers and to manage HTTP request handling and dependency injection.

### gRPC and Protocol Buffers

gRPC provides binary, strongly typed communication between Java services. Protocol Buffer files under the service `src/main/proto` directories define request and response messages and generate Java client/server classes during the Maven build.

### Angular and Nginx

Angular provides the browser application. The production build is copied into an Nginx Alpine image. Nginx serves static files and proxies requests beginning with `/api/` to the in-cluster API gateway.

### PostgreSQL and Hibernate

Services requiring persistence use PostgreSQL. Hibernate ORM maps Java entities to relational tables and handles database access. The users and appointments services contain their persistence models and repositories.

### Docker and Kubernetes

Docker packages each deployable service as an image. Kubernetes runs the images as Deployments and exposes them through Services. This gives the system repeatable deployment definitions and allows the application to run on a cluster rather than as manually started processes.

## Service Responsibilities

### API gateway

The `api` module is the only application-facing backend entry point. Its controllers expose REST resources for authentication, users, appointments, diagnoses, ICD records, sick leaves, general menu data, queries, page authorization, and service status. Gateway classes create gRPC channels and blocking stubs for downstream services.

The gateway also exposes `/api/status`. It reads its own `SERVICE_VERSION` environment variable and requests the versions of auth, users, and appointments through their gRPC version services.

### Auth service

The `auth` module handles login, registration-related authentication operations, password hashing through BCrypt, JWT creation, token validation, and authorization requests. It communicates with the users service when user information is required. Its JWT secret is supplied through the Kubernetes `auth-secret` secret.

### Users service

The `users` module persists doctors, patients, and specialties. It exposes gRPC methods for creating, retrieving, updating, and deleting these entities, as well as listing general practitioners and obtaining user-related counts.

### Appointments service

The `appointments` module manages appointments, diagnoses, ICD records, and sick leaves. It also provides reporting data used by the API query endpoints. It communicates with users through a gRPC gateway when it needs doctor or patient information.

### Frontend service

The `frontend` module contains the Angular application. It provides login, registration, menus, dashboards, appointment operations, diagnosis operations, administrator queries, and the global service-version footer. Nginx is the runtime process inside the frontend container.

## Communication and Data Flow

### Browser request

1. A user selects an action in the Angular application.
2. An Angular service creates an HTTP request under `/api`.
3. Nginx proxies the request to `api-service` inside the Kubernetes namespace.
4. A Spring controller validates the request and applies gateway-level authorization logic.
5. The gateway invokes one or more gRPC methods.
6. A backend service performs business logic and database operations.
7. The response travels back through gRPC, the gateway, Nginx, and Angular.

### Version request

The frontend loads `/api/status` for backend versions and `/version.json` for its own version. The API gateway obtains its own version from `SERVICE_VERSION` and obtains the other backend versions from their gRPC `VersionService` implementations. The global Angular component combines these values and displays them on every route.

## Authentication and Authorization

The auth service creates signed JWT tokens after successful authentication. The frontend stores the token in browser local storage and sends it with protected requests according to the existing application flow. The Angular route guard determines whether a route can be entered, while the API gateway and backend services enforce server-side authorization.

The JWT secret is not stored in source control. Terraform creates or manages the `auth-secret` Kubernetes secret, and the auth Deployment reads it through `secretKeyRef`. The application rejects missing or insufficiently long JWT secrets. Tokens currently have a limited lifetime, which reduces the impact of leaked credentials but also means users must authenticate again after expiry.

This implementation is suitable for a course project, not a public healthcare system. A real deployment would require an external identity provider, secure token storage, key rotation, audit logging, encrypted transport, strict secret handling, and a formal privacy and compliance review.

## Frontend

The Angular application is configured in `frontend/angular.json`. Its production build is generated into `dist/frontend`, with the browser entry point under the generated `browser` directory. Nginx serves that directory and proxies `/api/` requests to the API service.

The root Angular component owns the version footer. It initializes fallback values of `unknown`, requests the backend status endpoint, requests `/version.json`, and replaces the fallback values after successful responses. The footer is outside the router outlet, so it remains available on login, registration, home, menu, dashboard, and administrator routes.

The frontend image creates `public/version.json` during its Docker build:

```text
SERVICE_VERSION=2.0.2
		  |
		  v
public/version.json -> {"version":"2.0.2"}
```

After Angular builds, Nginx serves the generated JSON as a static file. A browser cache can retain old JavaScript bundles, so use a hard refresh after deployment.

## REST API

The API gateway uses the `/api` prefix. The main resource groups are:

| Resource | Main paths |
|---|---|
| Auth | `/api/auth/register-doctor`, `/api/auth/register-patient`, `/api/auth/log-doctor-in`, `/api/auth/log-patient-in`, `/api/auth/log-admin-in`, `/api/auth/validate-token` |
| Users | `/api/users/doctors/*`, `/api/users/patients/*`, `/api/users/specialty/*` |
| Appointments | `/api/appointments/create`, `/api/appointments/get/id`, `/api/appointments/list/all`, `/api/appointments/update`, `/api/appointments/delete`, `/api/appointments/start`, `/api/appointments/finish` |
| Diagnoses | `/api/diagnosis/create`, `/api/diagnosis/get/id`, `/api/diagnosis/list/all`, `/api/diagnosis/update`, `/api/diagnosis/delete` |
| ICD | `/api/icd/create`, `/api/icd/get/id`, `/api/icd/list/all`, `/api/icd/update`, `/api/icd/delete` |
| Sick leave | `/api/sick-leave/create`, `/api/sick-leave/get/id`, `/api/sick-leave/list/all`, `/api/sick-leave/update`, `/api/sick-leave/delete` |
| Menu data | `/api/general-work/patient-menu-data`, `/api/general-work/doctor-menu-data` |
| Queries | `/api/query/*` |
| Page authorization | `/api/pages/login`, `/api/pages/register`, `/api/pages/home`, `/api/pages/menu`, `/api/pages/dashboard`, `/api/pages/admin-query` |
| Status | `/api/status` |

The controller classes in `api/src/main/java/com/medrec/controllers` are the authoritative source for request methods, DTOs, query parameters, response structures, and authorization behavior. For the final academic documentation, add request and response examples for each endpoint group.

## gRPC Contracts

Protocol Buffer contracts are stored in `api/src/main/proto`, `auth/src/main/proto`, `users/src/main/proto`, and `appointments/src/main/proto` as applicable. The Maven Protocol Buffer plugin generates message classes and gRPC stubs during `mvn package`.

Each backend service implements a version RPC. This is used by the API gateway to provide a single status response to the frontend. The version RPC is deliberately small, but it demonstrates service discovery through a typed contract rather than direct database access or shared configuration.

## Persistence

PostgreSQL is deployed as a Kubernetes workload named `postgres`. The application uses separate databases for service concerns, including `users` and `appointments`. The database connection settings are provided through service configuration and Kubernetes environment variables.

The users service models doctors, patients, and specialties. The appointments service models appointments, diagnoses, ICD data, and sick leaves. Repositories isolate persistence operations from gRPC service implementations. Hibernate handles object-relational mapping, while application-specific exceptions map database and validation failures to service responses.

For a full report, include an entity-relationship diagram, table descriptions, key constraints, indexes, transaction boundaries, and examples of how a user action changes persisted data.

## Versioning

The root `pom.xml` is the source of version configuration. It contains:

```xml
<revision>2.0.0</revision>
<auth.version>2.0.0</auth.version>
<users.version>2.0.0</users.version>
<appointments.version>2.0.0</appointments.version>
<api.version>2.0.0</api.version>
<frontend.version>2.0.2</frontend.version>
```

The Java module POMs inherit the parent properties. CI evaluates every service property with Maven and passes the values to Docker as image tags and as `SERVICE_VERSION` build arguments. The backend Dockerfiles expose that value at runtime. The frontend Dockerfile writes its value to `version.json`.

`k8s/versions.yaml` records the image repository and tag used for deployment. `scripts/update_versions.py` updates this file from per-service environment variables. `scripts/render_manifests.py` replaces placeholders such as `__AUTH_IMAGE__` and `__AUTH_TAG__` in Kubernetes manifests.

To release a frontend-only change, edit the frontend property:

```xml
<frontend.version>2.0.3</frontend.version>
```

For a backend-only change, edit the corresponding service property. A new tag is important because Kubernetes must pull a new immutable image instead of restarting an old image with the same tag.

## Docker Images

The image repositories are:

```text
naksito03/auth-service
naksito03/users-service
naksito03/appointments-service
naksito03/api-service
naksito03/frontend-service
```

The Java Dockerfiles copy the Maven-produced JAR into a small Alpine-based runtime image. The gRPC services use a custom Java runtime generated with `jlink`. The API and frontend images expose their HTTP ports; the other services expose their gRPC ports.

Example local image build:

```bash
docker build --build-arg SERVICE_VERSION=2.0.0 \
  -t naksito03/auth-service:2.0.0 ./auth
```

The CI workflow also pushes a `latest` tag for convenience. Deployments use explicit semantic version tags from `k8s/versions.yaml`, which is safer and more reproducible than deploying `latest`.

## Kubernetes

The `k8s` directory contains resources for the `medical-record` namespace, PostgreSQL, the four Java services, and the frontend. Each application has a Deployment and a Service. The frontend is exposed with a NodePort, while internal services use ClusterIP.

The single-node cluster used during development experienced memory pressure during rolling updates. The application Deployments therefore use a `Recreate` strategy and reduced resource requests suitable for that cluster. This reduces temporary double-pod memory usage, but it introduces a short availability gap during replacement. A production cluster should use a rolling strategy with sufficient capacity and carefully configured disruption budgets.

Typical inspection commands:

```bash
kubectl get pods -n medical-record -o wide
kubectl get deployments -n medical-record
kubectl get services -n medical-record
kubectl describe pod -n medical-record <pod-name>
kubectl logs -n medical-record <pod-name>
kubectl rollout status deployment/frontend-service -n medical-record --timeout=300s
```

## Terraform

Terraform in `terraform/main.tf` manages the namespace and three secrets:

- `auth-secret` containing `JWT_SECRET`.
- `users-secret` containing the users database password.
- `appointments-secret` containing the appointments database password.

The Kubernetes provider reads a kubeconfig path from `terraform/variables.tf`. Secret variables are marked sensitive. The CI workflows initialize Terraform and import existing resources before applying, which makes reruns work with a cluster that was provisioned previously.

Never commit real passwords, JWT keys, kubeconfig files, or Terraform state containing sensitive values. The repository currently contains Terraform state files in the Terraform directory; review and protect them before using a public repository for anything beyond coursework.

## CI/CD

The main workflow is `.github/workflows/ci.yml`. It runs for pushes to `main` and manual dispatch. Its stages are:

1. Check out the repository.
2. Install Java 17, Terraform, and kubectl.
3. Cache Maven dependencies.
4. Build all Maven modules.
5. Extract `revision` and each service version property.
6. Log in to Docker Hub using GitHub Secrets.
7. Build and push five versioned images and `latest` tags.
8. Update and commit `k8s/versions.yaml`.
9. Configure kubeconfig from `KUBE_CONFIG`.
10. Import and apply Terraform-managed namespace and secrets.
11. Render and apply Kubernetes manifests.
12. Wait for each Deployment rollout and print diagnostics on failure.

The separate `.github/workflows/deploy.yml` workflow can deploy when `k8s/versions.yaml` changes. `.github/workflows/dry-run.yml` provides a manual render-only workflow that uploads rendered manifests as an artifact without applying them to a cluster.

Required GitHub Secrets:

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN
KUBE_CONFIG
JWT_SECRET
USERS_DB_PASSWORD
APPOINTMENTS_DB_PASSWORD
```

The `KUBE_CONFIG` secret must contain a valid kubeconfig with a current context and access to the target cluster. The JWT secret must satisfy the application minimum length requirement.

## Local Development

### Backend build

From the project root:

```bash
mvn clean package
```

To skip tests during an image-oriented build:

```bash
mvn clean package -DskipTests
```

The resulting JAR files are placed in the individual module `target` directories. The generated gRPC sources are produced by the Protocol Buffer Maven plugin.

### Frontend development

From `frontend`:

```bash
npm ci
npm start
```

The Angular development server provides a local development experience. Production validation uses:

```bash
npm run build
```

The frontend expects the API gateway to be available through the configured `/api` path. For local development, configure a suitable Angular proxy or run the gateway where the development server can reach it.

### Local Kubernetes deployment

After building and tagging images, render manifests and apply them:

```bash
python scripts/update_versions.py 2.0.0
python scripts/render_manifests.py
kubectl apply -f /tmp/medical-record-k8s-rendered/namespace.yaml
kubectl apply -f /tmp/medical-record-k8s-rendered/postgresql/postgresql.yaml
kubectl apply -f /tmp/medical-record-k8s-rendered/auth/auth.yaml
kubectl apply -f /tmp/medical-record-k8s-rendered/users/users.yaml
kubectl apply -f /tmp/medical-record-k8s-rendered/appointments/appointments.yaml
kubectl apply -f /tmp/medical-record-k8s-rendered/api/api.yaml
kubectl apply -f /tmp/medical-record-k8s-rendered/frontend/frontend.yaml
```

The Python scripts require Python 3. The image names and tags must exist in the configured registry, and private images require a valid Kubernetes image pull secret.

## Deployment Procedure

The normal release process is:

1. Change one or more service version properties in the root `pom.xml`.
2. Review the source changes and confirm semantic version format `x.y.z`.
3. Push to `main` or manually dispatch the CI/CD workflow.
4. Confirm Maven resolves the intended properties.
5. Confirm Docker Hub contains each new image tag.
6. Confirm `k8s/versions.yaml` contains the intended tags.
7. Wait for the deployment workflow to finish.
8. Check rollout output for every Deployment.
9. Test the frontend and API status endpoint.
10. Hard-refresh the browser after frontend changes.

For a frontend release, verify both:

```text
http://<node-ip>:32000/
http://<node-ip>:32000/version.json
```

The second URL should return JSON such as `{"version":"2.0.2"}`. The page footer should display API, Auth, Users, Appointments, and Frontend versions.

## Verification and Troubleshooting

### Version footer is missing

Check the frontend image:

```bash
kubectl get deployment frontend-service -n medical-record -o wide
```

If it still uses an old tag, restart is insufficient. Build and deploy a new image tag. Then verify `/version.json`. If that URL returns the Angular `index.html`, the running image has the old Nginx configuration or no generated version file.

### Version shows `unknown`

The footer starts with fallback values. Check the browser Network tab for `/api/status` and `/version.json`. The API endpoint must return JSON with `api`, `auth`, `users`, and `appointments`. The version asset must return JSON with `version`. Inspect browser console errors and Nginx configuration if either request fails.

### Login and Register buttons are missing

The home template uses Angular's `*ngIf` directive. `HomeComponent` must import `NgIf` in its standalone component imports. Also check that the browser is running a newly built frontend image rather than a cached or older image.

### Pod remains Pending

Inspect events:

```bash
kubectl describe pod -n medical-record <pod-name>
```

The single-node development cluster previously reported `Insufficient memory`. Reduce requests only with care, remove unused workloads, or increase cluster capacity. Do not hide repeated scheduling failures by increasing rollout timeouts indefinitely.

### Auth pod fails to start

Check that `auth-secret` exists and contains a sufficiently long `JWT_SECRET`:

```bash
kubectl get secret auth-secret -n medical-record
kubectl describe pod -n medical-record <auth-pod>
kubectl logs -n medical-record <auth-pod>
```

Do not print secret values in CI logs.

### Image pull failures

The manifests refer to `regcred`. Verify that the secret exists in the `medical-record` namespace and that the image repository and tag are correct. A warning about an old or missing pull secret may be harmless for public images, but it should be corrected for private repositories.

## Security Considerations

The project demonstrates authentication, but it is not a production healthcare security design. Important concerns include:

- Do not use default administrator credentials.
- Do not hardcode database passwords in manifests.
- Store JWT secrets and kubeconfig only as protected secrets.
- Use HTTPS and encrypted gRPC transport outside a trusted development cluster.
- Rotate secrets and signing keys.
- Avoid exposing database services publicly.
- Add audit logging for access to medical records.
- Validate authorization on the server for every protected operation.
- Avoid storing long-lived JWTs in local storage in a production browser application.
- Protect, remove, or encrypt Terraform state containing sensitive infrastructure data.

## Limitations and Future Work

Known limitations and useful improvements include:

- Add automated unit, integration, API contract, and end-to-end tests.
- Add health and readiness endpoints for every service.
- Add TLS, network policies, RBAC, resource quotas, and PodDisruptionBudgets.
- Replace default credentials with secret-backed configuration.
- Use an external identity provider and key rotation.
- Add database migrations instead of relying only on ORM schema behavior.
- Add observability with structured logs, metrics, and distributed tracing.
- Use a registry and deployment strategy that supports signed images and provenance.
- Add rollback automation after failed rollouts.
- Separate build, image publication, manifest update, and deployment into clearer jobs with explicit artifact passing.
- Add dependency and container vulnerability scanning.
- Add a formal data-retention, privacy, and backup strategy.
- Improve frontend loading and error states for unavailable services.
- Add generated OpenAPI and gRPC documentation.

## Suggested 60-Page Report

The following structure can be expanded into a Word document of approximately 60 pages. Page counts are approximate and depend on diagrams, screenshots, code excerpts, and formatting.

| Section | Suggested pages | Content |
|---|---:|---|
| Abstract and keywords | 1 | Problem, solution, technologies, results |
| Introduction | 3 | Motivation, scope, objectives, methodology |
| Domain analysis | 4 | Healthcare workflows, actors, requirements |
| Related technologies | 5 | REST, gRPC, Angular, Spring Boot, Docker, Kubernetes |
| System requirements | 3 | Functional and non-functional requirements |
| Architecture design | 7 | Context diagram, containers, service boundaries, deployment view |
| API gateway | 4 | Controllers, DTOs, routing, authorization, error handling |
| Authentication | 4 | Registration, login, BCrypt, JWT, route guards, threat discussion |
| Users service | 4 | Entities, repositories, gRPC methods, validation |
| Appointments service | 4 | Appointments, diagnoses, ICD, sick leaves, reports |
| Database design | 5 | ER diagram, tables, constraints, persistence lifecycle |
| Frontend design | 3 | Angular components, services, routes, Nginx, version footer |
| Containerization | 3 | Dockerfiles, JAR packaging, jlink, image tags |
| Kubernetes and Terraform | 5 | Resources, secrets, scheduling, infrastructure state |
| CI/CD and versioning | 4 | GitHub Actions, semantic versions, registry, deployment flow |
| Testing and evaluation | 3 | Test strategy, rollout checks, manual scenarios, results |
| Security and limitations | 2 | Risks, ethical issues, production gaps |
| Conclusion and future work | 1 | Achievements and improvements |

### Recommended report evidence

Add the following items to turn this README into a strong report:

- System context and container architecture diagrams.
- Sequence diagrams for login, registration, appointment creation, and status/version loading.
- Component diagrams for the Angular frontend and API gateway.
- ER diagrams for users and appointments databases.
- Screenshots of login, registration, menu, dashboard, administrator query, and version footer pages.
- REST request/response examples.
- Representative Protocol Buffer definitions and generated-stub explanation.
- Docker build and image-tag screenshots.
- Kubernetes Deployment, Service, Secret, and Terraform excerpts.
- GitHub Actions run screenshots showing Maven build, image push, manifest update, and rollout verification.
- A table comparing the intended version in `pom.xml`, image tag in `k8s/versions.yaml`, deployed image, and displayed UI value.
- A testing table containing scenario, input, expected result, observed result, and status.

## Closing Summary

The project demonstrates a complete educational path from domain requirements to a running distributed application: separate Java services are built with Maven, communicate with gRPC, persist data in PostgreSQL, are packaged as Docker images, deployed with Kubernetes and Terraform, and published through GitHub Actions. Per-service semantic version properties in the root POM flow into image tags, runtime version metadata, Kubernetes manifests, and the frontend display. The most important operational rule is to verify the deployed image tag and the live `/version.json` endpoint whenever a frontend change is made.