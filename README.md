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
There is no better reason for opting for microservices, than learning purposes. A monolithic architecture would be
much more appropriate for a small-scale project like this one. My intention behind the microservices was not to
create a system optimized for scaling, but rather learn more about distributed systems in general.

### Why use different means of communication, and not stick to simple RESTful APIs?
The reasons to include multiple means of network communication are two. First, again to expose myself to new,
modern technologies with the intention of learning more about them. Two, optimize inter-service communication for
performance and end-user experience. An HTTP request that bounces from service to service might take a significant amount
of time, depending on the headers and body size. Additionally, any database IO operations that a service might need
to perform, to fulfil the request will add more time overhead. To solve this, I've decided to build the
inter-service communication entirely on Remote Procedure Calls. RPC is 5/6 times faster than JSON, due to the binary
serialization and deserialization.

### Why use Kubernetes?
My best option would have been to use Docker Compose for defining and running multiple Docker containers for each
of the services. However, Kubernetes is the de-facto standard for deploying and orchestrating distributed systems.
The point of using a Kubernetes cluster is mainly for learning purposes.

### Why use my own auth?
Proper authentication and authorization are notoriously hard to implement. Many companies use third-party tools to
ensure secure auth for their applications. Implementing my own auth brings nothing more than learning more about
it. In this particular case, the system is not going to ever be used in a production setting with real users, so
proper security is not of upmost importance. Still, to follow best practices, I have decided to set the expiration
of the JWT tokens to only 60 minutes, to minimize the chance of cross-site request forgery (CSRF) attacks.