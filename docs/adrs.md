# Architecture Decision Records (ADRs) — SumaqAgro Platform

This document maintains Architecture Decision Records (ADRs) for the **SumaqAgro Platform** following the [Michael Nygard format](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions).

---

## Index

- [ADR-001: Modular Monolith with Layered Architecture and Domain-Driven Design](#adr-001-modular-monolith-with-layered-architecture-and-domain-driven-design)
- [ADR-002: Command Query Responsibility Segregation (CQRS) Pattern](#adr-002-command-query-responsibility-segregation-cqrs-pattern)
- [ADR-003: Anti-Corruption Layer (ACL) and Context Facades for Cross-Context Integration](#adr-003-anti-corruption-layer-acl-and-context-facades-for-cross-context-integration)
- [ADR-004: Decoupling Domain Aggregates from JPA Persistence Entities](#adr-004-decoupling-domain-aggregates-from-jpa-persistence-entities)
- [ADR-005: Functional Error Handling with Sealed Result Type](#adr-005-functional-error-handling-with-sealed-result-type)
- [ADR-006: Stateless JWT Authentication and Spring Security Integration](#adr-006-stateless-jwt-authentication-and-spring-security-integration)
- [ADR-007: Standardized Technology Packaging Taxonomy for Infrastructure](#adr-007-standardized-technology-packaging-taxonomy-for-infrastructure)
- [ADR-008: Aggregate Invariant Encapsulation and Reconstitution Factory Pattern](#adr-008-aggregate-invariant-encapsulation-and-reconstitution-factory-pattern)
- [ADR-009: Standardized Domain Event Dispatching via DomainEventPublisher](#adr-009-standardized-domain-event-dispatching-via-domaineventpublisher)
- [ADR-010: Aggregate References by Identity (FieldPlotId, CropCampaignId)](#adr-010-aggregate-references-by-identity-fieldplotid-cropcampaignid)
- [ADR-011: Adoption of RFC 9457 Problem Details for REST Error Responses](#adr-011-adoption-of-rfc-9457-problem-details-for-rest-error-responses)
- [ADR-012: Virtual Threads and Concurrency Optimization via Project Loom](#adr-012-virtual-threads-and-concurrency-optimization-via-project-loom)
- [ADR-013: Sequenced Collections in the Plot Boundary Value Object](#adr-013-sequenced-collections-in-the-plot-boundary-value-object)
- [ADR-014: Mercado Pago Checkout Pro behind a PaymentGateway Port, Confirmed Server-Side](#adr-014-mercado-pago-checkout-pro-behind-a-paymentgateway-port-confirmed-server-side)
- [ADR-015: AgroMonitoring Integration behind Outbound Monitoring Ports](#adr-015-agromonitoring-integration-behind-outbound-monitoring-ports)
- [ADR-016: Transactional Email through Brevo behind Email Sender Ports](#adr-016-transactional-email-through-brevo-behind-email-sender-ports)
- [ADR-017: Side Effects after Commit with Transactional Event Listeners](#adr-017-side-effects-after-commit-with-transactional-event-listeners)
- [ADR-018: Plan Quotas Enforced through the Subscriptions ACL with Pessimistic Locks](#adr-018-plan-quotas-enforced-through-the-subscriptions-acl-with-pessimistic-locks)
- [ADR-019: Resource-Level Authorization with @PreAuthorize and Access Components](#adr-019-resource-level-authorization-with-preauthorize-and-access-components)
- [ADR-020: Domain Events Handled inside Their Own Bounded Context](#adr-020-domain-events-handled-inside-their-own-bounded-context)

---

## ADR-001: Modular Monolith with Layered Architecture and Domain-Driven Design

### Status
Accepted

### Context
The platform requires clear boundaries between distinct business domains (identity, cooperative profiles, subscriptions and payments, field management, crop health, harvest certification) while keeping development velocity, transactional simplicity, and a single deployment on Render, without the distributed systems overhead of microservices.

### Decision
We structure the system as a **Modular Monolith** applying **Domain-Driven Design (DDD)** and **Classic 4-Layer Architecture**:
1. **Bounded Contexts**: `iam`, `profiles`, `subscriptions`, `farming` (Field Management), `crop` (Crop Health), `certification` (Harvest Certification), and the `shared` kernel.
2. **Layer Separation**: Each context defines `domain`, `application`, `infrastructure`, and `interfaces`.
3. **Dependency Rule**: Dependencies point inward toward the `domain` layer. Domain classes never depend on application, infrastructure, or interface layers.
4. **Anti-Corruption Layer (ACL)**: Cross-context communication is governed through explicit facades (`IamContextFacade`, `ProfilesContextFacade`, `SubscriptionsContextFacade`, `FarmingContextFacade`).

### Consequences
- **Positive**: Strict domain boundaries, high testability of business logic without database dependencies, clean evolution path toward independent services if needed.
- **Negative**: Requires mapping layers (`*PersistenceAssembler`, `*FromResourceAssembler`, `*ResourceFromEntityAssembler`) between domain models, persistence entities, and REST resources.

---

## ADR-002: Command Query Responsibility Segregation (CQRS) Pattern

### Status
Accepted

### Context
Models optimized for state mutation (invariants, validations, transactional consistency) diverge from models required for presentation and queries. Mixing read and write logic into monolithic service classes leads to bloated controllers and leaked business logic.

### Decision
We implement a lightweight **CQRS** pattern across all bounded contexts:
1. **Command Services**: Dedicated interfaces (`*CommandService`) accepting immutable Java `record` commands (e.g., `RegisterFieldPlotCommand`, `SignInCommand`, `IssueQualityCertificateCommand`) for state-changing operations and returning `Result<T, ApplicationError>`.
2. **Query Services**: Dedicated interfaces (`*QueryService`) accepting immutable Java `record` queries (e.g., `GetFieldPlotByIdQuery`, `GetAllPlansQuery`) for read-only operations and returning domain aggregates or collections.
3. **Immutability**: All commands and queries are immutable Java `record`s that validate themselves in their compact constructors.

### Consequences
- **Positive**: Clear separation of side-effecting operations and read operations, easier optimization of query paths, highly testable service contracts.
- **Negative**: Increases the number of interfaces and record types per aggregate root.

---

## ADR-003: Anti-Corruption Layer (ACL) and Context Facades for Cross-Context Integration

### Status
Accepted

### Context
Bounded contexts need to interact: Crop Health reads plot polygons from Field Management, Field Management checks plot quotas in Subscriptions, Harvest Certification validates cooperatives in Profiles. Allowing direct dependencies between internal repositories across contexts creates tight coupling and violates bounded context autonomy.

### Decision
We implement **Anti-Corruption Layers (ACL)** and **Context Facades** for cross-context collaboration:
1. **Exposed Facades**: Contexts expose a facade in `interfaces.acl` (`FarmingContextFacade`, `ProfilesContextFacade`, `SubscriptionsContextFacade`, with their implementations in `application.acl`, and `IamContextFacade`) that returns primitives or simple records, never aggregates.
2. **Outbound Adapters**: Crop Health and Profiles reach other contexts through outbound services in `application.internal.outboundservices.acl` (`ExternalFarmingService`, `ExternalIamService`); Harvest Certification does it through `ExternalFarmingService` and `ExternalProfilesService`, implemented in `infrastructure.acl`. IAM, Field Management, and the quota checks of Profiles call the other facade directly from the command service, because they only need one or two calls.
3. **Synchronous Calls**: Cross-context collaboration is synchronous and runs inside the caller's transaction, so quota checks and ownership checks are consistent with the change being made.

### Consequences
- **Positive**: Bounded contexts remain autonomous; changes to internal context structures do not break external consumers.
- **Negative**: Requires intermediary facade methods and translation boilerplate in every consuming context.

---

## ADR-004: Decoupling Domain Aggregates from JPA Persistence Entities

### Status
Accepted

### Context
Using JPA annotations directly on Domain Aggregate Roots couples core business logic and invariants to the ORM, the MySQL schema, and no-arg constructor requirements.

### Decision
We strictly separate the **Domain Model** from the **Persistence Model**:
1. **Domain Aggregates**: Pure Java classes extending `AbstractDomainAggregateRoot<T>`, residing in `domain.model.aggregates` with zero JPA annotations.
2. **Persistence Entities**: Reside in `infrastructure.persistence.jpa.entities`, extending `AuditableAbstractPersistenceEntity` and decorated with JPA annotations (`@Entity`, `@Table`, `@Column`).
3. **Persistence Assemblers and Adapters**: Static assemblers (`*PersistenceAssembler`) in `infrastructure.persistence.jpa.assemblers` translate between aggregates and entities, and repository adapters (`*RepositoryImpl`) in `infrastructure.persistence.jpa.adapters` implement the domain repository ports on top of Spring Data repositories.

### Consequences
- **Positive**: Domain models remain pure, expressive, and testable with plain unit tests without Spring or a database.
- **Negative**: Requires bidirectional mapping logic and manual sync between persistence entities and domain aggregates.

---

## ADR-005: Functional Error Handling with Sealed Result Type

### Status
Accepted

### Context
Using runtime exceptions for business rule violations (plot quota reached, batch already graded, payment already applied) creates hidden control flow, stack trace overhead, and uncommunicative method signatures.

### Decision
We introduce a functional, sealed `Result<T, E>` type in `com.dymbia.sumaqagro.platform.shared.application.result`:
1. **Sealed Interface**: `Result<T, E> permits Success, Failure`, supporting exhaustive Java `switch` pattern matching and record deconstruction.
2. **Standardized ApplicationError**: `ApplicationError(code, message, details)` captures business failure reasons without throwing exceptions.
3. **Assembly to HTTP**: `ResponseEntityAssembler` and `ProblemDetailFromApplicationErrorAssembler` transform `Result.Failure` into the right HTTP response.

### Consequences
- **Positive**: Explicit error handling in method signatures, exhaustive pattern matching, no unnecessary stack trace generation.
- **Negative**: Callers must unpack `Result` instances via `map`, `flatMap`, `getOrElse`, or pattern matching instead of relying on global catch blocks.

---

## ADR-006: Stateless JWT Authentication and Spring Security Integration

### Status
Accepted

### Context
The platform requires secure authentication and role-based authorization for producers, cooperative directors, and agronomists, while staying stateless so the API can scale on Render.

### Decision
We implement stateless JWT authentication integrated with Spring Security:
1. **JWT Generation & Validation**: `TokenService` / `TokenServiceImpl` issues and validates HMAC-SHA signed JWTs (the key comes from `authorization.jwt.secret`; JJWT picks HS256/HS384/HS512 from its length) with a configurable expiration in minutes.
2. **Password Hashing**: `HashingService` / `HashingServiceImpl` (through `BCryptHashingService`) uses BCrypt.
3. **Security DSL**: `WebSecurityConfiguration` configures stateless sessions (`SessionCreationPolicy.STATELESS`), method security (`@EnableMethodSecurity`), CORS, and the `BearerAuthorizationRequestFilter`.
4. **Roles**: `ROLE_FARMER`, `ROLE_COOPERATIVE_DIRECTOR`, and `ROLE_AGRONOMIST`, seeded on startup by `ApplicationReadyEventHandler`.
5. **Password Recovery**: single-use, expiring reset tokens encoded by `SecurePasswordResetTokenCodec` and sent by email (see ADR-016).

### Consequences
- **Positive**: Stateless, scalable authentication; standards-compliant Bearer authorization; declarative endpoint protection with `@PreAuthorize`.
- **Negative**: Tokens cannot be revoked before expiration without keeping a token blacklist.

---

## ADR-007: Standardized Technology Packaging Taxonomy for Infrastructure

### Status
Accepted

### Context
Infrastructure components (persistence, security, external APIs, email, concurrency, documentation, internationalization) must be easy to locate across seven modules.

### Decision
We standardize infrastructure packaging following the taxonomy:
`infrastructure.<technology-type>.<manufacturer-technology-name>[.configuration]`

Examples across contexts:
- `shared.infrastructure.persistence.jpa.configuration` (Jakarta Persistence / Hibernate)
- `shared.infrastructure.documentation.openapi.configuration` (SpringDoc OpenAPI)
- `shared.infrastructure.concurrency.loom.configuration` (Project Loom Virtual Threads)
- `shared.infrastructure.i18n.configuration` (Spring Localization)
- `shared.infrastructure.email.brevo` and `iam.infrastructure.email.brevo` (Brevo)
- `iam.infrastructure.authorization.sfs.configuration` (Spring Framework Security)
- `iam.infrastructure.tokens.jwt` and `iam.infrastructure.hashing.bcrypt` (JJWT, BCrypt)
- `subscriptions.infrastructure.payments.mercadopago` (Mercado Pago)
- `farming.infrastructure.agromonitoring.rest` and `crop.infrastructure.agromonitoring.rest` (AgroMonitoring)

### Consequences
- **Positive**: Predictable, consistent navigation across all bounded contexts and the shared kernel.
- **Negative**: Deeper package hierarchies.

---

## ADR-008: Aggregate Invariant Encapsulation and Reconstitution Factory Pattern

### Status
Accepted

### Context
If aggregates (`FieldPlot`, `CropCampaign`, `HarvestBatch`, `Subscription`, `Cooperative`, `User`) exposed public setters, callers could mutate internal state directly and bypass domain invariants such as "a closed campaign does not accept expenses" or "a batch is graded only once".

### Decision
1. No public setters on Aggregate Roots and inner Entities.
2. All state changes pass through expressive domain methods (e.g., `recordSowingDate(...)`, `close(...)`, `gradePotato(...)`, `cupCoffee(...)`, `markCertified()`, `activate(...)`, `addMember(...)`).
3. Static `reconstitute(...)` factory methods let persistence assemblers rehydrate stored state without triggering creation invariants or domain events.

### Consequences
- **Positive**: Aggregates protect their consistency boundaries; invalid state transitions cannot be written by accident.
- **Negative**: Persistence assemblers must pass every persistent field into the reconstitution methods.

---

## ADR-009: Standardized Domain Event Dispatching via DomainEventPublisher

### Status
Accepted

### Context
Aggregates accumulate domain events via `registerDomainEvent(...)` from `AbstractDomainAggregateRoot`. Because they are not Spring Data entities, Spring Data's `@DomainEvents` does not fire on `save()`.

### Decision
1. A `DomainEventPublisher` component lives in `com.dymbia.sumaqagro.platform.shared.infrastructure.events`.
2. Repository adapters inject `DomainEventPublisher` and call `publishAndClearEvents(aggregate)` after persisting the aggregate.
3. Events are published through Spring's `ApplicationEventPublisher` and handled by `*EventHandler` classes in `application.internal.eventhandlers`.

### Consequences
- **Positive**: Uniform event lifecycle in every bounded context; handlers such as `PlotBoundaryDelineatedEventHandler` or `BatchGradedEventHandler` reliably receive the events.
- **Negative**: Repository adapters must coordinate with `DomainEventPublisher` during persistence.

---

## ADR-010: Aggregate References by Identity (FieldPlotId, CropCampaignId)

### Status
Accepted

### Context
Crop campaigns, ledgers, batches, alerts, and inspections all relate to a plot. Holding the `FieldPlot` aggregate inside them would force loading whole aggregates and couple their persistence lifecycles, also across bounded contexts.

### Decision
1. Aggregates reference other aggregates only by identity: `FieldPlotId` and `CropCampaignId` value objects in `farming.domain.model.valueobjects`, and plain `Long` identifiers for aggregates owned by other bounded contexts (user, cooperative, plot ids).
2. Operations that need data from another aggregate receive it from the command service, which loads it through its repository or through an ACL facade.
3. JPA converters in `infrastructure.persistence.jpa.converters` map the identity value objects.

### Consequences
- **Positive**: Enforces aggregate boundary isolation and keeps bounded contexts independent of each other's models.
- **Negative**: Command services coordinate the loading of related data before calling the domain methods.

---

## ADR-011: Adoption of RFC 9457 Problem Details for REST Error Responses

### Status
Accepted

### Context
The Angular web application, Swagger UI, and any future client need one predictable error format with localized messages (English and Spanish).

### Decision
1. `ProblemDetailFromApplicationErrorAssembler` in `shared.interfaces.rest.transform` builds every error response.
2. Application error codes map to type URIs under `/api/v1/errors/{error-slug}` (e.g., `/api/v1/errors/not-found`, `/api/v1/errors/validation-error`).
3. `GlobalExceptionHandler` returns `ResponseEntity<ProblemDetail>` with localized `detail` strings and an `invalidParams` array for validation errors.
4. Assembler naming follows `[Target]From[Source]Assembler`.

### Consequences
- **Positive**: Adherence to IETF standards, native Spring `ProblemDetail` support, localized diagnostics for the web application.
- **Negative**: Clients must parse RFC 9457 fields (`type`, `title`, `status`, `detail`, `code`, `invalidParams`).

---

## ADR-012: Virtual Threads and Concurrency Optimization via Project Loom

### Status
Accepted

### Context
Many requests block on I/O: MySQL queries and calls to AgroMonitoring, Mercado Pago, and Brevo. On platform threads this can starve the Tomcat pool under load.

### Decision
1. Enable `spring.threads.virtual.enabled=true` in `application.properties`.
2. Add `VirtualThreadsConfiguration` in `com.dymbia.sumaqagro.platform.shared.infrastructure.concurrency.loom.configuration`, following the packaging convention of ADR-007.
3. Configure `@EnableAsync` with a virtual-thread-backed `applicationTaskExecutor` bean.

### Consequences
- **Positive**: High concurrency and low memory per request without reactive programming (WebFlux).
- **Negative**: The HikariCP connection pool must be sized to avoid contention.

---

## ADR-013: Sequenced Collections in the Plot Boundary Value Object

### Status
Accepted

### Context
A plot boundary is a polygon that AgroMonitoring expects as a closed ring (first vertex equals last vertex). Checking and closing the ring with index arithmetic is error-prone.

### Decision
1. `PlotBoundary` uses Java 21+ `SequencedCollection` operations (`getFirst()`, `getLast()`) to detect whether the ring is already closed and to close it.
2. The value object validates the minimum number of vertices and the coordinate ranges in its constructor, so an invalid polygon never reaches AgroMonitoring.

### Consequences
- **Positive**: O(1) access to the terminal vertices and a declarative invariant check.
- **Negative**: Requires a modern Java runtime (Java 27).

---

## ADR-014: Mercado Pago Checkout Pro behind a PaymentGateway Port, Confirmed Server-Side

### Status
Accepted

### Context
Subscription plans (Semilla, Cooperativa Pro, Asesor Técnico) are charged in PEN through Mercado Pago Checkout Pro. Webhook notifications can be repeated, arrive out of order, or be forged, and the browser must never see payment credentials.

### Decision
1. The `PaymentGateway` port in `subscriptions.application.internal.outboundservices.payments` defines `createCheckout(...)` and `findPayment(...)`; `MercadoPagoPaymentGateway` in `subscriptions.infrastructure.payments.mercadopago` implements it.
2. Only the backend talks to Mercado Pago. The web application receives the `checkoutUrl` and redirects the browser to it.
3. Each checkout gets a unique reference (`mp-<uuid>`) stored with a pending `Payment`.
4. `PaymentNotificationsController` (webhook) never trusts the notification body: it extracts the payment id and the command service reads the payment back from Mercado Pago with `findPayment(...)` before changing any subscription.
5. The payment row is locked (`PESSIMISTIC_WRITE`) while it is confirmed, so a repeated notification is idempotent. The webhook always answers `200` so Mercado Pago does not retry endlessly.

### Consequences
- **Positive**: Forged or repeated notifications cannot activate a subscription; the provider can be replaced without touching the domain.
- **Negative**: Each notification costs an extra call to Mercado Pago, and local testing needs a public URL for the webhook.

---

## ADR-015: AgroMonitoring Integration behind Outbound Monitoring Ports

### Status
Accepted

### Context
SumaqAgro offers precision agriculture without field hardware: plot polygons, NDVI/NDWI imagery, and 5-day forecasts come from the AgroMonitoring API. The domain must not depend on its JSON format, and the API may be slow or unavailable.

### Decision
1. Field Management defines the `PolygonMonitoringService` port in `application.internal.outboundservices.monitoring`, implemented by `AgroMonitoringPolygonService` in `farming.infrastructure.agromonitoring.rest`. It registers the polygon when a boundary is delineated (`PlotBoundaryDelineatedEventHandler`).
2. Crop Health defines the `SatelliteImagery` and `WeatherForecasting` ports, implemented by `AgroMonitoringSatelliteImagery` and `AgroMonitoringWeatherForecast` in `crop.infrastructure.agromonitoring.rest`.
3. Provider resources (`AgroMonitoringPolygonRequest`, `AgroMonitoringPolygonResponse`) stay in infrastructure; the domain only sees its own value objects.
4. A provider failure is returned as an `ApplicationError` instead of breaking the request.

### Consequences
- **Positive**: The monitoring provider can be replaced or stubbed in tests without touching the domain.
- **Negative**: Two contexts integrate with the same provider through separate adapters.

---

## ADR-016: Transactional Email through Brevo behind Email Sender Ports

### Status
Accepted

### Context
The platform sends three kinds of email: password recovery (IAM), agronomist invitations (Profiles), and serious agroclimatic alerts (Crop Health). Local development and tests must run without a Brevo API key.

### Decision
1. The shared kernel defines the `EmailSender` port in `shared.application.outboundservices.email`, implemented by `BrevoEmailSender` (JSON over HTTPS) and `DisabledEmailSender` when no key is configured. Profiles and Crop Health use it.
2. IAM keeps its own `PasswordResetEmailSender` port, implemented by `BrevoPasswordResetEmailSender`, because the reset link and its template belong to the identity context.
3. User-provided text is HTML-escaped before it is placed in an email.

### Consequences
- **Positive**: The provider is isolated in infrastructure; the application runs with email disabled in development.
- **Negative**: Two Brevo adapters exist (shared and IAM) and must be configured with the same credentials.

---

## ADR-017: Side Effects after Commit with Transactional Event Listeners

### Status
Accepted

### Context
Sending an email inside the transaction that stores an invitation or an alert would send messages for data that may still roll back, and a failing email would undo a valid business change.

### Decision
1. Handlers with external side effects (`AgronomistInvitedEventHandler`, `AgroclimaticAlertIssuedEventHandler`) use `@TransactionalEventListener(phase = AFTER_COMMIT, fallbackExecution = true)`.
2. Handlers that must complete the same business change use `BEFORE_COMMIT` (`BatchGradedEventHandler` assigns the quality score in the grading transaction) or a plain `@EventListener`.
3. An email failure is logged and never undoes the stored invitation or alert; the user still sees it in the application.

### Consequences
- **Positive**: No email is sent for data that was not saved, and external failures do not break business operations.
- **Negative**: An email lost after commit is not retried automatically.

---

## ADR-018: Plan Quotas Enforced through the Subscriptions ACL with Pessimistic Locks

### Status
Accepted

### Context
Each plan limits resources: plots for Semilla, members and plots for Cooperativa Pro, advised plots for Asesor Técnico. Two simultaneous requests could both pass a quota check and exceed the limit.

### Decision
1. `SubscriptionsContextFacade` exposes `lockSeedPlotLimit`, `lockCooperativeMemberLimit`, `lockCooperativePlotLimit`, and `lockAdvisorPlotLimit`.
2. Each method locks the user's subscription row (`PESSIMISTIC_WRITE`) inside the caller's transaction and returns the limit of the active plan, or empty when there is no active plan.
3. Field Management and Profiles count the existing resources and compare against the limit before creating a new one.

### Consequences
- **Positive**: Quotas hold under concurrent requests; the plan rules stay owned by Subscriptions.
- **Negative**: Concurrent creations for the same user are serialized for the duration of the transaction.

---

## ADR-019: Resource-Level Authorization with @PreAuthorize and Access Components

### Status
Accepted

### Context
Roles alone are not enough: a cooperative director may manage only their own cooperative, and an agronomist may only see the plots assigned to them.

### Decision
1. Endpoints declare the required role with `@PreAuthorize("hasRole(...)")`.
2. Ownership checks live in `@Component` access classes next to the controllers (`CooperativeAccess`, `CertificationAccess`, `PlanSelectionAccess`), which use local query services to answer questions such as "does this principal manage this cooperative?".
3. Private profile data is never exposed in public resources (e.g., the public traceability view of a certificate).

### Consequences
- **Positive**: Authorization rules are explicit, close to the endpoints, and testable.
- **Negative**: Each protected resource type needs its own access check.

---

## ADR-020: Domain Events Handled inside Their Own Bounded Context

### Status
Accepted

### Context
Using domain events as the communication channel between contexts would hide dependencies and make it hard to know which context reacts to what.

### Decision
1. Every domain event is handled by a handler of the same bounded context (e.g., `CropCampaignStartedEvent` in Field Management, `PestReportRecordedEvent` in Crop Health, `AgronomistInvitedEvent` in Profiles).
2. When a handler needs data or an action from another context, it calls that context's facade through its outbound ACL service (ADR-003).
3. Application lifecycle events (`ApplicationReadyEvent`) seed reference data: roles (`ApplicationReadyEventHandler`) and the plan catalog (`PlanCatalogReadyEventHandler`).

### Consequences
- **Positive**: Cross-context dependencies are always visible as facade calls; each context is understandable on its own.
- **Negative**: There is no asynchronous integration-event bus; extracting a context into a service would require adding one.
