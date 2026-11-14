# SumaqAgro Backend Architecture

## Overview

SumaqAgro is a Spring Boot REST API for agricultural management and technical advisory in Peru. It supports independent farmers, cooperative directors, and agronomists. The backend manages accounts, profiles, cooperative membership, field plots, crop campaigns, and crop health workflows.

The application is a **modular monolith**. It runs as one Spring Boot application backed by MySQL. Java packages define bounded context boundaries, and each context owns its business rules and persistence mappings.

## Bounded Contexts

| Context | Package | Responsibilities |
| --- | --- | --- |
| Identity and Access Management | `iam` | Account registration, sign-in, roles, and JWT authentication |
| Profiles | `profiles` | User profiles, cooperatives, member rosters, agronomist invitations, and plot assignments |
| Farming | `farming` | Plot registration, plot boundaries, soil baselines, and crop campaigns |
| Crop Health | `crop` | Satellite observations, forecasts, alerts, pest reports, inspections, and technical prescriptions |
| Subscriptions and Payments | `subscriptions` | Plan catalog, checkout, subscription validity, renewal, cancellation, and payment history. Developed in `feature/subscription-management`. |

The `shared` package is the **Shared Kernel**. It provides common aggregate bases, application results, REST error mapping, event publishing support, and JPA auditing.

## Layers Within a Context

| Layer | Responsibility |
| --- | --- |
| `domain` | Defines aggregates, entities, value objects, domain events, and repository contracts. Aggregate methods enforce business rules. |
| `application` | Coordinates use cases. Command services handle writes, and query services handle reads. Application services define transaction boundaries where required. |
| `infrastructure` | Implements repository contracts and external integrations. JPA entities and persistence assemblers map between stored records and domain objects where the models are separate. |
| `interfaces` | Exposes REST controllers, request and response resources, assemblers, and public facades for other contexts. |

A typical write request flows from a REST controller to a command service. The service invokes domain behavior and persists the result through a repository contract. A query service retrieves the data requested by the controller, which maps it to a REST response. Commands and queries share the same database.

## Communication Between Contexts

Contexts use facades for capabilities that other modules need. For example, `ProfilesContextFacade` exposes profile and assignment information, and `FieldManagementContextFacade` exposes Farming capabilities. These contracts let consumers work without importing another context's persistence repositories.

Aggregates reference entities owned by other contexts through identifiers. For example, an agronomist assignment records the agronomist and plot IDs while Profiles owns the assignment lifecycle. Repository adapters that use `DomainEventPublisher` dispatch events recorded by aggregates during persistence.

In the subscription branch, Farming obtains the applicable plot limit through `SubscriptionsContextFacade`. Farming counts the registered plots and applies the limit during registration. Profiles validates cooperative membership when a director registers a plot for a member.

## Authentication and Error Handling

Spring Security authenticates protected requests using bearer JWTs issued by IAM. Passwords are hashed with BCrypt. Operations that act on behalf of the signed-in account can use the authenticated principal to resolve the caller's identity.

Application services can return `Result` values with an `ApplicationError`. Shared REST assemblers convert these errors into `ProblemDetail` responses. Message bundles support English and Spanish.

## Persistence and External Integration

MySQL stores the application's data. Spring Data JPA and Hibernate implement persistence, using a physical naming strategy for snake_case identifiers and pluralized table names. The shared JPA base class provides generated identifiers and audit timestamps for persistence entities that extend it.

AgroMonitoring is an external provider accessed through infrastructure adapters. The subscription branch uses a checkout configured for the university project. It records payment outcomes without collecting card details or charging users.

## Runtime Configuration

Spring Boot configuration lives in `src/main/resources`. The `dev` profile uses a local MySQL instance, while `prod` reads database connection settings from environment variables. JWT signing and AgroMonitoring credentials are supplied through the environment.

See the [README](../README.md) for setup instructions, required environment variables, and OpenAPI endpoints.