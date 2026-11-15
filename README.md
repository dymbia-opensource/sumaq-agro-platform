# SumaqAgro Platform

SumaqAgro Platform is the RESTful backend for SumaqAgro, a university project for agricultural management and technical advisory in Peru. It supports independent farmers who manage their plots and crop campaigns, cooperative directors who maintain member rosters, and agronomists who supervise assigned plots and provide technical advice.

The platform uses a modular monolith organized into bounded contexts. It applies Domain-Driven Design (DDD) to model agricultural workflows and separates commands from queries at the application layer.

## Tech Stack

- **Language:** Java 27
- **Framework:** Spring Boot 4.1.2
- **Database and persistence:** MySQL, Spring Data JPA, and Hibernate
- **Database naming:** Physical naming strategy for pluralized table names and snake_case identifiers
- **Security:** Spring Security, JWT, and BCrypt
- **API documentation:** SpringDoc OpenAPI and Swagger UI
- **External integration:** AgroMonitoring
- **Build tool:** Maven Wrapper

## Prerequisites

- JDK 27 with `JAVA_HOME` set to its installation directory.
- A running MySQL instance and an account with access to the development database.
- An AgroMonitoring API key. The current configuration requires it at startup.

## Project Structure

```text
sumaq-agro-platform/
├── .mvn/                          # Maven Wrapper configuration
├── docs/                          # Project documentation
├── src/
│   ├── main/
│   │   ├── java/com/dymbia/sumaqagro/platform/
│   │   │   ├── crop/              # Crop monitoring, alerts, inspections, and prescriptions
│   │   │   ├── farming/           # Field plots, boundaries, soil baselines, and crop campaigns
│   │   │   ├── iam/               # Accounts, roles, sign-in, and JWT authentication
│   │   │   ├── profiles/          # Profiles, cooperatives, member rosters, and assignments
│   │   │   ├── shared/            # Shared domain, application, persistence, and REST utilities
│   │   │   └── SumaqagroBackendApplication.java
│   │   └── resources/             # Spring profiles and localized messages
│   └── test/java/                  # Backend tests
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE.md
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

Bounded contexts use the following layers where applicable:

- `domain` holds aggregates, entities, value objects, commands, queries, domain events, and repository contracts.
- `application` defines command and query services and contains their implementations and event handlers.
- `infrastructure` implements persistence adapters, JPA mappings, external clients, and security components.
- `interfaces` exposes REST controllers, request and response resources, assemblers, and facades for other contexts.

## Configuration

Spring Boot reads profile-based configuration from `src/main/resources`.

| File | Responsibility |
| --- | --- |
| `application.properties` | Shared defaults, JPA settings, JWT, CORS, and AgroMonitoring configuration |
| `application-dev.properties` | Local MySQL connection settings and SQL logging |
| `application-prod.properties` | Deployment database configuration |

The default profile is `dev`. It connects to `localhost:3306` and uses the `sumaqagro` schema. Set environment variables in the terminal or IDE run configuration that starts the application.

| Variable | Description | Development default |
| --- | --- | --- |
| `DATABASE_USER` | MySQL username | Set locally |
| `DATABASE_PASSWORD` | MySQL password | Set locally |
| `JWT_SECRET` | JWT signing secret. Use at least 32 random bytes encoded as Base64. | Set locally |
| `AGROMONITORING_API_KEY` | Credential for the AgroMonitoring integration | Set locally |
| `JWT_EXPIRATION_MINUTES` | Token lifetime in minutes | `30` |
| `FRONTEND_ORIGIN` | Frontend origin allowed by CORS | `http://localhost:4200` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |

The `prod` profile also expects `DATABASE_URL` for the MySQL host, `DATABASE_PORT` for the port, and `DATABASE_NAME` for the schema. Keep credentials out of source control.

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/dymbia-opensource/sumaq-agro-platform.git
cd sumaq-agro-platform
```

### 2. Prepare the Database

Start MySQL and ensure the configured account can access the `sumaqagro` schema. If the account cannot create databases, create the schema beforehand. The development configuration lets Hibernate update the tables during startup.

### 3. Run the Application

After setting the required environment variables, start the backend from the repository root.

**Windows PowerShell**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS or Linux**

```bash
./mvnw spring-boot:run
```

### 4. Open the API Documentation

| Resource | Local URL |
| --- | --- |
| REST API base URL | `http://localhost:8080/api/v1` |
| Swagger UI | <http://localhost:8080/swagger-ui/index.html> |
| OpenAPI JSON | <http://localhost:8080/v3/api-docs> |

## Scripts and Commands

| Task | macOS or Linux | Windows PowerShell |
| --- | --- | --- |
| Compile | `./mvnw compile` | `.\mvnw.cmd compile` |
| Run tests | `./mvnw test` | `.\mvnw.cmd test` |
| Package the application | `./mvnw package` | `.\mvnw.cmd package` |
| Run build verification | `./mvnw verify` | `.\mvnw.cmd verify` |

Tests that load the Spring application context require the configured environment variables and a reachable MySQL instance.

## API Surface

| Context | Main endpoints |
| --- | --- |
| IAM | `POST /api/v1/users`, `POST /api/v1/tokens` |
| Profiles | `/api/v1/profiles`, `/api/v1/cooperatives`, `/api/v1/agronomist-assignments` |
| Farming | `/api/v1/field-plots`, `/api/v1/crop-campaigns` |
| Crop monitoring | `/api/v1/climate-forecasts`, `/api/v1/agroclimatic-alerts`, `/api/v1/regional-bulletins` |
| Technical advisory | `/api/v1/pest-reports`, `/api/v1/field-inspections`, `/api/v1/technical-prescriptions` |

Use Swagger UI to check the supported methods and request schemas for each resource.

To call a protected endpoint, register through `POST /api/v1/users` or use an existing development account. Send the account email and password to `POST /api/v1/tokens`, then include the returned token in the request header.

```http
Authorization: Bearer <token>
```

Registration accepts one role per account: `ROLE_FARMER`, `ROLE_COOPERATIVE_DIRECTOR`, or `ROLE_AGRONOMIST`.

## Architecture and Development Conventions

- **Commands and queries:** Command services handle writes. Query services handle reads.
- **Domain and persistence:** In contexts with separate domain and JPA models, repository adapters implement the domain contracts and assemblers map between the two representations.
- **Context boundaries:** Facades expose the capabilities that other contexts need. For example, Farming exposes field management operations through `FieldManagementContextFacade`.
- **Domain events:** Aggregates can record domain events. Repository adapters that use `DomainEventPublisher` dispatch them when saving the aggregate.
- **REST errors:** Shared assemblers convert application failures into `ProblemDetail` responses. Message bundles provide English and Spanish translations.

## Documentation

- [Project documentation directory](docs/)
- [Contributing guide](CONTRIBUTING.md)
- [Changelog](CHANGELOG.md)

## License

This project is licensed under the [MIT License](LICENSE.md).