package com.dymbia.sumaqagro.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configures the OpenAPI specification exposed by the platform.
 * <p>
 * The document is served by Swagger UI and declares the JWT bearer scheme,
 * so the protected endpoints can be tried with the token returned by sign-in.
 * </p>
 *
 * @since 1.0
 */
@Configuration
public class OpenApiConfiguration {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Value("${spring.application.name}")
    String applicationName;

    @Value("${documentation.application.description}")
    String applicationDescription;

    @Value("${documentation.application.version}")
    String applicationVersion;

    @Value("${documentation.server.url:http://localhost:8080}")
    String serverUrl;

    /**
     * Builds the OpenAPI document used by Swagger UI and client generation tools.
     *
     * @return configured OpenAPI descriptor
     */
    @Bean
    public OpenAPI sumaqAgroPlatformOpenApi() {
        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title(this.applicationName)
                        .description(this.applicationDescription)
                        .version(this.applicationVersion)
                        .contact(new Contact()
                                .name("Dymbia")
                                .url("https://github.com/dymbia-opensource"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("SumaqAgro Platform Documentation")
                        .url("https://github.com/dymbia-opensource/sumaqAgro-report"));

        openApi.servers(List.of(
                new Server()
                        .url(this.serverUrl)
                        .description("SumaqAgro Platform API")
        ));

        openApi.addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer token for API authentication")));

        return openApi;
    }
}
