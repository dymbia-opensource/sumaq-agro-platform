# SumaqAgro Platform — REST API Technical Stories

## Overview
This document contains API-focused technical stories intended for frontend developers integrating with the SumaqAgro Platform REST API (Java 27, Spring Boot 4.1). They cover the 77 endpoints of the six bounded contexts: IAM, Profiles, Subscriptions and Payments, Field Management, Crop Health, and Harvest Certification.

Common conventions
- Base path: `/api/v1`
- All request and response bodies use `Content-Type: application/json`, except the certificate document (`application/pdf`)
- All protected endpoints require a JWT Bearer token in the `Authorization` header (see TS-ARCH001)
- Error responses follow a standard schema (see TS-ARCH002)
- Roles: `ROLE_FARMER` (agricultural producer), `ROLE_COOPERATIVE_DIRECTOR` (cooperative director), `ROLE_AGRONOMIST` (technical field advisor)
- Interactive documentation: `/swagger-ui.html`

---

### TS-ARCH001 — Authenticate API Requests with a JWT Bearer Token
As a frontend developer, I want to include the JWT token in my API requests so that I can access protected endpoints after signing in.

Note: Only these endpoints are public: `POST /api/v1/users` (sign-up), `POST /api/v1/tokens` (sign-in), `POST /api/v1/authentication/password-recovery/request`, `POST /api/v1/authentication/password-recovery/reset`, `GET /api/v1/public/traceability` (certificate verification), `POST /api/v1/payment-notifications` (Mercado Pago webhook), and the OpenAPI documentation. All other endpoints require authentication.

Acceptance criteria:
- Scenario: Authenticated request succeeds
  - Given the client received a token via `POST /api/v1/tokens`
  - When the client sends any request to a protected endpoint with the header `Authorization: Bearer <token>`
  - Then the API processes the request normally and responds with the expected resource.
- Scenario: Missing token
  - Given the client sends a request to a protected endpoint without an `Authorization` header
  - When the API detects no token
  - Then the API responds `401 Unauthorized` with no further body.
- Scenario: Expired or invalid token
  - Given the client sends a request with a malformed or expired `Authorization: Bearer <token>` header
  - When the API fails to validate the token
  - Then the API responds `401 Unauthorized` with no further body.
- Scenario: Authenticated but not allowed
  - Given the client sends a valid token of a user without the required role or ownership (e.g., a farmer calling a cooperative director endpoint, or a director acting on another cooperative)
  - When the API checks the role or the ownership of the resource
  - Then the API responds `403 Forbidden`, or `404 Not Found` when revealing that the resource exists would leak private data.

---

### TS-ARCH002 — Handle Standardized API Error Responses (RFC 9457 Problem Details)
As a frontend developer, I want all API error responses to follow the RFC 9457 Problem Details standard so that I can implement a single, reusable error-handling layer in my application.

Acceptance criteria:
- Scenario: Any error response
  - Given any API request results in an error (4xx or 5xx) produced by the application
  - Then the API returns a JSON body with `Content-Type: application/problem+json` containing:
    - `type` (URI, required) — problem type identifier URI formatted as `/api/v1/errors/{error-slug}` (e.g., `/api/v1/errors/not-found`, `/api/v1/errors/validation-error`)
    - `title` (String, required) — short, human-readable summary of the problem type
    - `status` (Integer, required) — HTTP status code
    - `detail` (String, required) — human-readable explanation specific to this occurrence (localized per `Accept-Language`)
    - `code` (String, required) — machine-readable domain error code (e.g., `VALIDATION_ERROR`, `FIELDPLOT_NOT_FOUND`)
    - `details` (String, optional) — additional context or original error details
    - `invalidParams` (Array of Objects, optional) — list of validation errors for `400 Bad Request` with `field` and `message` properties

Error code to HTTP status and problem type URI mapping:

| `code` value                                   | HTTP Status                 | Problem `type` URI                       | Title                     |
|------------------------------------------------|-----------------------------|------------------------------------------|---------------------------|
| `VALIDATION_ERROR`                             | `400 Bad Request`           | `/api/v1/errors/validation-error`        | Validation Error          |
| `INVALID_CREDENTIALS`, `AUTHENTICATION_REQUIRED` | `401 Unauthorized`        | `/api/v1/errors/invalid-credentials`, `/api/v1/errors/authentication-required` | Unauthorized |
| `ACCESS_DENIED`                                | `403 Forbidden`             | `/api/v1/errors/access-denied`           | Forbidden                 |
| `*_NOT_FOUND` (e.g., `FIELDPLOT_NOT_FOUND`)   | `404 Not Found`             | `/api/v1/errors/not-found`               | Resource Not Found        |
| `*_CONFLICT` (e.g., `USER_CONFLICT`)           | `409 Conflict`              | `/api/v1/errors/conflict`                | Resource Conflict         |
| `BUSINESS_RULE_VIOLATION`                      | `422 Unprocessable Entity`  | `/api/v1/errors/business-rule-violation` | Business Rule Violation   |
| `UNEXPECTED_ERROR`                             | `500 Internal Server Error` | `/api/v1/errors/unexpected-error`        | Internal Server Error     |

