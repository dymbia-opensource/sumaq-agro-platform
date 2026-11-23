package com.dymbia.sumaqagro.platform.shared.interfaces.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Checks the HTTP status that the {@link GlobalExceptionHandler} gives to the standard
 * Spring MVC and Spring Security exceptions, which must not become internal server errors.
 */
class GlobalExceptionHandlerWebMvcTests {

    private MockMvc mockMvc;
    private Locale previousDefaultLocale;

    record ProbeResource(String name) {
    }

    @RestController
    static class ProbeController {

        @PostMapping("/probes")
        String create(@RequestBody ProbeResource resource) {
            return resource.name();
        }

        @GetMapping("/probes/{probeId}")
        String getById(@PathVariable Long probeId) {
            return probeId.toString();
        }

        @GetMapping("/probes")
        String getByOwner(@RequestParam Long ownerUserId) {
            return ownerUserId.toString();
        }

        @GetMapping("/secured-probes")
        String secured() {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @BeforeEach
    void setUp() {
        previousDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("es-PE"));
        mockMvc = MockMvcBuilders.standaloneSetup(new ProbeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(previousDefaultLocale);
    }

    @Test
    void malformedJsonBodyIsBadRequest() throws Exception {
        mockMvc.perform(post("/probes").contentType(MediaType.APPLICATION_JSON).content("{bad"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pathVariableOfTheWrongTypeIsBadRequest() throws Exception {
        mockMvc.perform(get("/probes/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingRequestParameterIsBadRequest() throws Exception {
        mockMvc.perform(get("/probes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unsupportedMethodIsMethodNotAllowed() throws Exception {
        mockMvc.perform(delete("/probes/1"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void accessDeniedIsForbiddenInTheLanguageOfTheRequest() throws Exception {
        mockMvc.perform(get("/secured-probes").header("Accept-Language", "en"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.detail").value("You do not have permission to perform this action."));
    }

    @Test
    void accessDeniedIsAnsweredInSpanishWhenTheRequestAsksForIt() throws Exception {
        mockMvc.perform(get("/secured-probes").header("Accept-Language", "es"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("No tienes permiso para realizar esta acción."));
    }
}
