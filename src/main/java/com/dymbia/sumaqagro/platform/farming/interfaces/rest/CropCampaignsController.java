package com.dymbia.sumaqagro.platform.farming.interfaces.rest;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.CropCampaignCommandService;
import com.dymbia.sumaqagro.platform.farming.application.queryservices.CropCampaignQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.CloseCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCropCampaignByIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.CropCampaignResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.StartCropCampaignResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.UpdateCropCampaignResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.CropCampaignResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.StartCropCampaignCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.UpdateCropCampaignCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.resources.MessageResource;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for crop campaigns.
 * It only translates HTTP: the rules live in the services and the aggregate.
 */
@RestController
@RequestMapping(value = "/api/v1/crop-campaigns", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Crop Campaigns", description = "Crop campaign management endpoints")
public class CropCampaignsController {
    private final CropCampaignCommandService cropCampaignCommandService;
    private final CropCampaignQueryService cropCampaignQueryService;

    public CropCampaignsController(CropCampaignCommandService cropCampaignCommandService,
                                   CropCampaignQueryService cropCampaignQueryService) {
        this.cropCampaignCommandService = cropCampaignCommandService;
        this.cropCampaignQueryService = cropCampaignQueryService;
    }

    /**
     * Starts a crop campaign on a field plot.
     *
     * @param resource the campaign data sent by the client
     * @return 201 with the new campaign, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Start a crop campaign",
            description = "Starts a crop campaign on a field plot. A plot can only have one campaign in progress.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Crop campaign started",
                    content = @Content(schema = @Schema(implementation = CropCampaignResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid campaign data"),
            @ApiResponse(responseCode = "404", description = "Field plot not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The plot already has a campaign in progress")
    })
    @PostMapping
    public ResponseEntity<?> startCropCampaign(@RequestBody StartCropCampaignResource resource) {
        var command = StartCropCampaignCommandFromResourceAssembler.toCommandFromResource(resource);
        return toResponseWithCropCampaign(cropCampaignCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Gets a crop campaign by its id.
     *
     * @param cropCampaignId the campaign id
     * @return 200 with the campaign, or 404 when it does not exist
     */
    @Operation(summary = "Get a crop campaign by ID", description = "Retrieves a crop campaign by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crop campaign found",
                    content = @Content(schema = @Schema(implementation = CropCampaignResource.class))),
            @ApiResponse(responseCode = "404", description = "Crop campaign not found")
    })
    @GetMapping("{cropCampaignId}")
    public ResponseEntity<?> getCropCampaignById(
            @PathVariable
            @Parameter(description = "Unique crop campaign identifier", example = "1", required = true)
            Long cropCampaignId) {
        return cropCampaignQueryService.handle(new GetCropCampaignByIdQuery(cropCampaignId))
                .<ResponseEntity<?>>map(cropCampaign -> ResponseEntity.ok(
                        CropCampaignResourceFromEntityAssembler.toResourceFromEntity(cropCampaign)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("CropCampaign", cropCampaignId.toString())));
    }

    /**
     * Changes the crop type, the seed variety or the sowing date of a campaign in progress.
     * Only the fields that are sent change; the sowing date starts the satellite monitoring (policy P4).
     *
     * @param cropCampaignId the campaign id
     * @param resource       the fields to change
     * @return 200 with the campaign, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Update a crop campaign",
            description = "Changes the crop type, the seed variety or the sowing date of a campaign in progress. "
                    + "Only the fields that are sent change.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crop campaign updated",
                    content = @Content(schema = @Schema(implementation = CropCampaignResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or no field to change"),
            @ApiResponse(responseCode = "404", description = "Crop campaign not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The campaign is finished")
    })
    @PatchMapping("{cropCampaignId}")
    public ResponseEntity<?> updateCropCampaign(
            @PathVariable
            @Parameter(description = "Unique crop campaign identifier", example = "1", required = true)
            Long cropCampaignId,
            @RequestBody UpdateCropCampaignResource resource) {
        var command = UpdateCropCampaignCommandFromResourceAssembler.toCommandFromResource(cropCampaignId, resource);
        return toResponseWithCropCampaign(cropCampaignCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Finishes a campaign in progress.
     *
     * @param cropCampaignId the campaign id
     * @return 200 with a confirmation message, or the problem detail of the error (404 or 409)
     */
    @Operation(summary = "Close a crop campaign",
            description = "Finishes a campaign in progress. After this it accepts no more changes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crop campaign closed",
                    content = @Content(schema = @Schema(implementation = MessageResource.class))),
            @ApiResponse(responseCode = "404", description = "Crop campaign not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The campaign is already finished")
    })
    @PostMapping("{cropCampaignId}/closures")
    public ResponseEntity<?> closeCropCampaign(
            @PathVariable
            @Parameter(description = "Unique crop campaign identifier", example = "1", required = true)
            Long cropCampaignId) {
        var result = cropCampaignCommandService.handle(new CloseCropCampaignCommand(cropCampaignId))
                .map(id -> new MessageResource("Crop campaign closed successfully"));
        return ResponseEntityAssembler.toResponseEntityFromResult(result, message -> message, HttpStatus.OK);
    }

    /**
     * The commands only return the id; the campaign is read back through the query service (CQRS).
     *
     * @param commandResult the result of the command
     * @param successStatus the HTTP status of a successful response
     * @return the campaign response, or the problem detail of the error
     */
    private ResponseEntity<?> toResponseWithCropCampaign(Result<Long, ApplicationError> commandResult,
                                                         HttpStatusCode successStatus) {
        Result<CropCampaign, ApplicationError> result = commandResult.flatMap(cropCampaignId ->
                cropCampaignQueryService.handle(new GetCropCampaignByIdQuery(cropCampaignId))
                        .<Result<CropCampaign, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(
                                ApplicationError.notFound("CropCampaign", cropCampaignId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                CropCampaignResourceFromEntityAssembler::toResourceFromEntity, successStatus);
    }
}