Example error response body:
```json
{
  "type": "/api/v1/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "code": "VALIDATION_ERROR",
  "invalidParams": [
    {
      "field": "areaHectares",
      "message": "must be greater than 0"
    }
  ]
}
```

---

### TS-ARCH003 — Request Localized Error Messages
As a frontend developer, I want to receive error messages in my users' language so that I can display them directly without additional translation.

Supported locales: `en` (English, default), `es` (Spanish)

Acceptance criteria:
- Scenario: Default locale (English)
  - Given the client sends a request without an `Accept-Language` header (or with `Accept-Language: en`)
  - When the API returns an error response
  - Then the `detail` field in the problem detail is in English (e.g., `"Resource not found"`)
- Scenario: Spanish locale
  - Given the client sends a request with the header `Accept-Language: es`
  - When the API returns an error response
  - Then the `detail` field in the problem detail is in Spanish (e.g., `"Recurso no encontrado"`)
- Scenario: Unsupported locale falls back to English
  - Given the client sends a request with an unsupported `Accept-Language` value (e.g., `fr`)
  - When the API returns an error response
  - Then the `detail` field is returned in English.

Note: Only the `detail` field in error responses is localized. All resource data fields (plot names, descriptions, etc.) are stored and returned as provided by the client.

---

## IAM

### TS-IAM001 — Sign-up through the API
As a frontend developer, I want to register a new user through the API so that producers, cooperative directors, and agronomists can create their accounts from the sign-up form.

Acceptance criteria:
- Scenario: Successful sign-up
  - Given a POST request to `/api/v1/users` is received with a body containing: email (String), password (String), roles (Array of String: `ROLE_FARMER`, `ROLE_COOPERATIVE_DIRECTOR`, or `ROLE_AGRONOMIST`), profile (Object: firstName, lastName)
  - When the API validates the request, creates the user, and creates its profile
  - Then the API responds `201 Created` and returns the user with attributes: id (Long), email (String), roles (Array of String).
- Scenario: Pending agronomist invitations
  - Given the new user signs up with `ROLE_AGRONOMIST` and an email that has pending cooperative invitations
  - When the API creates the account
  - Then the invitations are accepted and the agronomist joins the technical team of those cooperatives.
- Scenario: Validation error
  - Given the body has an invalid email, a weak password, or an unknown role
  - When the API validates the request
  - Then the API responds `400 Bad Request` and returns an error payload describing validation errors.
- Scenario: Email already registered
  - Given a user with the same email already exists
  - When the API tries to create the user
  - Then the API responds `409 Conflict` and returns an error payload.

---

### TS-IAM002 — Sign-in through the API
As a frontend developer, I want to sign in through the API so that I can obtain a JWT token and show the right menu for the user's role.

Acceptance criteria:
- Scenario: Successful sign-in
  - Given a POST request to `/api/v1/tokens` is received with a body containing: email (String), password (String)
  - When the API validates the credentials
  - Then the API responds `200 OK` and returns the authenticated user with attributes: id (Long), email (String), roles (Array of String), token (String), fullName (String).
- Scenario: Invalid credentials
  - Given the email does not exist or the password does not match
  - When the API validates the credentials
  - Then the API responds `401 Unauthorized` and returns an error payload with code `INVALID_CREDENTIALS`.

---

### TS-IAM003 — Recover a Forgotten Password
As a frontend developer, I want to request a password reset and set a new password through the API so that users who forgot their password can recover their account.

Acceptance criteria:
- Scenario: Request a reset link
  - Given a POST request to `/api/v1/authentication/password-recovery/request` is received with a body containing: email (String)
  - When the API processes the request
  - Then the API responds `202 Accepted` with no body, whether or not the email is registered (so accounts cannot be discovered), and when the email is registered a reset link is sent by email (Brevo).
- Scenario: Reset the password
  - Given a POST request to `/api/v1/authentication/password-recovery/reset` is received with a body containing: token (String), newPassword (String)
  - When the token is valid, not expired, and not used
  - Then the API updates the password and responds `204 No Content`.
- Scenario: Invalid or expired token
  - Given the token is unknown, expired, or already used, or the new password is invalid
  - When the API validates the request
  - Then the API responds with an error payload (`400 Bad Request` or `422 Unprocessable Entity`).

---

## Profiles

### TS-PRO001 — Get and Update a Profile
As a frontend developer, I want to read and update the user's profile through the API so that I can build the account settings view.

Acceptance criteria:
- Scenario: Get profiles
  - Given a GET request to `/api/v1/profiles` is received, optionally with the query parameter `userId`
  - When the API finds the profile of that user, or of the caller when `userId` is not sent
  - Then the API responds `200 OK` and returns a list of profiles with attributes: id (Long), userId (Long), firstName (String), lastName (String), fullName (String), email (String), phoneNumber (String), photoUrl (String), preferredLanguage (String), dni (String). The email, phone number, and DNI are only returned to the owner of the profile.
