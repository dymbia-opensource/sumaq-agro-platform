package com.dymbia.sumaqagro.platform.crop.interfaces.rest;

import com.dymbia.sumaqagro.platform.crop.application.queryservices.SatelliteImageryQueryService;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationImageryQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.queries.GetSatelliteObservationTileQuery;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteIndex;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform.SatelliteImageryResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Authenticated metadata and original Web Mercator PNG tiles of a saved observation. */
@RestController
@RequestMapping("/api/v1/satellite-observations/{observationId}/imagery")
@Tag(name = "Satellite Observations")
@SecurityRequirement(name = "sumaqAgroBearer")
public class SatelliteImageryController {
    private final SatelliteImageryQueryService service;
    public SatelliteImageryController(SatelliteImageryQueryService service) { this.service = service; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get available image layers for the exact saved capture")
    public ResponseEntity<?> metadata(@PathVariable Long observationId, @RequestParam Long plotId,
            @AuthenticationPrincipal(expression = "#this instanceof T(org.springframework.security.core.userdetails.UserDetails) ? id : null") Long userId) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntityAssembler.toResponseEntityFromResult(service.handle(
                new GetSatelliteObservationImageryQuery(plotId, observationId, userId)),
                SatelliteImageryResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @GetMapping(value = "/{index}/{zoom}/{x}/{y}", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Get one original satellite index tile; indices are NDVI and NDWI",
            description = "Matches the saved capture date and satellite. Returns 404 when missing, 400 for invalid coordinates, or 500 when the provider fails. Credentials are never returned.")
    public ResponseEntity<?> tile(@PathVariable Long observationId, @RequestParam Long plotId,
            @PathVariable SatelliteIndex index, @PathVariable int zoom, @PathVariable int x, @PathVariable int y,
            @AuthenticationPrincipal(expression = "#this instanceof T(org.springframework.security.core.userdetails.UserDetails) ? id : null") Long userId) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        var result = service.handle(new GetSatelliteObservationTileQuery(
                new GetSatelliteObservationImageryQuery(plotId, observationId, userId), index, zoom, x, y));
        if (result instanceof Result.Failure<byte[], ?> failure) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    (com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError) failure.error());
        }
        var bytes = result.toOptional().orElseThrow();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff").body(bytes);
    }
}
