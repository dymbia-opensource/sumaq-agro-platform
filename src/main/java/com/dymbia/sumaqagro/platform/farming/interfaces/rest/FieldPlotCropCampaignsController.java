package com.dymbia.sumaqagro.platform.farming.interfaces.rest;

import com.dymbia.sumaqagro.platform.farming.application.queryservices.CropCampaignQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetAllCropCampaignsByFieldPlotIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.CropCampaignResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.CropCampaignResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for the crop campaigns of one field plot.
 */
@RestController
@RequestMapping(value = "/api/v1/field-plots/{fieldPlotId}/crop-campaigns", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Field Plots", description = "Field plot registration and lookup endpoints")
public class FieldPlotCropCampaignsController {
    private final CropCampaignQueryService cropCampaignQueryService;

    public FieldPlotCropCampaignsController(CropCampaignQueryService cropCampaignQueryService) {
        this.cropCampaignQueryService = cropCampaignQueryService;
    }

    /**
     * Gets the crop campaigns of a field plot.
     *
     * @param fieldPlotId the field plot id
     * @return 200 with the campaigns, or an empty list when the plot has none
     */
    @Operation(summary = "Get the crop campaigns of a field plot",
            description = "Retrieves every crop campaign, in progress or finished, of a field plot.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crop campaigns retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CropCampaignResource.class))))
    })
    @GetMapping
    public ResponseEntity<List<CropCampaignResource>> getCropCampaignsByFieldPlotId(
            @PathVariable
            @Parameter(description = "Unique field plot identifier", example = "1", required = true)
            Long fieldPlotId) {
        var cropCampaigns = cropCampaignQueryService.handle(new GetAllCropCampaignsByFieldPlotIdQuery(fieldPlotId));
        return ResponseEntity.ok(cropCampaigns.stream()
                .map(CropCampaignResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
}