- Scenario: Get a profile by id
  - Given a GET request to `/api/v1/profiles/{profileId}` is received
  - When the API finds the profile
  - Then the API responds `200 OK` and returns the profile; otherwise it responds `404 Not Found`.
- Scenario: Update my profile
  - Given a PATCH request to `/api/v1/profiles/{profileId}` is received with a body containing any of: firstName, lastName, phoneNumber, photoUrl, preferredLanguage (`ES` or `EN`), dni
  - When the caller owns the profile and the data is valid
  - Then the API responds `200 OK` and returns the updated profile.
- Scenario: Not the owner
  - Given the caller tries to update another user's profile
  - When the API checks the ownership
  - Then the API responds `403 Forbidden`.

---

### TS-COOP001 — Register and Manage a Cooperative
As a frontend developer, I want to register, list, and update cooperatives through the API so that cooperative directors can set up their organization.

Acceptance criteria:
- Scenario: Register a cooperative
  - Given a POST request to `/api/v1/cooperatives` is received from a user with `ROLE_COOPERATIVE_DIRECTOR`, with a body containing: ruc (String, 11 digits), businessName (String), region (String), managerUserId (Long), institutionalEmail (String), legalRepresentative (String), headquartersAddress (String), switchboardPhone (String)
  - When the API validates the request and the caller is the manager
  - Then the API responds `201 Created` and returns the cooperative with attributes: id (Long), ruc, businessName, region, managerUserId, institutionalEmail, legalRepresentative, headquartersAddress, switchboardPhone.
- Scenario: Get cooperatives
  - Given a GET request to `/api/v1/cooperatives` or `/api/v1/cooperatives/{cooperativeId}` is received
  - When the API finds the cooperatives
  - Then the API responds `200 OK` and returns the cooperative list or the cooperative.
- Scenario: Update a cooperative
  - Given a PUT request to `/api/v1/cooperatives/{cooperativeId}` is received from the director of that cooperative with the editable attributes
  - When the API validates the request
  - Then the API responds `200 OK` and returns the updated cooperative.
- Scenario: Not the director
  - Given the caller does not have `ROLE_COOPERATIVE_DIRECTOR` or does not manage the cooperative
  - When the API checks the role and the ownership
  - Then the API responds `403 Forbidden`.
- Scenario: Duplicated RUC
  - Given a cooperative with the same RUC already exists
  - When the API tries to register it
  - Then the API responds `409 Conflict`.

---

### TS-COOP002 — Manage the Member Registry
As a frontend developer, I want to add, list, and link cooperative members through the API so that directors can keep the member registry of their cooperative.

Acceptance criteria:
- Scenario: Look up a producer account
  - Given a GET request to `/api/v1/cooperatives/{cooperativeId}/member-candidates?email={email}` is received from the director
  - When the API finds a producer account with that email
  - Then the API responds `200 OK` and returns: userId (Long), email, firstName, lastName, dni, phoneNumber, alreadyMember (Boolean), so the form can be filled without typing the data again.
- Scenario: Add a member
  - Given a POST request to `/api/v1/cooperatives/{cooperativeId}/members` is received from the director with a body containing: dni (String), firstName, lastName, community, joinedAt (LocalDate), userId (Long, optional), email (String, optional)
  - When the API validates the request and the cooperative plan still has member quota
  - Then the API responds `201 Created` and returns the member with attributes: id (Long), cooperativeId, dni, firstName, lastName, fullName, community, joinedAt, active (Boolean), userId.
- Scenario: Member quota reached
  - Given the cooperative already has as many members as its plan allows
  - When the API checks the quota through Subscriptions
  - Then the API responds `422 Unprocessable Entity` with code `BUSINESS_RULE_VIOLATION`.
- Scenario: List members
  - Given a GET request to `/api/v1/cooperatives/{cooperativeId}/members`, or to `/api/v1/cooperative-members` with `cooperativeId` or `userId` as query parameter, is received
  - When the API finds the members
  - Then the API responds `200 OK` and returns the member list; when neither `cooperativeId` nor `userId` is sent to `/api/v1/cooperative-members` it responds `400 Bad Request`.
- Scenario: Link a member to an account
  - Given a PUT request to `/api/v1/cooperatives/{cooperativeId}/members/{memberId}/account` is received from the director with a body containing: email (String)
  - When a producer account with that email exists
  - Then the API links the member to that account and responds `200 OK` with the member.

---

### TS-COOP003 — Invite Agronomists to the Technical Team
As a frontend developer, I want to invite agronomists by email and list the invitations through the API so that directors can build their technical team.

Acceptance criteria:
- Scenario: Invite an agronomist
  - Given a POST request to `/api/v1/cooperatives/{cooperativeId}/invitations` is received from the director with a body containing: email (String)
  - When the API stores the invitation
  - Then the API responds `201 Created` and returns the invitation with attributes: id (Long), cooperativeId, email, status (String), sentAt (Instant), agronomistUserId (Long, null until accepted), and the invitation email is sent after the transaction commits.
