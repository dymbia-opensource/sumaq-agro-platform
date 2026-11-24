# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Profiles**: A member is linked to his producer account by **email** (`email` in `POST /api/v1/cooperatives/{id}/members`) instead of the numeric user id, and a member registered without an account is linked later with `PUT /api/v1/cooperatives/{id}/members/{memberId}/account`. Only producer accounts can be linked.
- **Profiles**: Optional **DNI** in the profile (`dni` in `PATCH /api/v1/profiles/{id}`; an empty value removes it), only shown to the owner of the profile.
- **Profiles**: `GET /api/v1/cooperatives/{id}/member-candidates?email=` returns the names, DNI and phone of the producer account that uses an email, so the director only adds the community when he registers the member.
- **Profiles**: Technical team of the cooperative with `GET /api/v1/cooperatives/{id}/invitations`: pending and accepted invitations, the accepted ones with the user id of the agronomist.
- **Profiles**: An agronomist who signs up with an invited email joins the technical team of the cooperative (the invitation is accepted), and an agronomist who already has an account joins at once and is emailed a link to sign in.

### Changed
- **REST API documentation**: Every endpoint is grouped and described in Swagger (`@Tag` and `@Operation` on the plans, subscriptions, demo checkout, password recovery and certification workspace controllers).
- **Profiles**: `userId` of `POST /api/v1/cooperatives/{id}/members` is deprecated in favor of `email`; it must belong to a producer account.
- **Profiles**: The same email cannot be invited twice to a cooperative, and the invitation link also carries the invited email.

### Security
- **Profiles**: `GET /api/v1/profiles` no longer lists every profile: without `userId` it returns the caller's own profile, and the phone, email and DNI are only shown to the owner (other users see the name).
- **Profiles**: Only the director of a cooperative can update it (`PUT /api/v1/cooperatives/{id}`), assign or reassign the agronomists of its plots (`POST /api/v1/agronomist-assignments`, `POST /api/v1/agronomist-assignments/{id}/reassignments`), and a director registers a cooperative only in his own name. These endpoints accepted any authenticated user before.
- **Profiles**: Only agronomists of the technical team of the cooperative can be put in charge of its plots.
- **Profiles**: The role of the director endpoints is checked with `@PreAuthorize`, and `CooperativeAccess` checks that the director manages the cooperative.
- **Profiles**: A profile can only be updated by its owner (`PATCH /api/v1/profiles/{id}` accepted any user before).
- **Profiles**: Only the director of the cooperative can invite agronomists (`POST /api/v1/cooperatives/{id}/invitations` accepted any request before).

## [1.1.0] - 2026-11-22

### Added
- **Campaign Ledger** (Farming, US-35 to US-40):
  - `CampaignLedger` aggregate with the `ExpenseEntry` entity and the `Money`, `YieldUnit`, `ExpenseCategory` and `CropCampaignId` value objects: input, daily labor and field freight expenses, expected and actual yield, total investment and breakeven price.
  - The ledger opens by itself when a crop campaign starts (`CropCampaignStartedEvent`) and is frozen when the campaign is closed (`CropCampaignClosedEvent`). The same `clientSyncId` is recorded only once.
  - JPA persistence (`campaign_ledgers` and `expense_entries`), command and query services, and the endpoints `GET /api/v1/campaign-ledgers/{id}`, `GET /api/v1/crop-campaigns/{id}/campaign-ledger`, `POST /api/v1/campaign-ledgers/{id}/expenses/inputs`, `/expenses/labor`, `/expenses/freight` and `PUT /api/v1/campaign-ledgers/{id}/yield-estimate`.
- **Crop Health**: Weather forecast from AgroMonitoring with `POST /api/v1/climate-forecasts/imports`. The 5-day forecast of the plot polygon is recorded as one climate forecast per day (average temperature in Celsius and share of 3-hour intervals with rain), and frost and water stress alerts are issued for the plot region.
- **Crop Health**: Serious agroclimatic alerts (`HIGH` and `CRITICAL`) are emailed to the producers with plots in the region.
- **Subscriptions**: Payment of the plans with Mercado Pago Checkout Pro: `POST /api/v1/subscriptions/me/checkout-sessions`, `POST /api/v1/subscriptions/me/payment-confirmations` and the webhook `POST /api/v1/payment-notifications`. The payment is always read back from Mercado Pago and checked (owner, amount and currency) before the subscription is activated or renewed; applying the same payment twice changes nothing. The demo checkout stays available in the `dev` profile.
- **Profiles**: The agronomist invitation email is sent with Brevo after the invitation is stored.
- **Shared**: `EmailSender` port with the Brevo adapter for notification emails, disabled when there are no Brevo credentials.
- **Shared**: OpenAPI and virtual threads configuration, and error details given as message keys are translated.
- Unit tests of the field plot, crop campaign, campaign ledger, climate forecast import, payments and notification handlers.

### Changed
- **REST API** (breaking for API clients):
  - `PUT /api/v1/profiles/{id}` is now `PATCH`.
  - `PUT /api/v1/agronomist-assignments/{id}` is now `POST /api/v1/agronomist-assignments/{id}/reassignments`.
  - `PATCH /api/v1/field-inspections/{id}/completion` and `PATCH /api/v1/technical-prescriptions/{id}/application` are now `POST`.
  - `POST /api/v1/subscriptions/me/cancel` is now `POST /api/v1/subscriptions/me/cancellation`.
  - `POST /api/v1/satellite-observations/import` is now `POST /api/v1/satellite-observations/imports`.
  - `GET /api/v1/quality-certificates/document?certificateNumber=` is now `GET /api/v1/quality-certificates/{id}/document`.
- **Bounded contexts**: Crop and Certification read Farming and Profiles only through `FarmingContextFacade` and `ProfilesContextFacade`, and the controllers of Certification use query services instead of repositories.
- **Farming**: `FieldManagementContextFacade` renamed to `FarmingContextFacade`; the soil baseline is stored as an embeddable.
- **Subscriptions**: The payment status is stored as text, so new statuses (`PENDING`) do not need a column change.

### Fixed
- **Security**: Field plot and Crop Health endpoints require authentication; only sign-up, sign-in, Swagger, public traceability, password recovery and the payment webhook are public.
- **Farming**: Domain errors of the command services answer `400`, `404` and `409` with translatable message keys.
- **Farming**: Timeouts in the AgroMonitoring polygon client.
- **Shared**: Spring MVC and security exceptions answer their HTTP status, and error messages no longer fall back to the language of the server.
- **Crop**: The Farming ACL bean has its own name, so the application starts.

### Upgrade notes
- New environment variables: `JWT_SECRET`, `MERCADOPAGO_ACCESS_TOKEN`, `BREVO_API_KEY`, `BREVO_SENDER_EMAIL` and `PASSWORD_RESET_EMAIL_ENABLED`.
- Databases created by an earlier version of Crop Health must drop an old column: `ALTER TABLE climate_forecasts DROP COLUMN temperature;`.

## [1.0.0] - 2026-11-15

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

[Unreleased]: https://github.com/dymbia-opensource/sumaq-agro-platform/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/dymbia-opensource/sumaq-agro-platform/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/dymbia-opensource/sumaq-agro-platform/releases/tag/v1.0.0
