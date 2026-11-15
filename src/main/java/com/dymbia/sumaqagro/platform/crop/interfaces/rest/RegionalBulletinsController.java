package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.RegionalBulletinCommandService;
import com.dymbia.sumaqagro.platform.crop.application.queryservices.RegionalBulletinQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetRegionalBulletinsByRegionQuery;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.PublishRegionalBulletinResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RegionalBulletinResource;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.PublishRegionalBulletinCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.RegionalBulletinResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for publishing and querying regional bulletins.
 */
@RestController
@RequestMapping(value = "/api/v1/regional-bulletins", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Regional Bulletins", description = "Regional bulletin endpoints")
public class RegionalBulletinsController {

    private final RegionalBulletinCommandService commandService;
    private final RegionalBulletinQueryService queryService;

    public RegionalBulletinsController(RegionalBulletinCommandService commandService, RegionalBulletinQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    /**
     * Publishes a new regional bulletin.
     *
     * @param resource the resource containing the bulletin data
     * @return ResponseEntity with the created bulletin or an error
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Publish a regional bulletin")
    @ApiResponse(responseCode = "201", description = "Bulletin published",
            content = @Content(schema = @Schema(implementation = RegionalBulletinResource.class)))
    public ResponseEntity<?> publishRegionalBulletin(@RequestBody PublishRegionalBulletinResource resource) {
        var command = PublishRegionalBulletinCommandFromResourceAssembler
                .toCommandFromResource(resource);
        var result = commandService.handle(command);

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                RegionalBulletinResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves regional bulletins by region.
     *
     * @param region the region to filter by
     * @return ResponseEntity with the list of regional bulletins or an error
     */
    @GetMapping
    @Operation(summary = "Get regional bulletins by region")
    public ResponseEntity<List<RegionalBulletinResource>> getRegionalBulletinsByRegion(@RequestParam("region") String region) {
        var query = new GetRegionalBulletinsByRegionQuery(region);
        var resources = queryService.handle(query)
                .stream()
                .map(RegionalBulletinResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }
}