- Scenario: List invitations
  - Given a GET request to `/api/v1/cooperatives/{cooperativeId}/invitations` is received from the director
  - When the API finds the invitations of the cooperative
  - Then the API responds `200 OK` and returns the invitation list.
- Scenario: Not the director
  - Given the caller does not manage the cooperative
  - When the API checks the ownership
  - Then the API responds `403 Forbidden`.

---

### TS-COOP004 — Assign Agronomists to Plots
As a frontend developer, I want to assign and reassign agronomists to member plots through the API so that each plot has a technical advisor.

Acceptance criteria:
- Scenario: Assign an agronomist
  - Given a POST request to `/api/v1/agronomist-assignments` is received from the director with a body containing: cooperativeId (Long), agronomistUserId (Long), plotId (Long)
  - When the agronomist is in the technical team, the plot belongs to an active member, and the advisor plan still has quota
  - Then the API responds `201 Created` and returns the assignment with attributes: id (Long), cooperativeId, agronomistUserId, plotId, producerUserId, assignedAt (Instant), active (Boolean), status (String), history (Array of: agronomistUserId, assignedAt, endedAt).
- Scenario: Reassign an agronomist
  - Given a POST request to `/api/v1/agronomist-assignments/{assignmentId}/reassignments` is received from the director with a body containing: agronomistUserId (Long)
  - When the API validates the new agronomist
  - Then the API responds `200 OK`, returns the assignment, and adds the previous agronomist to its history.
- Scenario: Get assignments
  - Given a GET request to `/api/v1/agronomist-assignments` or `/api/v1/agronomist-assignments/{assignmentId}` is received
  - When the API finds the assignments
  - Then the API responds `200 OK`; for an unknown id it responds `404 Not Found`.
- Scenario: Business rule violation
  - Given the agronomist is not in the technical team, the plot does not belong to an active member, or the quota is reached
  - When the API validates the request
  - Then the API responds `422 Unprocessable Entity`.

---

## Subscriptions and Payments

### TS-SUB001 — Get the Subscription Plans
As a frontend developer, I want to list the subscription plans through the API so that I can show the plans catalog.

Acceptance criteria:
- Scenario: List plans
  - Given a GET request to `/api/v1/plans` is received
  - When the API reads the plan catalog
  - Then the API responds `200 OK` and returns the plans with attributes: code (`SEED`, `COOPERATIVE_PRO`, `TECHNICAL_ADVISOR`), monthlyPrice (BigDecimal), annualPrice (BigDecimal), currency (`PEN`), registeredPlotLimit (Integer), producerLimit (Integer), supervisedPlotLimit (Integer).

---

### TS-SUB002 — Pay a Plan with Mercado Pago Checkout Pro
As a frontend developer, I want to start a Mercado Pago checkout through the API so that the user can pay a plan without the browser ever handling payment credentials.

Acceptance criteria:
- Scenario: Start a checkout
  - Given a POST request to `/api/v1/subscriptions/me/checkout-sessions` is received with a body containing: planCode (`SEED`, `COOPERATIVE_PRO`, `TECHNICAL_ADVISOR`), billingCycle (`MONTHLY`, `ANNUAL`)
  - When the plan matches the caller's role and Mercado Pago creates the preference
  - Then the API responds `201 Created` and returns: reference (String), amount (BigDecimal), currency (String), checkoutUrl (URI), sandboxCheckoutUrl (URI), and the client redirects the browser to `checkoutUrl`.
- Scenario: Plan not allowed for the role
  - Given the plan does not match the caller's role (e.g., a farmer selecting `COOPERATIVE_PRO`)
  - When the API checks the plan selection
  - Then the API responds `403 Forbidden`.
- Scenario: Confirm the payment after the redirect
  - Given a POST request to `/api/v1/subscriptions/me/payment-confirmations` is received with a body containing: paymentId (String, returned by Mercado Pago in the redirect)
  - When the API reads the payment from Mercado Pago and it belongs to the caller
  - Then the API responds `200 OK` and returns the payment with attributes: reference, subscriptionId (Long), planCode, billingCycle, amount, currency, status (`PENDING`, `COMPLETED`, `REJECTED`), attemptedAt (Instant); an approved payment activates or renews the subscription.
- Scenario: Mercado Pago webhook
  - Given Mercado Pago sends a POST request to `/api/v1/payment-notifications` with `type=payment` and the payment id
  - When the API reads the payment back from Mercado Pago
  - Then the API applies it once (repeated notifications have no effect) and always responds `200 OK`.

Note: `POST /api/v1/subscriptions/me/checkout` (body: planCode, billingCycle, simulatedOutcome) only exists when `subscriptions.demo-payments.enabled=true`; it simulates a completed or rejected payment for demos without Mercado Pago.

---

### TS-SUB003 — Get and Cancel My Subscription
As a frontend developer, I want to read the current subscription, its payments, and cancel its renewal through the API so that I can build the billing view.

