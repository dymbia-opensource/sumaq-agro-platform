# SumaqAgro Platform — Study Guide

> A comprehensive learning resource for Software Engineering students, based on the SumaqAgro Platform codebase. This guide explains the architectural patterns, design principles, framework features, and language capabilities applied in this RESTful API project for precision agriculture without field hardware.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture: Layered DDD with Ports and Adapters](#2-architecture-layered-ddd-with-ports-and-adapters)
   - [SOLID Principles](#solid-principles-in-this-architecture)
3. [Domain-Driven Design Patterns](#3-domain-driven-design-patterns)
4. [CQRS: Command Query Responsibility Segregation](#4-cqrs-command-query-responsibility-segregation)
5. [Modern Java Features (Java 21+)](#5-modern-java-features-java-21)
6. [Spring Boot Framework Patterns](#6-spring-boot-framework-patterns)
7. [Spring Security & JWT Authentication](#7-spring-security--jwt-authentication)
8. [REST API Design & Best Practices](#8-rest-api-design--best-practices)
9. [Persistence with Spring Data JPA](#9-persistence-with-spring-data-jpa)
10. [External Integrations: Mercado Pago, AgroMonitoring, Brevo](#10-external-integrations-mercado-pago-agromonitoring-brevo)
11. [Testing Strategies](#11-testing-strategies)
12. [Cross-Cutting Concerns](#12-cross-cutting-concerns)
13. [Recommended Learning Resources](#13-recommended-learning-resources)
    - [Architecture & Design Fundamentals](#architecture--design-fundamentals)
    - [Domain-Driven Design](#domain-driven-design)
    - [Architectural Styles](#architectural-styles)
    - [CQRS & Event Sourcing](#cqrs--event-sourcing)
    - [SOLID Principles](#solid-principles)
    - [Java Modern Features](#java-modern-features)
    - [Spring Boot](#spring-boot)
    - [REST API Design](#rest-api-design)
    - [External Services](#external-services)
    - [Testing](#testing)

---

## 1. Project Overview

### What is this project?

The **SumaqAgro Platform** is the RESTful API of a web platform for smallholder farmers, agricultural cooperatives, and agronomists in Peru. It registers plots and their polygons, monitors crop health with satellite indexes (NDVI, NDWI) and weather forecasts, tracks campaign costs and the breakeven price, manages cooperatives and their technical teams, charges subscription plans with Mercado Pago, and certifies the quality of harvest batches with a QR code that anyone can verify.

### Tech Stack

| Category         | Technology                                                       |
|------------------|------------------------------------------------------------------|
| Language         | **Java 27** (with Project Loom virtual threads)                  |
| Framework        | **Spring Boot 4.1.2** (Web MVC, Validation, Data JPA, Security)  |
| Database         | **MySQL 8+**                                                     |
| Persistence      | **Spring Data JPA / Hibernate**                                  |
| Security         | **Spring Security + JWT (JJWT) + BCrypt**                        |
| API Docs         | **SpringDoc OpenAPI (Swagger UI)**                               |
| Documents        | **Apache PDFBox** (certificate PDF) and **ZXing** (QR code)      |
| External APIs    | **Mercado Pago**, **AgroMonitoring**, **Brevo**                  |
| Build Tool       | **Maven 3.9+**                                                   |
| Containerization | **Docker** (deployed on Render)                                  |

### Bounded Contexts

The application is divided into seven modules: six **bounded contexts** and a shared kernel.

| Module            | Responsibility                                                                                 |
|-------------------|------------------------------------------------------------------------------------------------|
| `iam/`            | Identity & Access Management: users, roles, JWT sign-in, password recovery                     |
| `profiles/`       | Profiles, cooperatives, member registry, agronomist invitations and assignments               |
| `subscriptions/`  | Plans (Semilla, Cooperativa Pro, Asesor Técnico), subscriptions, Mercado Pago payments, quotas |
| `farming/`        | Field Management: plots and polygons, crop campaigns, campaign cost ledger, breakeven price    |
| `crop/`           | Crop Health: satellite observations, forecasts, agroclimatic alerts, pest reports, prescriptions |
| `certification/`  | Harvest Certification: harvest batches, grading, quality certificates, public traceability     |
| `shared/`         | Shared kernel: base aggregate, Result type, Problem Details, event publisher, email port, i18n |

---

## 2. Architecture: Layered DDD with Ports and Adapters

### What is Hexagonal Architecture?

Also known as **Ports and Adapters** architecture, it keeps the domain logic at the center of the application, isolated from external concerns like databases, web frameworks, payment providers, and satellite APIs.

**Why this matters**: SumaqAgro talks to MySQL, Mercado Pago, AgroMonitoring, and Brevo. If the domain depended on any of them, a change in a provider's JSON or an outage in its sandbox would break business rules and tests. By keeping the domain pure, you can:
- **Test business logic without Spring or a database** — `CropCampaignTests` runs in milliseconds
- **Swap providers freely** — replace Mercado Pago with another gateway by writing a new `PaymentGateway` adapter
- **Reason about your code** — the domain layer tells you what the business does (a closed campaign accepts no expenses), not how it is stored or served

**Origin**: Introduced by **Alistair Cockburn** in 2005 ([original article](https://alistair.cockburn.us/hexagonal-architecture/)); related styles are **Clean Architecture** by **Robert C. Martin** ([blog post](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)), **Layered Architecture** described by **Martin Fowler** ([article](https://martinfowler.com/bliki/PresentationDomainDataLayering.html)), and **Onion Architecture** by **Jeffrey Palermo** ([blog series](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/)).

### How this project implements it

The project is a **modular monolith**: one deployable Spring Boot application, split into bounded contexts that only talk through facades. Each bounded context follows a **4-layer structure**:

```
bounded_context/
├── domain/          # Core business logic (aggregates, entities, value objects, commands, queries, events, repository ports)
├── application/     # Command/query services, event handlers, ACL facade implementations, outbound ports
├── infrastructure/  # Adapters for external technologies (JPA, Spring Security, Mercado Pago, AgroMonitoring, Brevo)
└── interfaces/      # Entry points (REST controllers, resources, transformers, ACL facades)
```

The class diagrams of each context are in [`docs/class-diagrams`](class-diagrams), and the C4 model (context, containers, and components) is in [`docs/software-architecture.dsl`](software-architecture.dsl).

### Layer Responsibilities

#### Domain Layer (`domain/`)
- **Aggregates**: `User`, `Profile`, `Cooperative`, `AgronomistAssignment`, `Subscription`, `FieldPlot`, `CropCampaign`, `CampaignLedger`, `PestReport`, `AgroclimaticAlert`, `HarvestBatch`, `QualityCertificate`
- **Entities**: `Role`, `PasswordResetToken`, `CooperativeMember`, `AgronomistInvitation`, `Plan`, `Payment`, `ExpenseEntry`
- **Value Objects**: `FieldPlotId`, `CropCampaignId`, `PlotBoundary`, `GeoCoordinate`, `SeedVariety`, `PlanCode`, `BillingCycle`
- **Repository Ports**: Pure Java interfaces (e.g., `CropCampaignRepository`)
- **Commands and Queries**: `StartCropCampaignCommand`, `RegisterFieldPlotCommand`, `GetFieldPlotByIdQuery`
- **Domain Events**: `CropCampaignStartedEvent`, `PlotBoundaryDelineatedEvent`, `AgroclimaticAlertIssuedEvent`, `BatchGradedEvent`

**Key principle**: Business rules live here. Aggregates validate their invariants and signal errors with message keys (e.g., `crop.campaign.error.finished`) that the interfaces layer later translates.

#### Application Layer (`application/`)
- **Command Services**: Handle write operations and return `Result<T, ApplicationError>`
- **Query Services**: Handle read operations (find, list)
- **Event Handlers**: React to domain events of the same bounded context
- **ACL Facade Implementations**: `FarmingContextFacadeImpl`, `ProfilesContextFacadeImpl`, `SubscriptionsContextFacadeImpl`
- **Outbound Ports**: `PaymentGateway`, `PolygonMonitoringService`, `SatelliteImagery`, `WeatherForecasting`, `EmailSender`, `TokenService`, `HashingService`

#### Infrastructure Layer (`infrastructure/`)
- **Repository Adapters**: `*RepositoryImpl` classes that implement the domain repository ports with Spring Data JPA
- **Persistence Entities, Assemblers, and Converters**: keep JPA out of the domain
- **External Adapters**: `MercadoPagoPaymentGateway`, `AgroMonitoringPolygonService`, `BrevoEmailSender`
- **Security**: `WebSecurityConfiguration`, `BearerAuthorizationRequestFilter`, `TokenServiceImpl`, `HashingServiceImpl`

#### Interfaces Layer (`interfaces/`)
- **REST Controllers**: Expose the 77 HTTP endpoints
- **Resources**: Records for request/response bodies
- **Transformers**: `*CommandFromResourceAssembler` and `*ResourceFromEntityAssembler`
- **Access Components**: `CooperativeAccess`, `CertificationAccess`, `PlanSelectionAccess` check ownership
- **ACL Facades**: Let other bounded contexts use this one without knowing its model

### Dependency Direction

```
interfaces → application → domain ← infrastructure
```

Dependencies point **inward** toward the domain. The domain never depends on outer layers. Bounded contexts never import each other's domain; they only use the other context's facade (see [12.2](#122-acl-facades-between-bounded-contexts)).

### SOLID Principles in This Architecture

This architecture embodies the **SOLID principles** defined by **Robert C. Martin** ([article](https://blog.cleancoder.com/uncle-bob/2020/10/18/Solid-Relevance.html)):

| Principle | Author | How this project applies it |
|-----------|--------|-----------------------------|
| **S**ingle Responsibility | Robert C. Martin | `CampaignLedger` owns the cost rules, `CropCampaignRepositoryImpl` owns persistence, `FieldPlotsController` only translates HTTP |
| **O**pen/Closed | Robert C. Martin | A new payment provider is a new `PaymentGateway` adapter; `SubscriptionCommandServiceImpl` does not change |
| **L**iskov Substitution | Barbara Liskov ([paper](https://dl.acm.org/doi/10.1145/62138.62141)) | `BrevoEmailSender` and `DisabledEmailSender` are interchangeable behind `EmailSender` |
| **I**nterface Segregation | Robert C. Martin | Separate `FieldPlotCommandService` and `FieldPlotQueryService`; `SubscriptionsContextFacade` only exposes the four quota methods other contexts need |
| **D**ependency Inversion | Robert C. Martin | `FieldPlotCommandServiceImpl` depends on `FieldPlotRepository` and `SubscriptionsContextFacade` interfaces, not on JPA or on the Subscriptions model |

### Why this matters

- **Testability**: Domain logic and command services are tested without a running database
- **Maintainability**: Changing MySQL, Mercado Pago, or AgroMonitoring does not affect business rules
- **Clarity**: Each layer, and each bounded context, has a single, well-defined responsibility
- **Evolvability**: A bounded context can later be extracted into its own service because it only exposes a facade

---

## 3. Domain-Driven Design Patterns

### 3.1 Aggregate Root

An **aggregate** is a cluster of objects treated as one unit for data changes. Its **root** is the only entry point, so it can guarantee that every business rule holds after each change.

```java
public class CropCampaign extends AbstractDomainAggregateRoot<CropCampaign> {
    private static final String FINISHED_MESSAGE_KEY = "crop.campaign.error.finished";
    private Long id;
    private FieldPlotId fieldPlotId;
    private CropType cropType;
    private LocalDate sowingDate;
    private CampaignStatus status;

    public void recordSowingDate(LocalDate sowingDate) {
        ensureInProgress();
        this.sowingDate = required(sowingDate, "crop.campaign.error.sowingDate.required");
        registerDomainEvent(new SowingDateRecordedEvent(fieldPlotId.fieldPlotId(), sowingDate));
    }

    public void close() {
        ensureInProgress();
        this.status = CampaignStatus.FINISHED;
        registerDomainEvent(new CropCampaignClosedEvent(id));
    }
}
```

The base class comes from the shared kernel and builds on Spring Data's `AbstractAggregateRoot`:

```java
public abstract class AbstractDomainAggregateRoot<T extends AbstractDomainAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {
    protected void registerDomainEvent(Object event) {
        super.registerEvent(event);
    }
}
```

**Why this matters**: Aggregates are the unit of consistency. "A finished campaign accepts no more changes" is enforced in one place (`ensureInProgress()`), so no controller, handler, or future endpoint can bypass it. Without aggregates, the same rule ends up copied (or forgotten) in many services.

**Origin**: From **Eric Evans'** "Domain-Driven Design" (2003) ([book](https://www.informit.com/store/domain-driven-design-tackling-complexity-in-the-heart-9780321125217)), refined by **Vaughn Vernon** ([Effective Aggregate Design](https://www.dddcommunity.org/library/vernon_2011/)).

### 3.2 Entity

An **entity** has an identity that stays the same while its attributes change. `ExpenseEntry` (inside `CampaignLedger`), `CooperativeMember` (inside `Cooperative`), and `Payment` are entities: two expenses with the same amount are still different expenses.

**Why this matters**: Entities let you model things that are tracked over time (a member who leaves the cooperative, a payment that goes from pending to completed) without confusing them with plain values.

**Origin**: From **Eric Evans'** "Domain-Driven Design" (2003) ([book](https://www.informit.com/store/domain-driven-design-tackling-complexity-in-the-heart-9780321125217)).

### 3.3 Value Object

A **value object** has no identity; it is defined by its attributes, it is immutable, and it validates itself. `PlotBoundary` is a polygon that cannot exist in an invalid shape:

```java
public record PlotBoundary(List<GeoCoordinate> vertices) {
    public PlotBoundary {
        if (vertices == null || vertices.isEmpty()) {
            throw new IllegalArgumentException("field.plot.error.boundary.minVertices");
        }
        var openRing = new ArrayList<>(vertices);
        if (openRing.size() > 1 && openRing.getFirst().equals(openRing.getLast())) {
            openRing.removeLast();
        }
        if (openRing.stream().distinct().count() < 3) {
            throw new IllegalArgumentException("field.plot.error.boundary.minVertices");
        }
        if (hasSelfIntersection(openRing)) {
            throw new IllegalArgumentException("field.plot.error.boundary.selfIntersection");
        }
        vertices = List.copyOf(openRing);
    }

    public BigDecimal calculateArea() { /* spherical approximation in hectares */ }
    public List<GeoCoordinate> closedRing() { /* first vertex repeated at the end, as AgroMonitoring expects */ }
}
```

**Why this matters**: Every `PlotBoundary` in the system is valid, so no service needs to re-check "at least 3 vertices, no self-intersection" before calling AgroMonitoring. Behavior that belongs to the value (area, closed ring) lives with it instead of in a utility class.

**Origin**: From **Eric Evans'** "Domain-Driven Design" (2003); see also **Martin Fowler's** [Value Object](https://martinfowler.com/bliki/ValueObject.html).

### 3.4 Repository Pattern

A **repository** gives the domain a collection-like interface for aggregates, hiding how they are stored. The port is in the domain; the adapter is in infrastructure:

```java
// domain/repositories — the port
public interface CropCampaignRepository {
    Optional<CropCampaign> findById(Long id);
    List<CropCampaign> findAllByFieldPlotId(FieldPlotId fieldPlotId);
    boolean existsByFieldPlotIdAndStatus(FieldPlotId fieldPlotId, CampaignStatus status);
    CropCampaign save(CropCampaign cropCampaign);
}

// infrastructure/persistence/jpa/adapters — the adapter
@Repository
public class CropCampaignRepositoryImpl implements CropCampaignRepository {
    private final CropCampaignPersistenceRepository cropCampaignPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public CropCampaign save(CropCampaign cropCampaign) {
        var isNew = cropCampaign.getId() == null;
        var savedEntity = cropCampaignPersistenceRepository.save(
                CropCampaignPersistenceAssembler.toPersistenceFromDomain(cropCampaign));
        var savedCropCampaign = CropCampaignPersistenceAssembler.toDomainFromPersistence(savedEntity);
        domainEventPublisher.publishAndClearEvents(cropCampaign);
        if (isNew) {
            savedCropCampaign.onStarted();
            domainEventPublisher.publishAndClearEvents(savedCropCampaign);
        }
        return savedCropCampaign;
    }
}
```

**Why this matters**: Command services depend on `CropCampaignRepository`, so tests replace it with a Mockito mock and never touch MySQL. Query methods are named in the language of the business (`existsByFieldPlotIdAndStatus`), not of SQL.

**Origin**: From **Martin Fowler's** "Patterns of Enterprise Application Architecture" (2002) ([catalog entry](https://martinfowler.com/eaaCatalog/repository.html)).

### 3.5 Domain Events

A **domain event** records that something meaningful happened in the domain. Aggregates register events; the repository adapter publishes them after saving; handlers in the same bounded context react.

```java
// registered by the aggregate
registerDomainEvent(new CropCampaignClosedEvent(id));

// published by the shared kernel
@Component
public class DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public <T extends AbstractDomainAggregateRoot<T>> void publishAndClearEvents(@Nullable T aggregate) {
        if (aggregate == null) {
            return;
        }
        aggregate.domainEvents().forEach(applicationEventPublisher::publishEvent);
        aggregate.clearDomainEvents();
    }
}

// handled after the transaction commits, so no email is sent for data that rolled back
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
public void on(AgroclimaticAlertIssuedEvent event) {
    if (!NOTIFIED_SEVERITIES.contains(event.getSeverity()) || !emailSender.isEnabled()) {
        return;
    }
    // email the producers of the region through Brevo
}
```

**Why this matters**: The aggregate does not need to know who cares about "alert issued" or "boundary delineated". New reactions (an email, a call to AgroMonitoring) are new handlers, not changes to the aggregate. Choosing the transaction phase matters: `AFTER_COMMIT` for external side effects (`AgronomistInvitedEventHandler`, `AgroclimaticAlertIssuedEventHandler`), `BEFORE_COMMIT` when the reaction must be part of the same change (`BatchGradedEventHandler`).

**Origin**: Described by **Martin Fowler** ([Domain Event](https://martinfowler.com/eaaDev/DomainEvent.html)) and added to DDD by **Eric Evans** and **Vaughn Vernon**; see the [Spring transaction-bound events documentation](https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html).

### 3.6 Factory Methods

Aggregates have two ways to be created: a constructor from a command (a **new** campaign, which raises events) and a static `reconstitute(...)` method (an **existing** campaign loaded from the database, which must not raise events again).

```java
public CropCampaign(StartCropCampaignCommand command) {
    this(new FieldPlotId(command.plotId()), command.season(), command.cropType(), command.seedVariety(),
            command.customSeedVariety(), command.sowingDate());
    if (sowingDate != null) {
        registerDomainEvent(new SowingDateRecordedEvent(fieldPlotId.fieldPlotId(), sowingDate));
    }
}

public static CropCampaign reconstitute(Long id, FieldPlotId fieldPlotId, String season, CropType cropType,
                                        SeedVariety seedVariety, boolean seedVarietyCustom,
                                        LocalDate sowingDate, CampaignStatus status) {
    var cropCampaign = new CropCampaign(fieldPlotId, season, cropType, seedVariety, seedVarietyCustom, sowingDate);
    cropCampaign.id = id;
    cropCampaign.status = required(status, "crop.campaign.error.status.required");
    return cropCampaign;
}
```

**Why this matters**: Without this split, loading a campaign from MySQL would fire `SowingDateRecordedEvent` every time it is read. Named factories make the intent explicit and keep public setters out of the aggregate.

**Origin**: **Factory Method** from the **Gang of Four** "Design Patterns" (1994) ([book](https://www.oreilly.com/library/view/design-patterns-elements/0201633612/)); "static factory methods" from **Joshua Bloch's** "Effective Java" ([book](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)).

### 3.7 Specification & Validation in Commands

Commands validate their own shape in the compact constructor of the record, before any service runs:

```java
public record StartCropCampaignCommand(Long plotId, String season, CropType cropType, SeedVariety seedVariety,
                                       boolean customSeedVariety, LocalDate sowingDate) {
    public StartCropCampaignCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.plotId.invalid");
        }
        if (season == null || !season.matches("^\\d{4}-(I|II)$")) {
            throw new IllegalArgumentException("crop.campaign.error.season.invalid");
        }
    }
}
```

**Why this matters**: An invalid command (a season like `2026-III`) can never reach the domain. The message key is translated to English or Spanish later, so the same validation serves both languages.

**Origin**: The **Specification** pattern by **Eric Evans and Martin Fowler** ([paper](https://martinfowler.com/apsupp/spec.pdf)); "parse, don't validate" ideas are explained by **Alexis King** ([article](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/)).

---

## 4. CQRS: Command Query Responsibility Segregation

### What is CQRS?

**CQRS** separates the operations that change state (commands) from the operations that read state (queries), each with its own model and service.

**Why this matters**: Writes in SumaqAgro carry heavy rules (quotas, ownership, locks, events); reads are simple lookups for screens. Mixing both in one service makes every read pay for the complexity of writes, and every change to a screen risk breaking a business rule.

**Origin**: Coined by **Greg Young** (2009) ([talk](https://www.infoq.com/presentations/greg-young-unshackle-qcon08/)), built on **Bertrand Meyer's** Command-Query Separation ([Fowler's summary](https://martinfowler.com/bliki/CommandQuerySeparation.html)) and popularized by **Udi Dahan** ([article](https://udidahan.com/2009/12/09/clarified-cqrs/)); see also [Fowler's CQRS article](https://martinfowler.com/bliki/CQRS.html).

### How this project implements CQRS

| Side     | Interface                 | Input                                             | Output                              |
|----------|---------------------------|---------------------------------------------------|-------------------------------------|
| Commands | `FieldPlotCommandService` | `RegisterFieldPlotCommand`, `DelineatePlotBoundaryCommand` | `Result<Long, ApplicationError>`   |
| Queries  | `FieldPlotQueryService`   | `GetFieldPlotByIdQuery`, `GetAllFieldPlotsByOwnerUserIdQuery` | `Optional<FieldPlot>`, `List<FieldPlot>` |

A command service orchestrates the use case. Here, registering a plot checks the plan quota through the Subscriptions facade inside one transaction:

```java
private Result<Long, ApplicationError> registerWithinTransaction(RegisterFieldPlotCommand command) {
    boolean cooperativePlot = command.cooperativeId() != null;
    var limit = cooperativePlot
            ? subscriptions.lockCooperativePlotLimit(command.ownerUserId())
            : subscriptions.lockSeedPlotLimit(command.ownerUserId());
    if (limit.isEmpty()) {
        return Result.failure(ApplicationError.businessRuleViolation("plot-plan-required", PLAN_REQUIRED_REASON));
    }
    if (fieldPlotRepository.existsByOwnerUserIdAndName(command.ownerUserId(), command.name())) {
        return Result.failure(ApplicationError.conflict(FIELD_PLOT, DUPLICATE_NAME_REASON));
    }
    long registered = cooperativePlot
            ? fieldPlotRepository.countByCooperativeId(command.cooperativeId())
            : fieldPlotRepository.countByOwnerUserId(command.ownerUserId());
    if (registered >= limit.get()) {
        return Result.failure(ApplicationError.businessRuleViolation("plot-quota", QUOTA_REACHED_REASON));
    }
    var fieldPlot = fieldPlotRepository.save(new FieldPlot(command));
    return Result.success(fieldPlot.getId());
}
```

### The Result Pattern

Expected failures are values, not exceptions. `Result` is a sealed interface with two records:

```java
public sealed interface Result<T, E> {
    record Success<T, E>(T value) implements Result<T, E> { }
    record Failure<T, E>(E error) implements Result<T, E> { }

    static <T, E> Result<T, E> success(T value) { return new Success<>(value); }
    static <T, E> Result<T, E> failure(E error) { return new Failure<>(error); }

    default Optional<T> toOptional() {
        return switch (this) {
            case Success<T, E> s -> Optional.of(s.value);
            case Failure<T, E> f -> Optional.empty();
        };
    }
}
```

It also offers `map`, `flatMap`, `mapError`, `recover`, and `getOrElse` to compose results without `if` chains.

**Why this matters**: "Plot quota reached" is not exceptional; it is a normal answer. With `Result`, the method signature says that the call can fail, the compiler forces callers to handle both cases in a `switch`, and no stack trace is built for an expected outcome.

**Origin**: Inspired by the `Either` type of functional languages such as Haskell ([docs](https://hackage.haskell.org/package/base/docs/Data-Either.html)) and Rust's `Result` ([docs](https://doc.rust-lang.org/std/result/)); **Scott Wlaschin** calls the style "Railway Oriented Programming" ([article](https://fsharpforfunandprofit.com/rop/)).

### ApplicationError Factory Methods

```java
ApplicationError.validationError("FieldPlot", "field.plot.error.boundary.minVertices"); // VALIDATION_ERROR → 400
ApplicationError.notFound("FieldPlot", "42");                                          // FIELDPLOT_NOT_FOUND → 404
ApplicationError.conflict("FieldPlot", "A plot with this name already exists");        // FIELDPLOT_CONFLICT → 409
ApplicationError.businessRuleViolation("plot-quota", "The plan plot quota is reached"); // BUSINESS_RULE_VIOLATION → 422
ApplicationError.unexpected("confirm-payment", "Mercado Pago could not be reached");   // UNEXPECTED_ERROR → 500
```

**Why this matters**: Error codes are built in one place, so the mapping to HTTP status codes (section 8.3) is consistent across the 77 endpoints.

---

## 5. Modern Java Features (Java 21+)

The project targets **Java 27** (`<java.version>27</java.version>` in `pom.xml`) and uses the language features below.

### 5.1 Records (Java 16+)

Commands, queries, resources, events, and many value objects are records: `StartCropCampaignCommand`, `PlotBoundary`, `GeoCoordinate`, `PlanResource`, `PaymentGateway.CheckoutRequest`.

**Why this matters**: Records are immutable, have value-based `equals`/`hashCode`, and remove boilerplate, so a command cannot be modified after it is validated.

**Origin**: [JEP 395: Records](https://openjdk.org/jeps/395).

### 5.2 Sealed Interfaces (Java 17+)

`Result<T, E>` is `sealed`: only `Success` and `Failure` can implement it.

**Why this matters**: The compiler knows every possible subtype, so a `switch` over a `Result` without a `default` branch is checked for completeness.

**Origin**: [JEP 409: Sealed Classes](https://openjdk.org/jeps/409).

### 5.3 Pattern Matching for Switch (Java 21+)

```java
return switch (provider.status()) {
    case PENDING -> Result.success(payment);
    case REJECTED -> {
        var rejected = payment.reject(provider.providerPaymentId());
        paymentRepository.update(rejected);
        yield Result.success(rejected);
    }
    case APPROVED -> approve(payment, provider);
};
```

`ResponseEntityAssembler` uses type patterns (`case Result.Success<T, ApplicationError> success ->`), and `ProblemDetailFromApplicationErrorAssembler` uses guarded patterns (`case String s when s.endsWith("_NOT_FOUND") ->`).

**Why this matters**: Exhaustive switches turn "I forgot a payment status" into a compile error instead of a production bug.

**Origin**: [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) and [JEP 440: Record Patterns](https://openjdk.org/jeps/440).

### 5.4 Virtual Threads — Project Loom (Java 21)

```properties
spring.threads.virtual.enabled=true
```

```java
@Configuration
@EnableAsync
public class VirtualThreadsConfiguration {
    @Bean(name = "applicationTaskExecutor")
    public TaskExecutor applicationTaskExecutor() {
        var executor = new SimpleAsyncTaskExecutor("loom-exec-");
        executor.setVirtualThreads(true);
        return executor;
    }
}
```

**Why this matters**: Many requests wait on I/O (MySQL, Mercado Pago, AgroMonitoring, Brevo). Virtual threads make waiting cheap, so the server handles many concurrent requests without a reactive programming model.

**Origin**: [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) from [Project Loom](https://openjdk.org/projects/loom/).

### 5.5 Sequenced Collections (Java 21)

`PlotBoundary` uses `getFirst()`, `getLast()`, and `removeLast()` to detect and normalize closed rings, and `closedRing()` uses `getFirst()` to close it again for AgroMonitoring.

**Why this matters**: Intent is clear (`getLast()` instead of `get(size() - 1)`), and off-by-one errors disappear.

**Origin**: [JEP 431: Sequenced Collections](https://openjdk.org/jeps/431).

### 5.6 Null-Safety Annotations (JSpecify)

Shared kernel classes are annotated with `@NullMarked`, and parameters that accept `null` are marked `@Nullable` (e.g., `publishAndClearEvents(@Nullable T aggregate)`).

**Why this matters**: Nullability becomes part of the API contract, and IDEs and Spring Framework 7 can warn you before a `NullPointerException` happens.

**Origin**: [JSpecify](https://jspecify.dev/docs/start-here/), adopted by [Spring Framework 7 null-safety](https://docs.spring.io/spring-framework/reference/core/null-safety.html).

### 5.7 `var`, Text Blocks, and Generic Methods

`var` keeps local variables readable (`var limit = subscriptions.lockSeedPlotLimit(...)`), and generic methods with bounded types (`<T extends AbstractDomainAggregateRoot<T>>`) let one `DomainEventPublisher` serve every aggregate.

**Origin**: [JEP 286: Local-Variable Type Inference](https://openjdk.org/jeps/286) and [JEP 378: Text Blocks](https://openjdk.org/jeps/378).

---

## 6. Spring Boot Framework Patterns

### 6.1 Dependency Injection

Every service receives its dependencies through the constructor:

```java
public FieldPlotCommandServiceImpl(FieldPlotRepository fieldPlotRepository,
                                   SubscriptionsContextFacade subscriptions,
                                   ProfilesContextFacade profiles,
                                   TransactionTemplate transaction) { ... }
```

**Why this matters**: Dependencies are explicit, fields can be `final`, and tests create the class with mocks without starting Spring.

**Origin**: Named by **Martin Fowler** (2004) ([article](https://martinfowler.com/articles/injection.html)); see the [Spring IoC container documentation](https://docs.spring.io/spring-framework/reference/core/beans.html).

### 6.2 Component Scanning & Stereotype Annotations

`@RestController` (controllers), `@Service` (command, query, and facade services), `@Repository` (repository adapters), `@Component` (access checks, event publisher), and `@Configuration` (security, OpenAPI, i18n, Loom) tell Spring what each class is.

**Why this matters**: The stereotype documents the layer of the class and enables layer-specific behavior (e.g., exception translation for `@Repository`).

**Origin**: [Spring stereotype annotations](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html).

### 6.3 Profile-Based Configuration

```properties
# application.properties (shared)
spring.profiles.active=dev
# application-dev.properties (local MySQL, demo payments, email disabled)
# application-prod.properties (Render: secrets read from environment variables)
```

Secrets such as `JWT_SECRET`, the Mercado Pago access token, the AgroMonitoring key, and the Brevo key are read from environment variables, never committed.

**Why this matters**: The same build runs locally and on Render; only configuration changes. Features like the demo checkout exist only where they are enabled (`@ConditionalOnProperty(name = "subscriptions.demo-payments.enabled", havingValue = "true")`).

**Origin**: [Spring Boot externalized configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html) and [The Twelve-Factor App: Config](https://12factor.net/config).

### 6.4 Maven Resource Filtering

`documentation.application.version=@project.version@` copies the version from `pom.xml` into the OpenAPI document at build time.

**Origin**: [Maven resource filtering](https://maven.apache.org/plugins/maven-resources-plugin/examples/filter.html).

### 6.5 SpringDoc OpenAPI

Every controller is documented with `@Operation`, `@ApiResponses`, `@Parameter`, and `@Schema`, and the documentation is served at `/swagger-ui.html`.

**Why this matters**: The frontend team (and your teacher) can try every endpoint without reading Java code, and the documentation cannot drift from the code because it is generated from it.

**Origin**: [springdoc-openapi](https://springdoc.org/) implementing the [OpenAPI Specification](https://spec.openapis.org/oas/latest.html).

### 6.6 Lombok

`@Getter` on aggregates and `@Getter @Setter @NoArgsConstructor` on JPA entities remove boilerplate. Aggregates intentionally have **no** `@Setter`.

**Origin**: [Project Lombok](https://projectlombok.org/features/).

---

## 7. Spring Security & JWT Authentication

### 7.1 Stateless JWT Authentication

```java
return http
        .cors(configurer -> configurer.configurationSource(request -> cors))
        .csrf(configurer -> configurer.disable())
        .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(configurer ->
                configurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/tokens").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/public/traceability").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/payment-notifications").permitAll()
                .anyRequest().authenticated())
        .addFilterBefore(bearerFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
```

```java
public String generateToken(String email) {
    var issuedAt = Instant.now();
    var expiration = issuedAt.plus(expirationMinutes, ChronoUnit.MINUTES);
    return Jwts.builder()
            .subject(email)
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiration))
            .signWith(getSigningKey())
            .compact();
}
```

**Why this matters**: No session is stored on the server, so any instance on Render can answer any request. The public endpoints are listed explicitly; everything else is closed by default.

**Origin**: JWT is defined in [RFC 7519](https://www.rfc-editor.org/rfc/rfc7519.html) (2015) and Bearer tokens in [RFC 6750](https://www.rfc-editor.org/rfc/rfc6750.html); see [Spring Security architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html) and [JJWT](https://github.com/jwtk/jjwt).

### 7.2 BCrypt Password Hashing

`HashingServiceImpl` implements `BCryptHashingService` (which extends the `HashingService` port and Spring Security's `PasswordEncoder`) with `BCryptPasswordEncoder`. Password reset tokens are random and URL-safe, expire, can be used once, and only their SHA-256 digest is stored (`SecurePasswordResetTokenCodec`).

**Why this matters**: BCrypt is slow on purpose and salted, so a leaked database does not reveal passwords. Answering `202 Accepted` to every reset request prevents attackers from discovering which emails have accounts.

**Origin**: BCrypt by **Niels Provos & David Mazières** (1999) ([paper](https://www.usenix.org/legacy/events/usenix99/provos/provos.pdf)); see the [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html).

### 7.3 Method-Level Security and Ownership Checks

Roles are checked declaratively; ownership is checked by access components:

```java
@PreAuthorize("hasRole('COOPERATIVE_DIRECTOR')")
@PostMapping("/{cooperativeId}/invitations")
public ResponseEntity<?> inviteAgronomist(...) { ... }

@Component
public class CooperativeAccess {
    boolean manages(UserDetailsImpl principal, Long cooperativeId) {
        return principal != null && cooperativeId != null
                && cooperatives.handle(new GetCooperativeByIdQuery(cooperativeId))
                .map(cooperative -> cooperative.getManagerUserId().equals(principal.getId()))
                .orElse(false);
    }
}
```

**Why this matters**: "Is a director" is not the same as "is the director of this cooperative". Checking ownership prevents one cooperative from reading or changing another's members, batches, or certificates (the classic [Broken Object Level Authorization](https://owasp.org/API-Security/editions/2023/en/0xa1-broken-object-level-authorization/) risk).

**Origin**: [Spring Security method security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html); see the [OWASP API Security Top 10](https://owasp.org/API-Security/).

---

## 8. REST API Design & Best Practices

### 8.1 Resource-Oriented Design

Endpoints are nouns in plural, and state transitions are sub-resources created with `POST`:

| Action                    | Endpoint                                                      |
|---------------------------|---------------------------------------------------------------|
| Register a plot           | `POST /api/v1/field-plots`                                    |
| Save its polygon          | `PUT /api/v1/field-plots/{fieldPlotId}/polygon`               |
| Close a campaign          | `POST /api/v1/crop-campaigns/{cropCampaignId}/closures`       |
| Revoke a certificate      | `POST /api/v1/quality-certificates/{id}/revocation`           |
| Current user's resources  | `GET /api/v1/subscriptions/me`, `GET /api/v1/subscriptions/me/payments` |

**Why this matters**: Predictable URLs make the API easy to learn and to secure by path. Modeling transitions as resources ("a closure") keeps HTTP verbs honest instead of inventing `/closeCampaign` endpoints.

**Origin**: REST by **Roy Fielding** (2000) ([dissertation, chapter 5](https://ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm)); HTTP semantics in [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html).

### 8.2 Versioning

Every path starts with `/api/v1`, so a future breaking change can live under `/api/v2` without breaking the deployed web application.

### 8.3 RFC 9457 Problem Details

```java
public static HttpStatusCode toStatusFromErrorCode(String errorCode) {
    return switch (errorCode) {
        case "INVALID_CREDENTIALS", "AUTHENTICATION_REQUIRED" -> HttpStatus.UNAUTHORIZED;
        case "ACCESS_DENIED" -> HttpStatus.FORBIDDEN;
        case "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
        case String s when s.endsWith("_NOT_FOUND") -> HttpStatus.NOT_FOUND;
        case "BUSINESS_RULE_VIOLATION" -> HttpStatusCode.valueOf(422);
        case String s when s.endsWith("_CONFLICT") -> HttpStatus.CONFLICT;
        default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
}
```

**Why this matters**: The web application has one error handler for every endpoint: it reads `code` to decide what to do and shows `detail`, which already comes in the user's language.

**Origin**: [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457.html) (2023), supported natively by [Spring's `ProblemDetail`](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html).

### 8.4 The Transformer Pattern

Resources never reach the domain. Each direction has its own static assembler:

```java
public final class RegisterFieldPlotCommandFromResourceAssembler {
    public static RegisterFieldPlotCommand toCommandFromResource(RegisterFieldPlotResource resource) {
        Objects.requireNonNull(resource, "Register field plot request body is required.");
        return new RegisterFieldPlotCommand(resource.ownerUserId(), resource.name(), resource.region(),
                resource.areaHectares(), resource.cooperativeId(), resource.cooperativeMemberId());
    }
}
```

The opposite direction (`FieldPlotResourceFromEntityAssembler`) decides what is exposed; for example, `ProfileResource` only includes the email, phone, and DNI for the owner of the profile.

**Why this matters**: The API contract can change without changing the domain, and private fields are never exposed by accident.

**Origin**: The **Data Transfer Object** and **Assembler** patterns from **Martin Fowler's** PoEAA ([DTO](https://martinfowler.com/eaaCatalog/dataTransferObject.html)).

### 8.5 ResponseEntityAssembler

```java
public static <T, R> ResponseEntity<?> toResponseEntityFromResult(
        Result<T, ApplicationError> result,
        Function<T, R> successResourceAssembler,
        HttpStatusCode successStatus) {
    return switch (result) {
        case Result.Success<T, ApplicationError> success ->
                new ResponseEntity<>(successResourceAssembler.apply(success.value()), successStatus);
        case Result.Failure<T, ApplicationError> failure ->
                ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(failure.error());
    };
}
```

**Why this matters**: Controllers stay thin. They authorize, transform, call the service, and hand the `Result` to this assembler; the success/error branching is written once.

---

## 9. Persistence with Spring Data JPA

### 9.1 Separation of Domain and Persistence Models

| Model       | Example                          | Package                                     |
|-------------|----------------------------------|---------------------------------------------|
| Domain      | `CropCampaign`                   | `farming.domain.model.aggregates`           |
| Persistence | `CropCampaignPersistenceEntity`  | `farming.infrastructure.persistence.jpa.entities` |
| Mapping     | `CropCampaignPersistenceAssembler` | `farming.infrastructure.persistence.jpa.assemblers` |
| Spring Data | `CropCampaignPersistenceRepository` | `farming.infrastructure.persistence.jpa.repositories` |

**Why this matters**: The aggregate has no JPA annotations, no public no-arg constructor, and no setters, so the database cannot dictate the shape of the domain.

### 9.2 JPA Entity Pattern

```java
@Entity
@Table(name = "crop_campaigns")
@Getter
@Setter
@NoArgsConstructor
public class CropCampaignPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "crop_type", nullable = false, length = 30)
    private CropType cropType;

    @Convert(converter = SeedVarietyPersistenceConverter.class)
    @Column(name = "seed_variety", nullable = false, length = 60)
    private SeedVariety seedVariety;
}
```

**Origin**: [Jakarta Persistence](https://jakarta.ee/specifications/persistence/) and [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/).

### 9.3 Auditable Base Entity

`AuditableAbstractPersistenceEntity` holds the id and the `createdAt`/`updatedAt` columns filled by Spring Data auditing (`JpaAuditingConfiguration`).

**Origin**: [Spring Data JPA auditing](https://docs.spring.io/spring-data/jpa/reference/auditing.html).

### 9.4 Custom Physical Naming Strategy

`SnakeCaseWithPluralizedTablePhysicalNamingStrategy` turns Java names into snake_case columns and pluralized table names (using the `pluralize` library).

**Why this matters**: Database names follow one convention everywhere, without writing `@Column(name = ...)` for every field.

**Origin**: [Hibernate naming strategies](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#naming).

### 9.5 Attribute Converters

```java
@Converter
public class PlotBoundaryPersistenceConverter implements AttributeConverter<PlotBoundary, String> {
    @Override
    public String convertToDatabaseColumn(PlotBoundary attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.vertices().stream()
                .map(vertex -> vertex.latitude() + "," + vertex.longitude())
                .collect(Collectors.joining(";"));
    }

    @Override
    public PlotBoundary convertToEntityAttribute(String dbData) {
        // parses "lat,lon;lat,lon;..." back into a validated PlotBoundary
    }
}
```

**Why this matters**: Value objects are stored without leaking JPA into them, and they are validated again when read back.

**Origin**: [Jakarta Persistence `AttributeConverter`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/attributeconverter).

### 9.6 Pessimistic Locking for Quotas and Payments

`SubscriptionPersistenceRepository` and `PaymentPersistenceRepository` use `@Lock(LockModeType.PESSIMISTIC_WRITE)`. The Subscriptions facade requires an existing transaction:

```java
@Override
@Transactional(propagation = Propagation.MANDATORY)
public Optional<Integer> lockSeedPlotLimit(long userId) {
    return findCurrentPlanForUpdate(userId, PlanCode.SEED).map(Plan::getRegisteredPlotLimit);
}
```

**Why this matters**: Two simultaneous "register plot" requests for the same producer are serialized, so the quota cannot be exceeded; two Mercado Pago notifications for the same payment cannot activate the subscription twice.

**Origin**: **Pessimistic Offline Lock** from **Martin Fowler's** PoEAA ([catalog entry](https://martinfowler.com/eaaCatalog/pessimisticOfflineLock.html)); see [Spring Data JPA locking](https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html).

---

## 10. External Integrations: Mercado Pago, AgroMonitoring, Brevo

All three providers follow the same rule: **a port in the application layer, an adapter in infrastructure**.

| Provider        | Port (application)                                   | Adapter (infrastructure)                                   |
|-----------------|------------------------------------------------------|------------------------------------------------------------|
| Mercado Pago    | `PaymentGateway`                                     | `MercadoPagoPaymentGateway`                                |
| AgroMonitoring  | `PolygonMonitoringService`, `SatelliteImagery`, `WeatherForecasting` | `AgroMonitoringPolygonService`, `AgroMonitoringSatelliteImagery`, `AgroMonitoringWeatherForecast` |
| Brevo           | `EmailSender` (shared), `PasswordResetEmailSender` (IAM) | `BrevoEmailSender`, `DisabledEmailSender`, `BrevoPasswordResetEmailSender` |

### 10.1 Mercado Pago Checkout Pro

```java
public interface PaymentGateway {
    CheckoutSession createCheckout(CheckoutRequest request);
    Optional<ProviderPayment> findPayment(String providerPaymentId);

    record CheckoutRequest(String reference, String title, BigDecimal amount, String currency) { }
    record CheckoutSession(String providerCheckoutId, URI checkoutUrl, URI sandboxCheckoutUrl) { }
    enum ProviderPaymentStatus { APPROVED, PENDING, REJECTED }
}
```

The flow is: the backend creates a preference and returns `checkoutUrl` → the browser is redirected to Mercado Pago → Mercado Pago calls the webhook (`POST /api/v1/payment-notifications`) → the backend **reads the payment back** with `findPayment(...)`, locks the local payment, and only then activates the subscription.

**Why this matters**: The webhook is public, so its body cannot be trusted. Re-reading the payment from Mercado Pago with the server's access token makes forged notifications useless, and the lock makes repeated notifications harmless (idempotency).

**Origin**: [Mercado Pago Checkout Pro](https://www.mercadopago.com.pe/developers/en/docs/checkout-pro/overview) and [webhook notifications](https://www.mercadopago.com.pe/developers/en/docs/your-integrations/notifications/webhooks); the port/adapter split is **Alistair Cockburn's** [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/).

### 10.2 AgroMonitoring

When a boundary is delineated, `PlotBoundaryDelineatedEventHandler` registers the polygon through `PolygonMonitoringService` and stores the returned polygon id in the plot. Crop Health later asks for NDVI/NDWI imagery and the 5-day forecast with that id.

**Why this matters**: The domain works with `PlotBoundary` and `SatelliteObservation`, not with AgroMonitoring's JSON; if the provider changes, only the adapter changes.

**Origin**: [AgroMonitoring API documentation](https://agromonitoring.com/api); NDVI is explained by [NASA Earth Observatory](https://earthobservatory.nasa.gov/features/MeasuringVegetation).

### 10.3 Brevo Transactional Email

`BrevoEmailSender` posts JSON to Brevo over HTTPS with Spring's `RestClient`. When no API key is configured, `DisabledEmailSender` is used, so local development and tests send nothing.

**Why this matters**: Email is a side effect that should never break a business operation (see the `AFTER_COMMIT` handlers in 3.5).

**Origin**: [Brevo transactional email API](https://developers.brevo.com/docs/send-a-transactional-email) and [Spring `RestClient`](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html).

---

## 11. Testing Strategies

### 11.1 Domain Tests (Pure Unit Tests)

```java
class CropCampaignTests {
    private static CropCampaign startedCampaign(LocalDate sowingDate) {
        return new CropCampaign(new StartCropCampaignCommand(1L, "2026-I", CropType.ANDEAN_POTATO,
                new SeedVariety("Yungay"), false, sowingDate));
    }

    @Test
    void finishedCampaignRejectsChangesWithTheFinishedMessageKey() {
        // Arrange
        var campaign = startedCampaign(null);
        campaign.close();
        // Act
        var exception = assertThrows(IllegalStateException.class,
                () -> campaign.selectCropType(CropType.SPECIALTY_COFFEE));
        // Assert
        assertEquals("crop.campaign.error.finished", exception.getMessage());
    }
}
```

**Why this matters**: No Spring context and no database: these tests run in milliseconds and pin down the business rules. Other examples: `FieldPlotTests`, `CampaignLedgerTests`, `CooperativeTests`, `AgronomistAssignmentTests`, `CertificationValueObjectsTests`.

### 11.2 Application and Infrastructure Tests (Mocking Tests)

```java
@Test
void changingAMissingPlotIsNotFound() {
    when(fieldPlotRepository.findById(1L)).thenReturn(Optional.empty());
    var result = service.handle(new LinkPlotPolygonCommand(1L, "polygon-1"));
    assertEquals("FIELDPLOT_NOT_FOUND", errorOf(result).code());
}

@Test
void anythingNotFinalStaysPending() {
    for (var status : new String[]{"pending", "in_process", "authorized", "in_mediation", "unknown"}) {
        assertEquals(ProviderPaymentStatus.PENDING, MercadoPagoPaymentGateway.toStatus(status));
    }
}
```

`FieldPlotCommandServiceImplTests`, `SubscriptionCommandServiceImplTests`, and the event handler tests mock repositories, facades, and providers. `GlobalExceptionHandlerWebMvcTests` checks the HTTP layer with a standalone `MockMvc`.

**Why this matters**: Each test isolates one class, so a failure points to one place. External providers are never called from tests.

**Origin**: **Arrange-Act-Assert** by **Bill Wake** ([article](https://xp123.com/3a-arrange-act-assert/)); **Test Double** vocabulary by **Gerard Meszaros** ([xUnit Patterns](http://xunitpatterns.com/Test%20Double.html)); see **Martin Fowler's** [Mocks Aren't Stubs](https://martinfowler.com/articles/mocksArentStubs.html).

### 11.3 Testing Tools

| Tool | Use in this project |
|------|---------------------|
| [JUnit 5](https://junit.org/junit5/docs/current/user-guide/) | Test runner and assertions |
| [Mockito](https://site.mockito.org/) | Mocks for repositories, facades, and providers |
| [AssertJ](https://assertj.github.io/doc/) | Fluent assertions (via `spring-boot-starter-test`) |
| [Spring Boot Test](https://docs.spring.io/spring-boot/reference/testing/index.html) | `MockMvc` web tests and the application context test |

---

## 12. Cross-Cutting Concerns

### 12.1 Internationalization (i18n)

```properties
# messages.properties (English)
error.not-found.message=Resource not found
crop.campaign.error.finished=The campaign is finished and accepts no more changes.

# messages_es.properties (Spanish)
error.not-found.message=Recurso no encontrado
```

`AcceptHeaderLocaleResolver` supports English (default) and Spanish. Domain errors carry message keys, and `GlobalExceptionHandler` / `ProblemDetailFromApplicationErrorAssembler` resolve them with Spring's `MessageSource`.

**Why this matters**: The web application is bilingual; errors arrive already translated, and the domain stays free of user-facing text.

**Origin**: [Spring `MessageSource`](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-messagesource) and the [`Accept-Language` header (RFC 9110)](https://www.rfc-editor.org/rfc/rfc9110.html#name-accept-language).

### 12.2 ACL Facades between Bounded Contexts

```java
public interface SubscriptionsContextFacade {
    Optional<Integer> lockSeedPlotLimit(long userId);
    Optional<Integer> lockCooperativeMemberLimit(long userId);
    Optional<Integer> lockCooperativePlotLimit(long userId);
    Optional<Integer> lockAdvisorPlotLimit(long userId);
}
```

Field Management asks Subscriptions "how many plots may this user have?" and gets an `Integer`, never a `Subscription` aggregate. Crop Health and Profiles wrap the facades they use in outbound services (`ExternalFarmingService`, `ExternalIamService`), which translate the answers into their own language.

**Why this matters**: Each bounded context keeps its own model. Subscriptions can change how plans are stored without breaking Field Management, as long as the facade answers the same question.

**Origin**: **Anti-Corruption Layer** from **Eric Evans'** "Domain-Driven Design" ([Microsoft summary](https://learn.microsoft.com/en-us/azure/architecture/patterns/anti-corruption-layer)); **Facade** from the **Gang of Four** ([refactoring.guru](https://refactoring.guru/design-patterns/facade)).

### 12.3 Domain Event Publishing

Covered in [3.5](#35-domain-events): aggregates register, repository adapters publish through `DomainEventPublisher`, and handlers react in the same bounded context. Lifecycle events (`ApplicationReadyEvent`) seed reference data: roles (`ApplicationReadyEventHandler`) and the plan catalog (`PlanCatalogReadyEventHandler`).

**Origin**: **Observer** pattern from the **Gang of Four** ([refactoring.guru](https://refactoring.guru/design-patterns/observer)); [Spring application events](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events).

### 12.4 Architecture Documentation

- [`docs/adrs.md`](adrs.md): the architecture decisions and their consequences
- [`docs/user-stories.md`](user-stories.md): the API technical stories with acceptance criteria
- [`docs/class-diagrams`](class-diagrams): one PlantUML class diagram per bounded context plus the shared kernel
- [`docs/software-architecture.dsl`](software-architecture.dsl): the C4 model in Structurizr DSL

**Origin**: ADRs by **Michael Nygard** ([article](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)); the **C4 model** by **Simon Brown** ([c4model.com](https://c4model.com/)); [PlantUML](https://plantuml.com/class-diagram) and [Structurizr DSL](https://docs.structurizr.com/dsl).

---

## 13. Recommended Learning Resources

### Architecture & Design Fundamentals

| Resource | Author | Description |
|----------|--------|-------------|
| ["Design Patterns: Elements of Reusable Object-Oriented Software"](https://www.oreilly.com/library/view/design-patterns-elements/0201633612/) | Gang of Four (Gamma, Helm, Johnson, Vlissides) | The foundational book on 23 classic design patterns (Factory, Observer, Strategy, Facade) |
| ["Patterns of Enterprise Application Architecture"](https://martinfowler.com/eaaCatalog/) | Martin Fowler | Catalog of patterns for enterprise apps: Repository, Data Transfer Object, Pessimistic Lock |
| ["Clean Architecture: A Craftsman's Guide to Software Structure and Design"](https://www.oreilly.com/library/view/clean-architecture-a/9780134494272/) | Robert C. Martin (Uncle Bob) | Dependency Rule, SOLID principles, and architectural boundaries |
| ["Clean Code: A Handbook of Agile Software Craftsmanship"](https://www.oreilly.com/library/view/clean-code-a/9780136083238/) | Robert C. Martin (Uncle Bob) | Code-level craftsmanship: naming, functions, classes, error handling |
| ["Refactoring: Improving the Design of Existing Code"](https://martinfowler.com/books/refactoring.html) | Martin Fowler | Systematic techniques for improving code structure without changing behavior |
| ["Head First Design Patterns"](https://www.oreilly.com/library/view/head-first-design/9781492077992/) | Eric Freeman & Elisabeth Robson | Accessible introduction to GoF patterns with visual learning |

### Domain-Driven Design

| Resource | Author | Description |
|----------|--------|-------------|
| ["Domain-Driven Design: Tackling Complexity in the Heart of Software"](https://www.informit.com/store/domain-driven-design-tackling-complexity-in-the-heart-9780321125217) | Eric Evans | The blue book — aggregates, value objects, bounded contexts, anti-corruption layers |
| ["Implementing Domain-Driven Design"](https://www.informit.com/store/implementing-domain-driven-design-9780321834577) | Vaughn Vernon | The red book — practical DDD implementation with Java examples |
| ["Domain-Driven Design Distilled"](https://www.informit.com/store/domain-driven-design-distilled-9780134434421) | Vaughn Vernon | Concise introduction to DDD for beginners |
| ["Learning Domain-Driven Design"](https://www.oreilly.com/library/view/learning-domain-driven-design/9781098100124/) | Vlad Khononov | Modern DDD, strategic and tactical patterns |
| [DDD Reference](https://www.domainlanguage.com/ddd/reference/) | Eric Evans | Free summary of every DDD pattern |

### Architectural Styles

| Resource | Author | Description |
|----------|--------|-------------|
| ["Hexagonal Architecture"](https://alistair.cockburn.us/hexagonal-architecture/) | Alistair Cockburn | Original article introducing Ports and Adapters |
| ["The Clean Architecture"](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) | Robert C. Martin (Uncle Bob) | Dependency inversion and architectural boundaries |
| ["The Onion Architecture"](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/) | Jeffrey Palermo | Domain-centric architecture |
| ["Modular Monolith"](https://www.kamilgrzybek.com/blog/posts/modular-monolith-primer) | Kamil Grzybek | Primer on modular monoliths, the style used by this project |
| ["The C4 model"](https://c4model.com/) | Simon Brown | Context, container, and component diagrams |

### CQRS & Event Sourcing

| Resource | Author | Description |
|----------|--------|-------------|
| ["Clarified CQRS"](https://udidahan.com/2009/12/09/clarified-cqrs/) | Udi Dahan | Blog post that popularized CQRS as a practical pattern |
| ["CQRS"](https://martinfowler.com/bliki/CQRS.html) | Martin Fowler | Short, balanced explanation of when to use CQRS |
| [CQRS Documents](https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf) | Greg Young | Original CQRS write-up by the author of the term |
| ["Enterprise Integration Patterns"](https://www.enterpriseintegrationpatterns.com/) | Gregor Hohpe & Bobby Woolf | Messaging and event-driven patterns |

### SOLID Principles

| Resource | Author | Description |
|----------|--------|-------------|
| ["Agile Software Development, Principles, Patterns, and Practices"](https://www.informit.com/store/agile-software-development-principles-patterns-and-practices-9780135974445) | Robert C. Martin (Uncle Bob) | Comprehensive treatment of SOLID with examples |
| ["Solid Relevance"](https://blog.cleancoder.com/uncle-bob/2020/10/18/Solid-Relevance.html) | Robert C. Martin (Uncle Bob) | Why SOLID still matters |
| ["Data Abstraction and Hierarchy"](https://dl.acm.org/doi/10.1145/62138.62141) | Barbara Liskov | The paper behind the Liskov Substitution Principle |

### Java Modern Features

| Resource | Author | Description |
|----------|--------|-------------|
| ["Modern Java in Action"](https://www.manning.com/books/modern-java-in-action) | Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft | Streams, functional style, and modern Java |
| ["Effective Java", 3rd Edition](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/) | Joshua Bloch | Static factories, immutability, and API design |
| [JEP 395: Records](https://openjdk.org/jeps/395) | — | JDK enhancement proposal |
| [JEP 409: Sealed Classes](https://openjdk.org/jeps/409) | — | JDK enhancement proposal |
| [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) | — | JDK enhancement proposal |
| [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) | — | JDK enhancement proposal |
| [JEP 431: Sequenced Collections](https://openjdk.org/jeps/431) | — | JDK enhancement proposal |

### Spring Boot

| Resource | Author | Description |
|----------|--------|-------------|
| ["Spring Boot in Action"](https://www.manning.com/books/spring-boot-in-action) | Craig Walls | Practical Spring Boot development |
| [Spring Boot Reference](https://docs.spring.io/spring-boot/index.html) | — | Official reference documentation |
| [Spring Security Reference](https://docs.spring.io/spring-security/reference/) | — | Security configuration guide |
| [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/) | — | Repositories, queries, locking, auditing |
| [Baeldung Tutorials](https://www.baeldung.com/) | — | Practical Spring Boot tutorials |

### REST API Design

| Resource | Author | Description |
|----------|--------|-------------|
| [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html) | — | Problem Details for HTTP APIs |
| [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html) | — | HTTP semantics: methods, status codes, headers |
| ["REST API Design Rulebook"](https://www.oreilly.com/library/view/rest-api-design/9781449317904/) | Mark Massé | Practical REST design rules |
| [OWASP API Security Top 10](https://owasp.org/API-Security/) | OWASP | The most common API security risks |
| ["Building Microservices"](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/) | Sam Newman | API design and service boundaries |

### External Services

| Resource | Author | Description |
|----------|--------|-------------|
| [Mercado Pago Checkout Pro](https://www.mercadopago.com.pe/developers/en/docs/checkout-pro/overview) | Mercado Pago | Preferences, redirects, and test accounts |
| [Mercado Pago Webhooks](https://www.mercadopago.com.pe/developers/en/docs/your-integrations/notifications/webhooks) | Mercado Pago | Payment notifications |
| [AgroMonitoring API](https://agromonitoring.com/api) | OpenWeather / AgroMonitoring | Polygons, satellite imagery, weather |
| [Brevo Transactional Email](https://developers.brevo.com/docs/send-a-transactional-email) | Brevo | Sending transactional emails through the API |

### Testing

| Resource | Author | Description |
|----------|--------|-------------|
| ["xUnit Test Patterns"](http://xunitpatterns.com/) | Gerard Meszaros | Comprehensive catalog of testing patterns |
| ["Growing Object-Oriented Software, Guided by Tests"](https://www.informit.com/store/growing-object-oriented-software-guided-by-tests-9780321503626) | Steve Freeman & Nat Pryce | Test-driven design with mocks |
| ["Working Effectively with Legacy Code"](https://www.oreilly.com/library/view/working-effectively-with/0131177052/) | Michael Feathers | Testing strategies for untested codebases |
| [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) | — | Modern Java testing |
| [Mockito](https://site.mockito.org/) | — | Mocking framework |
| [AssertJ Documentation](https://assertj.github.io/doc/) | — | Fluent assertions guide |

---

## Summary: Key Takeaways

1. **Layered DDD with Ports and Adapters** (Cockburn, Martin, Fowler) keeps the domain pure and independent of MySQL, Spring, and external providers
2. **SOLID Principles** (Robert C. Martin, Barbara Liskov) explain why services depend on interfaces like `FieldPlotRepository` and `PaymentGateway`
3. **DDD patterns** (Evans, Vernon) — aggregates, entities, value objects, repositories, events — model plots, campaigns, cooperatives, and certificates clearly
4. **Bounded contexts and ACL facades** (Evans) let six modules evolve independently inside one modular monolith
5. **Design Patterns** (Gang of Four) — Factory Method, Facade, Observer — underpin the tactical DDD implementation
6. **CQRS** (Greg Young, Udi Dahan, Bertrand Meyer) separates rule-heavy writes from simple reads
7. **Repository Pattern** (Martin Fowler) isolates persistence from domain logic
8. **Result type** (functional programming tradition) makes expected failures explicit and composable
9. **Java 21+ features** (records, sealed types, pattern matching, virtual threads, sequenced collections) make the code safer and more expressive
10. **Dependency Injection** (Martin Fowler) makes dependencies explicit and testable
11. **Stateless JWT + BCrypt + ownership checks** (RFC 7519, Provos & Mazières, OWASP) protect every endpoint
12. **Ports and adapters for external services** (Cockburn) let you trust Mercado Pago payments only after reading them back, and keep AgroMonitoring and Brevo replaceable
13. **Transactional event listeners and pessimistic locks** (Spring, Fowler) keep side effects and quotas consistent
14. **RFC 9457 Problem Details and i18n** give the web application one error format in English and Spanish
15. **Testing by layer** (Meszaros, Fowler): pure unit tests for the domain, mocks for services and adapters

---

> **Tip for your own projects**: Start with the domain model. Identify your aggregates, value objects, and business rules first (for SumaqAgro: a plot needs a valid polygon, a closed campaign accepts no expenses, a batch is certified only once). Then put every external service behind a port, build the application layer, and only at the end write the controllers. This "inside-out" approach ensures your architecture serves your business, not the other way around.
