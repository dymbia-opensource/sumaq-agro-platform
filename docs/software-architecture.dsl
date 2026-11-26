workspace "SumaqAgro Platform - C4 Model" "System-wide software architecture of the SumaqAgro Platform: system context, containers, and the components of the Landing Page, the Web Application, and the RESTful Web Services." {

    !identifiers hierarchical

    model {
        # =====================================================================
        # 1. HUMAN ACTORS
        # =====================================================================
        productor = person "Agricultural Producer" "Smallholder farmer who registers plots, records campaign expenses, monitors crop health, and reports pests." "Farmer"
        directivo = person "Cooperative Director" "Manages the cooperative, its member registry and technical team, and certifies harvest batches." "Director"
        asesor = person "Technical Field Advisor" "Agronomist who attends pest reports, schedules field inspections, and issues technical prescriptions." "Advisor"
        visitante = person "Visitor" "Person browsing the landing page or scanning a certificate QR code to verify it without an account." "Visitor"

        # =====================================================================
        # 2. EXTERNAL SYSTEMS
        # Red = services that send messages to people, orange = business services
        # =====================================================================
        agroMonitoringApi = softwareSystem "AgroMonitoring API" "Satellite and weather service that stores plot polygons and returns NDVI and NDWI imagery and 5-day weather forecasts." "External System, Business Service"
        mercadoPago = softwareSystem "Mercado Pago" "Payment platform (Checkout Pro) that charges the subscription plans and notifies each payment through a webhook." "External System, Business Service"
        brevoApi = softwareSystem "Brevo" "Transactional email service for password recovery, agronomist invitations, and serious agroclimatic alerts." "External System, Notification Service"

        # =====================================================================
        # 3. MAIN SOFTWARE SYSTEM, CONTAINERS AND COMPONENTS
        # =====================================================================
        sumaqAgro = softwareSystem "SumaqAgro Platform" "Web platform for precision agriculture without field hardware: crop monitoring, campaign costs, cooperative management, and harvest certification." {

            # -----------------------------------------------------------------
            # CONTAINER 1: LANDING PAGE (HTML5, CSS3, JS)
            # -----------------------------------------------------------------
            landingPage = container "Landing Page" "Static website with the value proposition, the subscription plans, the About-the-Product video, and a call to action for each segment." "HTML5, CSS3, JavaScript (Cloudflare Pages)" "Folder" {
                landingNav = component "Navigation & Hero Component" "Responsive navigation bar, value proposition, About-the-Product video, and the calls to action that open the Web Application for each segment." "HTML5 / CSS3 Flexbox"
                pricingCatalog = component "Pricing & Plans Catalog Component" "Comparison of the Semilla, Cooperativa Pro, and Asesor Técnico plans." "HTML5 / CSS3 Grid"
            }

            # -----------------------------------------------------------------
            # CONTAINER 2: STATIC CONTENT (delivers the compiled Angular app)
            # -----------------------------------------------------------------
            staticContent = container "Static Content" "HTML, CSS, JavaScript, and assets of the compiled Angular application." "Directory (Firebase Hosting)" "Folder"

            # -----------------------------------------------------------------
            # CONTAINER 3: WEB APPLICATION (ANGULAR SPA)
            # One component per bounded context, each one with the layers
            # domain, application (store), infrastructure (API), and presentation
            # -----------------------------------------------------------------
            webApp = container "Web Application" "Single Page Application for producers, cooperative directors, and agronomists, in English and Spanish. Queues the campaign expenses recorded without connection." "Angular 22, TypeScript, Angular Material, ngx-translate" "Web Browser" {
                iam = component "IAM Component" "Sign-up, sign-in, and password recovery. Its guard protects the private routes and its interceptor adds the JWT to every request." "Angular feature folder src/app/iam: domain, application (store), infrastructure (API), presentation"
                profiles = component "Profiles Component" "Account settings, cooperative registration, member registry, technical team, and agronomist assignment to plots." "Angular feature folder src/app/profiles: domain, application (store), infrastructure (API), presentation"
                subscriptions = component "Subscriptions and Payments Component" "Plans catalog, Mercado Pago checkout, payment result, and billing history." "Angular feature folder src/app/subscriptions: domain, application (store), infrastructure (API), presentation"
                fieldManagement = component "Field Management Component" "Plot registration with the boundary map, crop campaigns, and the campaign cost ledger with an offline queue of expenses." "Angular feature folder src/app/farming: domain, application (store), infrastructure (API), presentation / Leaflet / Chart.js"
                cropHealth = component "Crop Health Component" "NDVI and NDWI map, weather forecast, agroclimatic alerts, pest reports, field inspections, and technical prescriptions." "Angular feature folder src/app/crop: domain, application (store), infrastructure (API), presentation / Leaflet"
                harvestCertification = component "Harvest Certification Component" "Harvest batches, potato caliber and coffee cupping grading, quality certificates, and the public QR verification view." "Angular feature folder src/app/certification: domain, application (store), infrastructure (API), presentation"
                shared = component "Shared Component" "Elements used by every bounded context: layout with the menu by role, language switcher, base API classes, and error handling." "Angular shared folder src/app/shared"
            }

            # -----------------------------------------------------------------
            # CONTAINER 4: RESTFUL WEB SERVICES (SPRING BOOT 4.1)
            # One component per bounded context (6 contexts + Shared Kernel)
            # -----------------------------------------------------------------
            apiBackend = container "RESTful Web Services" "Business logic of the 6 bounded contexts and the Shared Kernel, JWT authentication, RFC 9457 error responses, and OpenAPI documentation." "Java 27, Spring Boot 4.1, Spring Data JPA, Spring Security (Render)" {
                iam = component "IAM Component" "Sign-up, sign-in with JWT, roles, and password recovery. Hashes passwords with BCrypt." "Spring Boot package iam: interfaces (REST, ACL), application, domain, infrastructure"
                profiles = component "Profiles Component" "Profiles, cooperatives, member registry, agronomist invitations, and agronomist assignment to plots." "Spring Boot package profiles: interfaces (REST, ACL), application, domain, infrastructure"
                subscriptions = component "Subscriptions and Payments Component" "Plans (Semilla, Cooperativa Pro, Asesor Técnico), subscriptions, Mercado Pago payments, and the quotas of each plan." "Spring Boot package subscriptions: interfaces (REST, ACL), application, domain, infrastructure"
                fieldManagement = component "Field Management Component" "Plots and their polygons, crop campaigns, campaign cost ledger, and breakeven price." "Spring Boot package farming: interfaces (REST, ACL), application, domain, infrastructure"
                cropHealth = component "Crop Health Component" "Satellite observations, weather forecasts, agroclimatic alerts, pest reports, field inspections, and technical prescriptions." "Spring Boot package crop: interfaces (REST), application, domain, infrastructure"
                harvestCertification = component "Harvest Certification Component" "Harvest batches, potato caliber and coffee cupping grading, quality certificates with QR code, and public verification." "Spring Boot package certification: interfaces (REST), application, domain, infrastructure"
                sharedKernel = component "Shared Kernel Component" "Base aggregate root, Result and ApplicationError, RFC 9457 Problem Details, domain event publisher, email sender port, i18n, and OpenAPI configuration." "Spring Boot package shared"
            }

            # -----------------------------------------------------------------
            # CONTAINER 5: DATABASE
            # -----------------------------------------------------------------
            database = container "Database" "Stores the tables of the 6 bounded contexts: accounts, profiles, subscriptions, plots and campaigns, crop health records, and certificates." "MySQL" "Database"
        }

        # =====================================================================
        # 4. SYSTEM CONTEXT RELATIONSHIPS
        # Declared first so the container and component relationships below
        # do not create duplicated implied relationships.
        # =====================================================================
        productor -> sumaqAgro "Registers plots, records campaign expenses, and monitors crop health" "HTTPS"
        directivo -> sumaqAgro "Manages members and the technical team, and certifies harvest batches" "HTTPS"
        asesor -> sumaqAgro "Attends pest reports and issues prescriptions" "HTTPS"
        visitante -> sumaqAgro "Browses the value proposition and verifies certificates by QR code" "HTTPS"

        sumaqAgro -> agroMonitoringApi "Registers plot polygons and fetches imagery, indexes, and weather forecasts" "JSON/HTTPS"
        sumaqAgro -> mercadoPago "Creates checkout preferences and reads each payment" "JSON/HTTPS"
        mercadoPago -> sumaqAgro "Notifies payments through a webhook" "JSON/HTTPS"
        sumaqAgro -> brevoApi "Sends password recovery, invitation, and alert emails" "JSON/HTTPS"

        # =====================================================================
        # 5. CONTAINER RELATIONSHIPS
        # =====================================================================
        # Actors -> Containers
        productor -> sumaqAgro.webApp "Registers plots, records campaign expenses, and monitors crop health using" "HTTPS"
        directivo -> sumaqAgro.webApp "Manages members and the technical team, and certifies harvest batches using" "HTTPS"
        asesor -> sumaqAgro.webApp "Attends pest reports and issues prescriptions using" "HTTPS"
        visitante -> sumaqAgro.landingPage "Browses the value proposition and subscription plans via" "HTTPS"
        visitante -> sumaqAgro.webApp "Verifies certificates by QR code using" "HTTPS"

        # Static content delivery
        productor -> sumaqAgro.staticContent "Loads the web app from" "HTTPS"
        directivo -> sumaqAgro.staticContent "Loads the web app from" "HTTPS"
        asesor -> sumaqAgro.staticContent "Loads the web app from" "HTTPS"
        visitante -> sumaqAgro.staticContent "Loads the public QR view from" "HTTPS"
        sumaqAgro.staticContent -> sumaqAgro.webApp "Delivers the Angular application to the user's web browser"

        # Inter-Container Relationships
        sumaqAgro.landingPage -> sumaqAgro.webApp "Redirects each segment to its sign-up view at" "HTTPS"
        sumaqAgro.webApp -> sumaqAgro.apiBackend "Makes API calls with the JWT and sends the queued offline expenses to" "JSON/HTTPS"
        sumaqAgro.apiBackend -> sumaqAgro.database "Reads from and writes to" "MySQL protocol / JDBC"

        # RESTful Web Services -> External Systems
        sumaqAgro.apiBackend -> agroMonitoringApi "Registers plot polygons and fetches imagery, indexes, and weather forecasts via" "JSON/HTTPS"
        sumaqAgro.apiBackend -> mercadoPago "Creates checkout preferences and reads each payment via" "JSON/HTTPS"
        mercadoPago -> sumaqAgro.apiBackend "Notifies payments through a webhook to" "JSON/HTTPS"
        sumaqAgro.apiBackend -> brevoApi "Sends password recovery, invitation, and alert emails via" "JSON/HTTPS"

        # =====================================================================
        # 6. COMPONENT RELATIONSHIPS: LANDING PAGE
        # =====================================================================
        visitante -> sumaqAgro.landingPage.landingNav "Browses the value proposition via" "HTTPS"
        sumaqAgro.landingPage.landingNav -> sumaqAgro.landingPage.pricingCatalog "Scrolls to"
        sumaqAgro.landingPage.landingNav -> sumaqAgro.webApp.iam "Opens the sign-in and sign-up view of each segment at" "HTTPS"
        sumaqAgro.landingPage.pricingCatalog -> sumaqAgro.webApp.iam "Redirects visitors to sign up at" "HTTPS"

        # =====================================================================
        # 7. COMPONENT RELATIONSHIPS: WEB APPLICATION (ANGULAR)
        # =====================================================================
        visitante -> sumaqAgro.webApp.iam "Signs up using" "HTTPS"
        visitante -> sumaqAgro.webApp.harvestCertification "Verifies certificates by QR code using" "HTTPS"
        productor -> sumaqAgro.webApp.fieldManagement "Registers plots and records campaign expenses using" "HTTPS"
        productor -> sumaqAgro.webApp.cropHealth "Monitors crop health and reports pests using" "HTTPS"
        productor -> sumaqAgro.webApp.subscriptions "Chooses a plan and pays using" "HTTPS"
        directivo -> sumaqAgro.webApp.profiles "Manages members and the technical team, and assigns agronomists using" "HTTPS"
        directivo -> sumaqAgro.webApp.harvestCertification "Grades harvest batches and issues certificates using" "HTTPS"
        directivo -> sumaqAgro.webApp.subscriptions "Chooses a plan and pays using" "HTTPS"
        asesor -> sumaqAgro.webApp.cropHealth "Attends pest reports and issues prescriptions using" "HTTPS"
        asesor -> sumaqAgro.webApp.subscriptions "Chooses a plan and pays using" "HTTPS"

        # IAM guard and interceptor
        sumaqAgro.webApp.profiles -> sumaqAgro.webApp.iam "Checks the session and role with"
        sumaqAgro.webApp.subscriptions -> sumaqAgro.webApp.iam "Checks the session and role with"
        sumaqAgro.webApp.fieldManagement -> sumaqAgro.webApp.iam "Checks the session and role with"
        sumaqAgro.webApp.cropHealth -> sumaqAgro.webApp.iam "Checks the session and role with"
        sumaqAgro.webApp.harvestCertification -> sumaqAgro.webApp.iam "Checks the session and role with"

        # Reads published by other bounded contexts through their stores
        sumaqAgro.webApp.cropHealth -> sumaqAgro.webApp.fieldManagement "Reads the plots of the user from"
        sumaqAgro.webApp.profiles -> sumaqAgro.webApp.fieldManagement "Reads the plots of each member from"
        sumaqAgro.webApp.harvestCertification -> sumaqAgro.webApp.profiles "Reads the cooperative and its members from"
        sumaqAgro.webApp.fieldManagement -> sumaqAgro.webApp.subscriptions "Reads the plot quota of the plan from"

        # Shared
        sumaqAgro.webApp.iam -> sumaqAgro.webApp.shared "Uses layout and base API classes from"
        sumaqAgro.webApp.shared -> sumaqAgro.webApp.iam "Shows the menu by role using"
        sumaqAgro.webApp.profiles -> sumaqAgro.webApp.shared "Uses layout and base API classes from"
        sumaqAgro.webApp.subscriptions -> sumaqAgro.webApp.shared "Uses layout and base API classes from"
        sumaqAgro.webApp.fieldManagement -> sumaqAgro.webApp.shared "Uses layout and base API classes from"
        sumaqAgro.webApp.cropHealth -> sumaqAgro.webApp.shared "Uses layout and base API classes from"
        sumaqAgro.webApp.harvestCertification -> sumaqAgro.webApp.shared "Uses layout and base API classes from"

        # Each bounded context calls the same bounded context in the RESTful Web Services
        sumaqAgro.webApp.iam -> sumaqAgro.apiBackend.iam "Makes sign-up, sign-in, and password recovery requests to" "JSON/HTTPS"
        sumaqAgro.webApp.profiles -> sumaqAgro.apiBackend.profiles "Makes profile, cooperative, member, and technical team requests to" "JSON/HTTPS"
        sumaqAgro.webApp.subscriptions -> sumaqAgro.apiBackend.subscriptions "Makes plan, checkout, and payment requests to" "JSON/HTTPS"
        sumaqAgro.webApp.fieldManagement -> sumaqAgro.apiBackend.fieldManagement "Makes plot, campaign, and cost ledger requests and sends the queued offline expenses to" "JSON/HTTPS"
        sumaqAgro.webApp.cropHealth -> sumaqAgro.apiBackend.cropHealth "Makes monitoring, alert, and advisory requests to" "JSON/HTTPS"
        sumaqAgro.webApp.harvestCertification -> sumaqAgro.apiBackend.harvestCertification "Makes harvest batch and certificate requests to" "JSON/HTTPS"

        # =====================================================================
        # 8. COMPONENT RELATIONSHIPS: RESTFUL WEB SERVICES (SPRING BOOT)
        # Bounded contexts only talk through anti-corruption layer facades;
        # each domain event is handled inside its own bounded context.
        # =====================================================================
        sumaqAgro.apiBackend.iam -> sumaqAgro.apiBackend.profiles "Creates the profile and accepts agronomist invitations on sign-up through ACL"
        sumaqAgro.apiBackend.profiles -> sumaqAgro.apiBackend.iam "Finds accounts by email and checks their roles through ACL"
        sumaqAgro.apiBackend.profiles -> sumaqAgro.apiBackend.fieldManagement "Reads the owner of each plot through ACL"
        sumaqAgro.apiBackend.profiles -> sumaqAgro.apiBackend.subscriptions "Checks the member and advisory quotas through ACL"
        sumaqAgro.apiBackend.fieldManagement -> sumaqAgro.apiBackend.subscriptions "Checks the plot quota through ACL"
        sumaqAgro.apiBackend.fieldManagement -> sumaqAgro.apiBackend.profiles "Checks the cooperative membership of the producer through ACL"
        sumaqAgro.apiBackend.cropHealth -> sumaqAgro.apiBackend.fieldManagement "Reads plot polygons, regions, and owners through ACL"
        sumaqAgro.apiBackend.cropHealth -> sumaqAgro.apiBackend.iam "Reads the email of the producers to alert through ACL"
        sumaqAgro.apiBackend.harvestCertification -> sumaqAgro.apiBackend.profiles "Validates the cooperative and its members through ACL"
        sumaqAgro.apiBackend.harvestCertification -> sumaqAgro.apiBackend.fieldManagement "Reads the origin plot of each batch through ACL"

        # Shared Kernel
        sumaqAgro.apiBackend.iam -> sumaqAgro.apiBackend.sharedKernel "Uses base classes and the email sender from"
        sumaqAgro.apiBackend.profiles -> sumaqAgro.apiBackend.sharedKernel "Uses base classes and the email sender from"
        sumaqAgro.apiBackend.subscriptions -> sumaqAgro.apiBackend.sharedKernel "Uses base classes from"
        sumaqAgro.apiBackend.fieldManagement -> sumaqAgro.apiBackend.sharedKernel "Uses base classes and value objects from"
        sumaqAgro.apiBackend.cropHealth -> sumaqAgro.apiBackend.sharedKernel "Uses base classes and the email sender from"
        sumaqAgro.apiBackend.harvestCertification -> sumaqAgro.apiBackend.sharedKernel "Uses base classes from"

        # External systems (only the RESTful Web Services integrate with them)
        sumaqAgro.apiBackend.subscriptions -> mercadoPago "Creates checkout preferences and reads each payment using" "JSON/HTTPS"
        mercadoPago -> sumaqAgro.apiBackend.subscriptions "Notifies payments through a webhook to" "JSON/HTTPS"
        sumaqAgro.apiBackend.fieldManagement -> agroMonitoringApi "Registers plot polygons using" "JSON/HTTPS"
        sumaqAgro.apiBackend.cropHealth -> agroMonitoringApi "Fetches NDVI and NDWI imagery and weather forecasts using" "JSON/HTTPS"
        sumaqAgro.apiBackend.iam -> brevoApi "Sends password recovery emails using" "JSON/HTTPS"
        sumaqAgro.apiBackend.profiles -> brevoApi "Sends agronomist invitation emails using" "JSON/HTTPS"
        sumaqAgro.apiBackend.cropHealth -> brevoApi "Sends serious agroclimatic alert emails using" "JSON/HTTPS"

        # Persistence
        sumaqAgro.apiBackend.iam -> sumaqAgro.database "Reads from and writes to" "MySQL protocol / JDBC"
        sumaqAgro.apiBackend.profiles -> sumaqAgro.database "Reads from and writes to" "MySQL protocol / JDBC"
        sumaqAgro.apiBackend.subscriptions -> sumaqAgro.database "Reads from and writes to" "MySQL protocol / JDBC"
        sumaqAgro.apiBackend.fieldManagement -> sumaqAgro.database "Reads from and writes to" "MySQL protocol / JDBC"
        sumaqAgro.apiBackend.cropHealth -> sumaqAgro.database "Reads from and writes to" "MySQL protocol / JDBC"
        sumaqAgro.apiBackend.harvestCertification -> sumaqAgro.database "Reads from and writes to" "MySQL protocol / JDBC"
    }

    views {
        systemContext sumaqAgro "SystemContext" "SumaqAgro Platform System Context Diagram" {
            include *
            properties {
                structurizr.metadata "false"
            }
        }

        container sumaqAgro "Containers" "SumaqAgro Platform Container Diagram" {
            include *
            properties {
                structurizr.metadata "false"
            }
        }

        component sumaqAgro.landingPage "landingComponents" "Landing Page Component Diagram" {
            include *
            properties {
                structurizr.metadata "false"
            }
        }

        component sumaqAgro.webApp "webAppComponents" "Angular Web Application Component Diagram" {
            include *
            properties {
                structurizr.metadata "false"
            }
        }

        component sumaqAgro.apiBackend "apiComponents" "Spring Boot RESTful Web Services Component Diagram" {
            include *
            properties {
                structurizr.metadata "false"
            }
        }

        styles {
            element "Element" {
                color #0773af
                background #ffffff
                stroke #0773af
                strokeWidth 7
                shape roundedbox
            }
            element "Person" {
                shape person
                background #ffffff
                color #2f7d00
                stroke #2f7d00
            }
            element "Component" {
                shape Component
            }
            element "External System" {
                background #ffffff
                strokeWidth 7
            }
            element "Notification Service" {
                color #c4001d
                stroke #c4001d
            }
            element "Business Service" {
                color #f08a00
                stroke #f08a00
            }
            element "Folder" {
                shape Folder
            }
            element "Web Browser" {
                shape WebBrowser
            }
            element "Database" {
                shape Cylinder
            }
            relationship "Relationship" {
                thickness 3
                color #0773af
                dashed false
                fontSize 20
            }
        }
    }
}