Acceptance criteria:
- Scenario: Get my subscription
  - Given a GET request to `/api/v1/subscriptions/me` is received
  - When the caller has a subscription
  - Then the API responds `200 OK` and returns: id (Long), planCode (String), billingCycle (String), status (String), startDate (LocalDate), endDate (LocalDate); when the caller has none, it responds `204 No Content`.
- Scenario: Get my payments
  - Given a GET request to `/api/v1/subscriptions/me/payments` is received
  - When the API finds the caller's payments
  - Then the API responds `200 OK` and returns the payment list (empty when there are none).
- Scenario: Cancel the renewal
  - Given a POST request to `/api/v1/subscriptions/me/cancellation` is received
  - When the caller has an active subscription
  - Then the API responds `200 OK` and returns the subscription, which stays usable until its `endDate`; otherwise it responds with an error payload.

---

## Field Management

### TS-FM001 — Register a Field Plot
As a frontend developer, I want to register a plot through the API so that producers can start monitoring their fields.

Acceptance criteria:
- Scenario: Successful registration
  - Given a POST request to `/api/v1/field-plots` is received with a body containing: ownerUserId (Long), name (String), region (String), areaHectares (BigDecimal), cooperativeId (Long, optional), cooperativeMemberId (Long, optional)
  - When the caller is the owner (or the director of the given cooperative) and the plan still has plot quota
  - Then the API responds `201 Created` and returns the plot with attributes: id (Long), ownerUserId, name, region, status (String), areaHectares, agroMonitoringPolygonId (String), boundary (Array of latitude/longitude), soilBaseline (Object), cooperativeId, cooperativeMemberId.
- Scenario: Plot quota reached
  - Given the caller already has as many plots as the plan allows
  - When the API checks the quota through Subscriptions
  - Then the API responds `422 Unprocessable Entity`.
- Scenario: Registering for someone else
  - Given the caller is neither the owner nor the director of the cooperative
  - When the API checks the ownership
  - Then the API responds `403 Forbidden`.

---

### TS-FM002 — Get Field Plots
As a frontend developer, I want to fetch plots through the API so that I can show the producer's plots and their map.

Acceptance criteria:
- Scenario: Get my plots
  - Given a GET request to `/api/v1/field-plots` is received
  - When the API finds the plots of the producer
  - Then the API responds `200 OK` and returns the plot list (empty when there are none).
- Scenario: Get a plot by id
  - Given a GET request to `/api/v1/field-plots/{fieldPlotId}` is received
  - When the API finds the plot
  - Then the API responds `200 OK` and returns the plot; otherwise it responds `404 Not Found`.

---

### TS-FM003 — Delineate the Plot Boundary and Record the Soil Baseline
As a frontend developer, I want to save the plot polygon and the soil analysis through the API so that the plot can be monitored by satellite.

Acceptance criteria:
- Scenario: Delineate the boundary
  - Given a PUT request to `/api/v1/field-plots/{fieldPlotId}/polygon` is received with a body containing: coordinates (Array of at least 3 objects with latitude and longitude)
  - When the polygon is valid
  - Then the API responds `200 OK`, returns the plot with its boundary, and registers the polygon in AgroMonitoring (the `agroMonitoringPolygonId` is filled in).
- Scenario: Record the soil baseline
  - Given a PUT request to `/api/v1/field-plots/{fieldPlotId}/soil-baseline` is received with a body containing: ph (BigDecimal), texture (String), organicMatterPercentage (BigDecimal), recordedAt (LocalDate)
  - When the values are valid
  - Then the API responds `200 OK` and returns the plot with its soil baseline.
- Scenario: Invalid data or unknown plot
  - Given the polygon has fewer than 3 points, the coordinates are out of range, or the plot does not exist
  - When the API validates the request
  - Then the API responds `400 Bad Request` or `404 Not Found`.

---

### TS-FM004 — Start and Manage a Crop Campaign
As a frontend developer, I want to start, update, list, and close crop campaigns through the API so that producers can track each season of a plot.

Acceptance criteria:
- Scenario: Start a campaign
  - Given a POST request to `/api/v1/crop-campaigns` is received with a body containing: fieldPlotId (Long), season (String), cropType (String), seedVariety (String), customSeedVariety (Boolean), sowingDate (LocalDate)
  - When the plot has no open campaign
  - Then the API responds `201 Created`, returns the campaign with attributes: id (Long), fieldPlotId, season, cropType, seedVariety, customSeedVariety, sowingDate, status (String), and opens its campaign ledger.
- Scenario: Update a campaign
  - Given a PATCH request to `/api/v1/crop-campaigns/{cropCampaignId}` is received with any of: cropType, seedVariety, customSeedVariety, sowingDate
  - When the campaign is open
  - Then the API responds `200 OK` and returns the updated campaign.
- Scenario: Get campaigns
  - Given a GET request to `/api/v1/crop-campaigns/{cropCampaignId}` or `/api/v1/field-plots/{fieldPlotId}/crop-campaigns` is received
  - When the API finds the campaigns
  - Then the API responds `200 OK`; for an unknown campaign it responds `404 Not Found`.
