# Contributing to SumaqAgro Backend

Thank you for contributing to the SumaqAgro RESTful API, built by the **Dymbia** team. This document sets the architecture rules, coding standards and development workflow that keep the codebase consistent while several team members work on different bounded contexts.

---

## Table of Contents
1. [Architecture & Design Principles](#architecture--design-principles)
2. [Git Workflow & Branching Strategy](#git-workflow--branching-strategy)
3. [Conventional Commits](#conventional-commits)
4. [Semantic Versioning (SemVer)](#semantic-versioning-semver)
5. [Java Guidelines](#java-guidelines)
6. [Spring Boot Standards](#spring-boot-standards)
7. [REST API Standards](#rest-api-standards)
8. [Configuration & Environments](#configuration--environments)
9. [Quality Assurance & Development Workflow](#quality-assurance--development-workflow)
10. [Pull Request (PR) Process](#pull-request-pr-process)

---

## Architecture & Design Principles

### Domain-Driven Design (DDD)
The code is organized by **bounded context** under `com.dymbia.sumaqagro.platform`. Package names match the names used by the Web Application whenever possible, so both repositories speak the same language.

| Package | Bounded context | Responsibility |
|---|---|---|
| `shared` | Shared Kernel | Base classes, `Result` and `ApplicationError`, error handling, naming strategy and configuration used by every context. |
| `farming` | Farming (Field Management) | Plots with their GPS polygon, crop campaigns and the cost ledger with the breakeven price. |
| `crophealth` | Crop Health | Satellite monitoring (NDVI/NDWI), climate forecasts, agroclimatic alerts, pest reports, inspections and prescriptions. |
| `profiles` | Profiles | Profiles, cooperatives and their members, and the assignment of agronomists to plots. |
| `harvestcertification` | Harvest Certification | Harvest batches, quality grading and certificates with public QR traceability. |
| `iam`, `subscriptions` | IAM, Subscriptions and Payments | Users and authentication; plans, payments and the plot quota. |

A context that is not in the code yet is designed in the class diagrams of the project report.

Each bounded context follows the same four layers:
```text
com.dymbia.sumaqagro.platform.<bounded-context>/
├── domain/
│   └── model/
│       ├── aggregates/     # Aggregate roots: plain classes extending AbstractDomainAggregateRoot
│       ├── entities/       # Entities that live inside an aggregate
│       ├── valueobjects/   # Value objects and enums
│       ├── commands/       # Commands: records that state an intention (RegisterFieldPlotCommand)
│       ├── queries/        # Queries: records that ask a question (GetFieldPlotByIdQuery)
│       └── events/         # Domain events
├── application/
│   ├── commandservices/    # Interfaces of the use cases that change data (*CommandService)
│   ├── queryservices/      # Interfaces of the use cases that read data (*QueryService)
│   ├── internal/           # Implementations (*Impl) and event handlers
│   └── outboundservices/   # Ports: what the context needs from the outside
├── infrastructure/
│   ├── persistence/jpa/    # entities/ (JPA), assemblers/ (aggregate <-> entity), repositories/ (Spring Data), adapters/ (repository implementations)
│   └── <technology>/       # Adapters of external systems
└── interfaces/
    ├── rest/               # Controllers; resources/ (JSON) and transform/ (assemblers)
    └── acl/                # Facades that other bounded contexts may call
```

Dependency rules between layers:

| Code in… | May use |
|---|---|
| `interfaces` | `application` (interfaces), `domain` (commands, queries, models to convert) and `shared` |
| `application` | `domain`, the repositories of `infrastructure/persistence` and its own ports |
| `domain` | Only `shared/domain` |
| `infrastructure` | `domain`, `application/outboundservices` (to implement ports) and `shared` |

Bounded contexts do not use each other's services, repositories or aggregates. They talk in two ways:
- **Domain events**: a context publishes an event and another one reacts to it.
- **ACL facades** (`interfaces/acl`): a context exposes a small class that returns simple values, for example `FarmingContextFacade`. An aggregate references another one only by its **id** (`Long plotId`), never by the object.

### Object-Oriented Programming (OOP) & Clean Code
- **SOLID**: One responsibility per class (the command validates its data, the service coordinates the use case, the repository saves, the assembler converts, the controller only translates HTTP). Depend on abstractions: controllers use the service interfaces, and services use ports instead of concrete clients.
- **CQRS**: Command services change state and return a `Result`; query services only read.
- **Rich Domain Model**: Business rules live in the aggregates and value objects, not in controllers.
- **Persistence separated from the domain**: An aggregate is a plain class with no JPA annotations. Its `<Aggregate>PersistenceEntity` extends `AuditableAbstractPersistenceEntity` (id, `createdAt`, `updatedAt`), a `<Aggregate>PersistenceAssembler` converts both ways and a `<Aggregate>RepositoryImpl` adapter saves the entity and publishes the domain events with `DomainEventPublisher`.
- **Encapsulation**: Aggregates use `@Getter` only. No public setters: state changes through methods named after the business action.
- **Value Objects**: Values without identity validate themselves in the constructor of the record. States and types are enums, never loose strings.
- **Rules that need the database** (a name is not repeated, there is a free quota) live in the command service. Rules that only need the data of the aggregate live in the aggregate.

---

## Git Workflow & Branching Strategy

We follow the **Git Flow** branching model (the GitFlowHelper plugin of IntelliJ IDEA is used to start and finish branches):

```text
main ──────────────────────────────────────────●────── (Releases, tagged vX.Y.Z)
         \                                    /
develop ──●─────────●───────────────●────────●──────── (Integration branch)
           \       /                 \      /
feature/    ●─────●                   ●────●           (Feature branches)
```

- `main`: Released code, tagged with SemVer (`v1.0.0`).
- `develop`: Integration branch where finished features are merged.
- `feature/<context>-<short-description>`: New features, refactors, fixes and documentation (from `develop`, back to `develop`).
  - *Examples:* `feature/farming-crop-campaigns`, `feature/shared-persistence-separation`
  - Always use the `feature/` prefix.
- `release/v<MAJOR.MINOR.PATCH>`: Release preparation (from `develop`, merged to `main` and `develop`).
- `hotfix/v<MAJOR.MINOR.PATCH>`: Urgent fixes of a released version (from `main`, merged to `main` and `develop`).

Before starting a branch, update `develop`:
```bash
git checkout develop
git pull origin develop
git checkout -b feature/<context>-<short-description>
```

Every member commits their own work. Do not commit changes of another member's bounded context without agreeing first. A change in `shared` affects everyone: announce it to the team before merging it.

---

## Conventional Commits

Commit messages follow the [Conventional Commits v1.0.0](https://www.conventionalcommits.org/) specification.

```text
<type>(<scope>): <short summary in imperative mood>

[optional body explaining why the change was made]

[optional footer, such as BREAKING CHANGE]
```

| Type       | Description                                                   |
|------------|---------------------------------------------------------------|
| `feat`     | A new feature (aggregate, command, service, endpoint)         |
| `fix`      | A bug fix                                                     |
| `docs`     | Documentation only                                            |
| `refactor` | Code changes that neither fix a bug nor add a feature         |
| `perf`     | Performance improvements                                      |
| `test`     | Adding or updating tests                                      |
| `build`    | Build configuration or dependency changes                     |
| `chore`    | Maintenance tasks                                             |
| `revert`   | Reverts a previous commit                                     |

The scope is the **aggregate** the commit works on, or the area changed: `field-plot`, `crop-campaign`, `campaign-ledger`, `crop-health`, `farming`, `profiles`, `harvest-certification`, `iam`, `subscriptions`, `shared`, `configuration`, `pom`, `deps`.

Inside a bounded context, one commit is one small piece, in the order of the layers: value objects, aggregate, queries, repository, services, resources and assemblers, controller and messages.

Examples:
- `feat(field-plot): add FieldPlot aggregate`
- `feat(crop-campaign): implement command and query services`
- `refactor(farming): rename fieldmanagement package to farming`
- `feat(shared): add problem detail and response entity assemblers`
- `docs: add changelog, contributing guidelines and license`

---

## Semantic Versioning (SemVer)

Versions follow [SemVer 2.0.0](https://semver.org/): `MAJOR.MINOR.PATCH`. Every release is described in [`CHANGELOG.md`](CHANGELOG.md).

- **MAJOR**: Incompatible changes of the REST contract (a removed endpoint or a renamed JSON field).
- **MINOR**: New backwards-compatible features, such as new endpoints or a new bounded context.
- **PATCH**: Backwards-compatible bug fixes.

---

## Java Guidelines

- **Java version**: Java 27 (`java.version` in `pom.xml`).
- **Naming Conventions**:
  - `PascalCase`: Classes, records, interfaces and enums.
  - `camelCase`: Methods, fields and variables.
  - `UPPER_SNAKE_CASE`: Constants and enum values.
  - Packages in lowercase without separators (`farming`, `crophealth`).
- **Class names by role**:

  | Role | Name | Example |
  |---|---|---|
  | Command / Query | `<Verb><Noun>Command`, `Get<Noun>By<Criteria>Query` | `RegisterFieldPlotCommand`, `GetFieldPlotByIdQuery` |
  | Services | `<Aggregate>CommandService`, `<Aggregate>QueryService`, `...Impl` | `FieldPlotCommandServiceImpl` |
  | Repository | `<Aggregate>Repository` | `FieldPlotRepository` |
  | Resources | `<Verb><Noun>Resource` (input), `<Noun>Resource` (output) | `RegisterFieldPlotResource`, `FieldPlotResource` |
  | Assemblers | `<Command>CommandFromResourceAssembler`, `<Noun>ResourceFromEntityAssembler` | `FieldPlotResourceFromEntityAssembler` |
  | Controllers | `<Aggregates>Controller` | `FieldPlotsController` |
  | Events | `<Aggregate><PastParticiple>Event` | `CropCampaignStartedEvent` |

- **Records** for commands, queries, resources, events and single-value objects. Validate in the compact constructor and throw `IllegalArgumentException` with a **message key** (`"field.plot.error.name.notBlank"`), never a hardcoded text. Use `java.util.Objects.requireNonNull` for nulls.
- **Lombok**: `@Getter` on aggregates and entities. Do not add `@Setter` or `@Data` to them.
- **Value objects**: one value is a `record` plus an `AttributeConverter`; several values are `@Embeddable`; a list is an `@ElementCollection`. Store enums with `@Enumerated(EnumType.STRING)`.
- **Money and measures**: Use `BigDecimal`, never `double`.
- **Comments**: Write Javadoc in English for public classes and methods, with `@param`, `@return`, `@throws` and `@since`. Use short `//` comments only for a rule that is not obvious from the code.

---

## Spring Boot Standards

- **Dependency Injection**: Constructor injection with `final` fields. No field injection with `@Autowired`.
- **Transactions**: Use `org.springframework.transaction.annotation.Transactional`. Command services use `@Transactional`; query services use `@Transactional(readOnly = true)`.
- **Errors without exceptions**: A command service returns `Result<T, ApplicationError>` with `ApplicationError.notFound(...)`, `conflict(...)` or `validation(...)`. Exceptions are for invalid input detected in a record and for programming errors.
- **Controllers only translate**: they convert the resource into a command or query, call the service and build the response with the shared `ResponseEntityAssembler`. They have no business rules and no private error conversion.
- **Events**: An aggregate registers a domain event with `registerDomainEvent(...)`; the repository adapter publishes it through `DomainEventPublisher` after saving, and the handlers (`@EventListener`) run in the same transaction.
- **External systems** (AgroMonitoring and others): define a port in `application/outboundservices` and implement it in `infrastructure/<technology>`. Never call an external API from the domain or a controller, and never log an API key.
- **Persistence**: Hibernate generates the schema and `SnakeCaseWithPluralizedTablePhysicalNamingStrategy` names tables and columns. The `@Id` and the audit fields come from `AuditableAbstractPersistenceEntity`, never from the aggregate.

---

## REST API Standards

- **Versioned, resource-oriented paths**: `/api/v1/<plural-resource>` (`/api/v1/field-plots`).
- **Status codes**:

  | Code | When |
  |---|---|
  | `200 OK` | A successful read or update. A list with no results is `200` with `[]`. |
  | `201 Created` | A resource was created. |
  | `204 No Content` | A resource was deleted. |
  | `400 Bad Request` | Invalid data. |
  | `404 Not Found` | The resource does not exist. |
  | `409 Conflict` | A business rule blocks the change (duplicated name, quota reached). |

- **JSON**: `camelCase` fields and ISO dates, with the same names the Web Application uses.
- **Errors**: RFC 9457 problem details with `type`, `title`, `status`, `detail` and `code`. The `detail` is translated according to the `Accept-Language` header (English by default, Spanish with `es`).
- **Messages**: Every message key goes in **both** `messages.properties` and `messages_es.properties`.
- **Documentation**: Annotate controllers with `@Tag` and `@Operation`. Swagger UI is at `http://localhost:8080/swagger-ui/index.html`.

---

## Configuration & Environments

- `application.properties`: common values.
- `application-dev.properties`: local MySQL.
- `application-prod.properties`: database configured from environment variables.

Secrets are never written in the files or committed. They are read from environment variables:

| Variable | Used for |
|---|---|
| `DATABASE_USER`, `DATABASE_PASSWORD` | MySQL credentials |
| `DATABASE_URL`, `DATABASE_PORT`, `DATABASE_NAME` | MySQL location |
| `AGROMONITORING_API_KEY` | API key of AgroMonitoring (polygons and satellite data) |

In IntelliJ IDEA set them in **Run → Edit Configurations → Environment variables**.

---

## Quality Assurance & Development Workflow

### Prerequisites
- JDK 27
- MySQL running locally
- The environment variables above

### Development Commands
```bash
# Run the application on http://localhost:8080
./mvnw spring-boot:run

# Run the tests
./mvnw test

# Build the jar
./mvnw clean package
```

### How to check a change
1. Run the application and read the `create table` statements: check table and column names.
2. Try the endpoint in Swagger UI with the happy path **and** the error cases (a `500` in an error case is a bug).
3. Look at the table in MySQL to confirm what was saved.
4. When connected to the Web Application, check the request in the browser's Network tab: the JSON must match what the web expects.

### Tests
- Test rules of aggregates, value objects and assemblers with plain unit tests (no Spring, no database).
- Name test classes `<ClassName>Tests`.

---

## Pull Request (PR) Process

Before merging into `develop`, verify that:
1. [ ] The code respects the DDD layers and the dependency rules above.
2. [ ] Commit messages follow Conventional Commits, one small piece per commit.
3. [ ] Every new message key exists in English and Spanish.
4. [ ] The application compiles and the tests pass (`./mvnw test`).
5. [ ] The endpoints were tried in Swagger UI, including the error cases.
6. [ ] No secret, API key or password was committed.
7. [ ] The JSON matches the contract used by the Web Application.
8. [ ] A change in `shared` was announced to the team.
9. [ ] `CHANGELOG.md` is updated when features or architecture change.
10. [ ] The branch targets `develop` (or `main` for hotfixes).
