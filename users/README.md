# Users Service
The `Users` service is a Java, gRPC server that persists all user data. It uses the `Hibernate ORM` for defining schemas
for the `users` database, inside the shared `PostgreSQL` database instance.

## Design
The `Users` database design consists of the following tables:
  - `Doctor` - table for storing all information about each doctor.
  - `Patient` - table for storing all information about each patient.
  - `Specialty` - table for storing all different specialties a `Doctor` can specialize in.