- Scenario: Close a campaign
  - Given a POST request to `/api/v1/crop-campaigns/{cropCampaignId}/closures` is received
  - When the campaign is open
  - Then the API responds `200 OK` with a message (String), and the ledger is frozen.
- Scenario: Campaign already closed
  - Given the campaign is already closed
  - When the API tries to update or close it
  - Then the API responds `422 Unprocessable Entity`.

---

### TS-FM005 — Record Campaign Expenses and the Breakeven Price
As a frontend developer, I want to record expenses and yields in the campaign ledger through the API so that producers know their total investment and breakeven price, even when expenses were queued offline.

Acceptance criteria:
- Scenario: Get the ledger
  - Given a GET request to `/api/v1/campaign-ledgers/{campaignLedgerId}` or `/api/v1/crop-campaigns/{cropCampaignId}/campaign-ledger` is received
  - When the API finds the ledger
  - Then the API responds `200 OK` and returns: id (Long), cropCampaignId, yieldUnit, expectedYield, actualYield, frozen (Boolean), currency, totalInvestment, breakevenPrice, entries (Array of: id, category, description, quantity, unitPrice, total, expenseDate, notes, clientSyncId); for an unknown ledger it responds `404 Not Found`.
- Scenario: Record an expense
  - Given a POST request is received to one of:
    - `/api/v1/campaign-ledgers/{campaignLedgerId}/expenses/inputs` with: inputName, quantity, unitPrice, expenseDate, clientSyncId
    - `/api/v1/campaign-ledgers/{campaignLedgerId}/expenses/labor` with: activity, workers, days, dailyWage, expenseDate, clientSyncId
    - `/api/v1/campaign-ledgers/{campaignLedgerId}/expenses/freight` with: route, cost, expenseDate, clientSyncId
  - When the ledger is not frozen and the values are valid
  - Then the API responds `201 Created` and returns the ledger with the new entry and the recalculated totalInvestment and breakevenPrice.
- Scenario: Offline expense sent twice
  - Given the client resends an expense with a `clientSyncId` that was already recorded
  - When the API detects the duplicate
  - Then the expense is not recorded twice.
- Scenario: Estimate the yield
  - Given a PUT request to `/api/v1/campaign-ledgers/{campaignLedgerId}/yield-estimate` is received with: expectedYield, actualYield
  - When the values are valid
  - Then the API responds `200 OK` and returns the ledger with the recalculated breakevenPrice.
- Scenario: Frozen ledger
  - Given the campaign is closed
  - When the client records an expense
  - Then the API responds `422 Unprocessable Entity`.

---

## Crop Health

### TS-CH001 — Import and Get Satellite Observations
As a frontend developer, I want to import and list NDVI/NDWI observations of a plot through the API so that I can show its vegetation map.

Acceptance criteria:
- Scenario: Import an observation
  - Given a POST request to `/api/v1/satellite-observations/imports` is received with a body containing: plotId (Long), start (Instant), end (Instant)
  - When the plot has a polygon in AgroMonitoring and imagery exists for the period
  - Then the API responds `200 OK` and returns the observation with attributes: id (Long), plotId, capturedAt (Instant), source (String), ndviMean (Double), ndwiMean (Double), cloudCoveragePercent (Double), surfaceTempKelvin (Double).
- Scenario: Get observations
  - Given a GET request to `/api/v1/satellite-observations?plotId={plotId}` is received
  - When the API finds the observations
  - Then the API responds `200 OK` and returns the observation list.
- Scenario: Provider or plot not ready
  - Given the plot has no polygon or AgroMonitoring does not respond
  - When the API tries to import
  - Then the API responds with an error payload and stores nothing.

---

### TS-CH002 — Import and Get Climate Forecasts
As a frontend developer, I want to import and list the weather forecast of a region through the API so that producers can plan field work.

Acceptance criteria:
- Scenario: Import a forecast
  - Given a POST request to `/api/v1/climate-forecasts/imports` is received with a body containing: plotId (Long)
  - When AgroMonitoring returns the 5-day forecast for the plot
  - Then the API responds `200 OK`, stores the forecasts of the plot's region, and returns them.
- Scenario: Get forecasts by region
  - Given a GET request to `/api/v1/climate-forecasts?region={region}` is received
  - When the API finds the forecasts
  - Then the API responds `200 OK` and returns: id (Long), region, temperatureCelsius (Double), rainChance (Double), forecastDate (Instant), source (String).

---

### TS-CH003 — Get Agroclimatic Alerts
As a frontend developer, I want to list the agroclimatic alerts of a region through the API so that producers see frost, drought, or heavy rain warnings.

Acceptance criteria:
- Scenario: Get alerts by region
  - Given a GET request to `/api/v1/agroclimatic-alerts?region={region}` is received
  - When the API finds the alerts
  - Then the API responds `200 OK` and returns: id (Long), title, description, severity (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), region, issuedAt (Instant), source.

Note: Alerts are issued by the platform from the imported forecasts; serious alerts are also emailed to the producers of the region after the transaction commits.

---

### TS-CH004 — Report Pests
As a frontend developer, I want to record, list, and delete pest reports through the API so that producers can ask for technical help.

