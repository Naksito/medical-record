# Medical Record System

## Overview
This is a medical record system that allows doctors to manage their patients' medical records, and patients to book
appointments with different doctors.

This project is built as a part of the "CSCB869 Java Web Services" course at the New Bulgarian University.

## Technical Overview
The system follows the microservices architecture for designing distributed systems.

Key technologies:
- Frontend: Angular
- Web Server: Nginx
- Backend: Java with the Maven build tool
- Inter-service Communication: gRPC
- API Gateway: A Spring Boot RESTful HTTP API
- Database: PostgreSQL
- Containerization: Docker
- Orchestration: Kubernetes

Apart from the frontend, all services are built using `Java`. `Maven` is used as a build tool. All services live
within their own `Maven` project, and all projects live under a parent `Maven` project as a multi-module setup.

Inter-service communication uses Google's implementation of `Remote Procedure Calls` - `gRPC`.

The frontend communicates with the services through a `RESTful HTTP API` gateway. `Nginx` is used as a web server, inside
the Docker image, to serve the built Angular files to the client's browser.

Services that require data persistence use a common `PostgreSQL` server instance with independent databases inside.

The services are containerized in `Docker` containers. All containers are orchestrated using `Kubernetes`. Additionally,
dependencies like the `PostgreSQL` database are also added to the cluster using manifests.

List of all services:
 - A frontend service
 - An `API Gateway` service
 - An `Auth` service
 - An `Appointments` service
 - A `Users` service

## Architecture
Here is an image of what the system's architecture looks like

![image-of-systems-design](docs/system-design.png)

## Architecture Q&A

### Why microservices architecture?
The system is designed using a microservices architecture to achieve modularity and separation of concerns. Each service is responsible for a specific business domain, such as authentication, user management, or appointment scheduling.
This approach allows independent development, testing, and deployment of components, and reduces coupling between different parts of the system. While a monolithic architecture would be simpler, the microservices approach better demonstrates distributed system design principles and aligns with modern software engineering practices.

###Why use multiple communication mechanisms instead of only REST?
The system uses a combination of REST and remote procedure calls to match different communication needs.
REST is used for external communication between the frontend and backend, as it is simple, widely supported, and well-suited for client-server interaction.
For internal service-to-service communication, a more efficient binary communication mechanism is used. This reduces serialization overhead and improves performance compared to text-based formats.
This separation improves both usability at the system boundary and efficiency inside the system.

###Why use Kubernetes?
The system is deployed using a container orchestration platform to ensure consistent execution of multiple independent services.
This approach provides:
automated service management
isolation between components
simplified deployment of distributed systems
improved reliability through self-recovery mechanisms
Although simpler tools exist for small systems, this approach reflects how distributed applications are managed in production environments.

###Why implement a custom authentication service?
Authentication is implemented as a separate service to maintain clear separation of concerns and to integrate it as part of the system architecture rather than relying on external providers.
The goal of this component is to demonstrate how authentication flows can be designed and integrated within a distributed system.
For security reasons, token-based authentication is used with limited validity to reduce risk exposure in case of token leakage.

###Why use a microservices-based design for a relatively small system?
Although microservices are typically used in large-scale systems, this design was chosen to demonstrate architectural principles of distributed systems, including service decomposition, communication patterns, and independent deployment.
The trade-off is increased system complexity compared to a monolithic architecture, but this is acceptable in the context of an academic project focused on system design rather than operational simplicity.
test CI trigger
test test test
test
test
testing again
