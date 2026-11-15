# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

The SumaqAgro RESTful API is under construction and has not been released yet. The Web Application keeps using the fake API (json-server) until the endpoints of every bounded context are available.

### Added
- **Project Setup**:
  - Spring Boot 4.1 backend with Java 27, Maven wrapper, Spring Web MVC, Spring Data JPA, Bean Validation, MySQL driver, Lombok, SpringDoc OpenAPI (Swagger UI) and Pluralize.
  - Environment profiles: `application.properties` (common), `application-dev.properties` and `application-prod.properties`, with database credentials and the AgroMonitoring API key read from environment variables.
  - Error messages in English and Spanish (`messages.properties` and `messages_es.properties`).
  - Project documentation: `CHANGELOG.md`, `CONTRIBUTING.md` and the MIT `LICENSE.md`.
- **Shared Kernel**:
  - Sealed `Result<T, E>` type with `Success` and `Failure`, and `fold` to handle both outcomes.
  - `ApplicationError` with a machine-readable code (`FIELDPLOT_NOT_FOUND`, `VALIDATION_ERROR`, `BUSINESS_RULE_VIOLATION`...), a message and optional details, created with `notFound`, `conflict`, `validationError`, `businessRuleViolation` and `unexpected`.
  - `AbstractDomainAggregateRoot`: base class of the aggregates, with domain event registration and no persistence concerns.
  - `AuditableAbstractPersistenceEntity`: base class of the JPA persistence entities, with `id`, `createdAt` and `updatedAt` filled by JPA auditing.
  - `DomainEventPublisher`: publishes and clears the domain events of an aggregate after it is saved.
  - `MessageResource`: resource for responses that only confirm an action, such as a deletion.
  - `LocaleConfiguration`: language of the request from `Accept-Language` (English by default, Spanish supported).
  - `SnakeCaseWithPluralizedTablePhysicalNamingStrategy`: `snake_case` columns and pluralized tables (`FieldPlot` is stored in `field_plots`).
  - `GlobalExceptionHandler` that answers with RFC 9457 problem details; validation errors thrown by commands, queries and value objects become `400 Bad Request` with a translated message.
  - `ProblemDetailFromApplicationErrorAssembler` and `ResponseEntityAssembler`: controllers build their responses from the `Result` of a service or from an `Optional`. The error carries its `type` (`/api/v1/errors/not-found`), its `code` and a message translated to the language of the request.
- **Farming Bounded Context** (Field Management):
  - Field plot: `FieldPlot` aggregate with `PlotStatus`, `RegisterFieldPlotCommand`, queries by id and by owner, `FieldPlotRepository`, command and query services, and the `POST /api/v1/field-plots` and `GET /api/v1/field-plots/{id}` endpoints.
  - Rules of the registration: a producer cannot repeat a plot name and cannot exceed the plot quota of the Seed plan.
- **Crop Health Bounded Context** (domain model):
  - Aggregates `PestReport`, `SatelliteObservation`, `AgroclimaticAlert` and `ClimateForecast`, with their commands and queries.
  - Value objects with their attribute converters (NDVI/NDWI indexes, percentage, severities and statuses).

### Changed
- **Farming**: Renamed the `fieldmanagement` package to `farming`, so the bounded context has a one-word name shared with the Web Application. REST paths and table names do not change.
- **Field Plot**: The plot quota is aligned with the Web Application (4 plots) and the services use Spring's `@Transactional`.
- **Field Plot**: `FieldPlotsController` uses the shared `ResponseEntityAssembler` instead of its own error conversion.

### Fixed
- **Shared**: `IllegalArgumentException` thrown by commands and value objects is answered as `400 Bad Request` instead of `500`.