Acceptance criteria:
- Scenario: Record a pest report
  - Given a POST request to `/api/v1/pest-reports` is received with a body containing: plotId (Long), reporterId (Long), description (String), severity (`LOW`, `MEDIUM`, `HIGH`, `SEVERE`)
  - When the plot exists and the data is valid
  - Then the API responds `201 Created` and returns: id (Long), plotId, reporterId, description, severity, status (String).
- Scenario: Get pest reports
  - Given a GET request to `/api/v1/pest-reports/{pestReportId}` or `/api/v1/pest-reports?plotId={plotId}` is received
  - When the API finds the reports
  - Then the API responds `200 OK`; for an unknown report it responds `404 Not Found`.
- Scenario: Delete a pest report
  - Given a DELETE request to `/api/v1/pest-reports/{pestReportId}` is received
  - When the report exists
  - Then the API responds `200 OK` with a message (String).

---

### TS-CH005 — Schedule and Complete Field Inspections
As a frontend developer, I want to schedule and complete field inspections through the API so that agronomists can attend pest reports.

Acceptance criteria:
- Scenario: Schedule an inspection
  - Given a POST request to `/api/v1/field-inspections` is received with a body containing: pestReportId (Long), agronomistId (Long), scheduledAt (Instant)
  - When the pest report exists
  - Then the API responds `201 Created` and returns: id (Long), pestReportId, plotId, agronomistId, scheduledAt, status (String), findings (String), completedAt (Instant).
- Scenario: Complete an inspection
  - Given a POST request to `/api/v1/field-inspections/{fieldInspectionId}/completion` is received with a body containing: findings (String)
  - When the inspection is scheduled
  - Then the API responds `200 OK` and returns the completed inspection.
- Scenario: Get inspections
  - Given a GET request to `/api/v1/field-inspections/{fieldInspectionId}` or `/api/v1/field-inspections?plotId={plotId}` is received
  - When the API finds the inspections
  - Then the API responds `200 OK`; for an unknown inspection it responds `404 Not Found`.

---

### TS-CH006 — Issue Technical Prescriptions
As a frontend developer, I want to issue prescriptions and register their application through the API so that agronomists can tell producers how to treat a pest.

Acceptance criteria:
- Scenario: Issue a prescription
  - Given a POST request to `/api/v1/technical-prescriptions` is received with a body containing: pestReportId (Long), agronomistId (Long), product (String), dosage (String), instructions (String), applicationDate (LocalDate)
  - When the pest report exists and the data is valid
  - Then the API responds `201 Created` and returns: id (Long), pestReportId, plotId, agronomistId, product, dosage, instructions, applicationDate, status (String), appliedAt (Instant).
- Scenario: Register the application
  - Given a POST request to `/api/v1/technical-prescriptions/{technicalPrescriptionId}/application` is received
  - When the prescription has not been applied yet
  - Then the API responds `200 OK` and marks the prescription as applied.
- Scenario: Get prescriptions
  - Given a GET request to `/api/v1/technical-prescriptions/{technicalPrescriptionId}` or `/api/v1/technical-prescriptions?plotId={plotId}` is received
  - When the API finds the prescriptions
  - Then the API responds `200 OK`; for an unknown prescription it responds `404 Not Found`.

---

### TS-CH007 — Publish Regional Bulletins
As a frontend developer, I want to publish and list regional bulletins through the API so that agronomists can share advice with every producer of a region.

Acceptance criteria:
- Scenario: Publish a bulletin
  - Given a POST request to `/api/v1/regional-bulletins` is received with a body containing: agronomistId (Long), region (String), title (String), content (String)
  - When the data is valid
  - Then the API responds `201 Created` and returns: id (Long), agronomistId, region, title, content, publishedAt (Instant).
- Scenario: Get bulletins by region
  - Given a GET request to `/api/v1/regional-bulletins?region={region}` is received
  - When the API finds the bulletins
  - Then the API responds `200 OK` and returns the bulletin list.

---

## Harvest Certification

### TS-HC001 — Register and List Harvest Batches
As a frontend developer, I want to register and list harvest batches through the API so that cooperative directors can receive the harvest of their members.

Acceptance criteria:
- Scenario: Register a batch
  - Given a POST request to `/api/v1/harvest-batches` is received from the cooperative director with a body containing: batchCode (String), cooperativeId (Long), memberId (Long), plotId (Long), cropType (String), harvestedOn (LocalDate)
  - When the plot belongs to an active member of the cooperative
  - Then the API responds `201 Created` and returns the batch with attributes: id (Long), batchCode, cooperativeId, memberId, plotId, cropType, harvestedOn, registeredAt, status, netWeightKg, sampleIdentifier, sampleWeightKg, firstCaliberPercentage, secondCaliberPercentage, thirdCaliberPercentage, pestDamagePercentage, coffeeCuppingScore, coffeeFermentDefect, qualityScore, qualityCategory.
- Scenario: Plot of another member
  - Given the plot does not belong to an active member of the cooperative
  - When the API validates the request
  - Then the API responds `422 Unprocessable Entity` with code `BUSINESS_RULE_VIOLATION`.
