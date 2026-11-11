package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.AgroclimaticAlertQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetAgroclimaticAlertsByRegionQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.AgroclimaticAlertResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.AgroclimaticAlertResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for querying agroclimatic alerts.
 */
@RestController
@RequestMapping(value = "/api/v1/agroclimatic-alerts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Agroclimatic Alerts", description = "Agroclimatic alerts by region")
public class AgroclimaticAlertsController {

    private final AgroclimaticAlertQueryService queryService;

    public AgroclimaticAlertsController(AgroclimaticAlertQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(summary = "Get agroclimatic alerts by region")
    public ResponseEntity<List<AgroclimaticAlertResource>> getAgroclimaticAlertsByRegion(@RequestParam("region") String region) {
        var query = new GetAgroclimaticAlertsByRegionQuery(region);
        var resources = queryService.handle(query)
                .stream()
                .map(AgroclimaticAlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}