- Scenario: Not the director
  - Given the caller does not direct the cooperative
  - When the API checks the ownership
  - Then the API responds `403 Forbidden`.
- Scenario: List and get batches
  - Given a GET request to `/api/v1/harvest-batches?cooperativeId={cooperativeId}` or `/api/v1/harvest-batches/{harvestBatchId}` is received
  - When the caller can read the batches (director, or the member who owns them)
  - Then the API responds `200 OK` with the batches, including their certificate when one was issued; for an unknown batch it responds `404 Not Found`.

---

### TS-HC002 — Weigh, Sample, and Grade a Batch
As a frontend developer, I want to record the weight, the sample, and the grading of a batch through the API so that its quality category can be computed.

Acceptance criteria:
- Scenario: Weigh the batch
  - Given a POST request to `/api/v1/harvest-batches/{harvestBatchId}/weight` is received with a body containing: grossWeightKg (BigDecimal), tareKg (BigDecimal)
  - When the weights are valid
  - Then the API responds `200 OK` and returns the batch with its netWeightKg.
- Scenario: Extract a representative sample
  - Given a POST request to `/api/v1/harvest-batches/{harvestBatchId}/sample` is received with: sampleIdentifier (String), sampleWeightKg (BigDecimal)
  - When the batch is weighed
  - Then the API responds `200 OK` and returns the batch with its sample.
- Scenario: Grade potato calibers
  - Given a POST request to `/api/v1/harvest-batches/{harvestBatchId}/potato-calibers` is received with: firstPercentage, secondPercentage, thirdPercentage, pestDamagePercentage
  - When the batch is a potato batch with a sample and the percentages are valid
  - Then the API responds `200 OK` and returns the graded batch with its qualityScore and qualityCategory (e.g., `PREMIUM_GOLD`, `STANDARD`, `B_GRADE_REQUIRES_SORTING`).
- Scenario: Record a coffee cupping
  - Given a POST request to `/api/v1/harvest-batches/{harvestBatchId}/coffee-cupping` is received with: aroma, flavor, acidity, body, totalScore (BigDecimal), fermentDefect (Boolean)
  - When the batch is a coffee batch with a sample
  - Then the API responds `200 OK` and returns the graded batch with its qualityCategory (e.g., `SPECIALTY_COFFEE`, `COMMERCIAL_COFFEE`).
- Scenario: Wrong step or not allowed
  - Given the batch is in the wrong state for the step, or the caller does not manage it
  - When the API validates the request
  - Then the API responds `422 Unprocessable Entity`, or `404 Not Found` when the caller does not manage the batch.

---

### TS-HC003 — Issue, Revoke, and Download Quality Certificates
As a frontend developer, I want to issue, revoke, and download quality certificates through the API so that cooperatives can prove the quality of a graded batch.

Acceptance criteria:
- Scenario: Issue a certificate
  - Given a POST request to `/api/v1/quality-certificates` is received from the director with a body containing: certificateNumber (String), harvestBatchId (Long), issuedByUserId (Long)
  - When the batch is graded and has no active certificate
  - Then the API responds `201 Created` and returns: id (Long), certificateNumber, harvestBatchId, qualityCategory, certificateHash (String), issuedByUserId, issuedAt (Instant), status (String).
- Scenario: Get a certificate by number
  - Given a GET request to `/api/v1/quality-certificates?certificateNumber={number}` is received
  - When the caller can read the batch
  - Then the API responds `200 OK` with the certificate; otherwise `404 Not Found`.
- Scenario: Revoke a certificate
  - Given a POST request to `/api/v1/quality-certificates/{qualityCertificateId}/revocation` is received from the director
  - When the certificate is active
  - Then the API responds `200 OK` and returns the revoked certificate.
- Scenario: Download the certificate document
  - Given a GET request to `/api/v1/quality-certificates/{qualityCertificateId}/document` is received
  - When the caller can read the certificate and it is active
  - Then the API responds `200 OK` with the PDF document, which includes a QR code to the public verification; a revoked certificate responds `409 Conflict`.

---

### TS-HC004 — Verify a Certificate Publicly by QR Code
As a frontend developer, I want to verify a certificate without a token so that buyers who scan the QR code can check that it is authentic.

Acceptance criteria:
- Scenario: Authentic certificate
  - Given a GET request to `/api/v1/public/traceability?number={certificateNumber}&hash={certificateHash}` is received without a token
  - When the certificate exists and the hash matches
  - Then the API responds `200 OK` and returns: certificateNumber, status, authentic (Boolean), batchCode, cropType, qualityCategory, qualityScore, plotName, region, seedVariety, without any private data of the producer.
- Scenario: Unknown certificate
  - Given the certificate number does not exist
  - When the API looks it up
  - Then the API responds `404 Not Found`.
- Scenario: Tampered or revoked certificate
  - Given the hash does not match or the certificate was revoked
  - When the API verifies it
  - Then the API responds `200 OK` with `authentic: false` or the revoked status.
