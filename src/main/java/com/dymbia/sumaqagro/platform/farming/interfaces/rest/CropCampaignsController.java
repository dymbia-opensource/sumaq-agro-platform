package com.dymbia.sumaqagro.platform.farming.interfaces.rest;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.CropCampaignCommandService;
import com.dymbia.sumaqagro.platform.farming.application.queryservices.CropCampaignQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.CloseCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCropCampaignByIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordSowingDateResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.SelectCropTypeResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.SpecifySeedVarietyResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.StartCropCampaignResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.CropCampaignResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.RecordSowingDateCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.SelectCropTypeCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.SpecifySeedVarietyCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.StartCropCampaignCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.resources.MessageResource;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Start a crop campaign")
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
    @Operation(summary = "Get a crop campaign by ID")
    @GetMapping("{cropCampaignId}")
    public ResponseEntity<?> getCropCampaignById(@PathVariable Long cropCampaignId) {
        return cropCampaignQueryService.handle(new GetCropCampaignByIdQuery(cropCampaignId))
                .<ResponseEntity<?>>map(cropCampaign -> ResponseEntity.ok(
                        CropCampaignResourceFromEntityAssembler.toResourceFromEntity(cropCampaign)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("CropCampaign", cropCampaignId.toString())));
    }

    /**
     * Changes the crop of a campaign in progress.
     *
     * @param cropCampaignId the campaign id
     * @param resource       the new crop type
     * @return 200 with the campaign, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Select the crop type of a crop campaign")
    @PutMapping("{cropCampaignId}/crop-type")
    public ResponseEntity<?> selectCropType(@PathVariable Long cropCampaignId,
                                            @RequestBody SelectCropTypeResource resource) {
        var command = SelectCropTypeCommandFromResourceAssembler.toCommandFromResource(cropCampaignId, resource);
        return toResponseWithCropCampaign(cropCampaignCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Changes the seed variety of a campaign in progress.
     *
     * @param cropCampaignId the campaign id
     * @param resource       the new seed variety
     * @return 200 with the campaign, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Specify the seed variety of a crop campaign")
    @PutMapping("{cropCampaignId}/seed-variety")
    public ResponseEntity<?> specifySeedVariety(@PathVariable Long cropCampaignId,
                                                @RequestBody SpecifySeedVarietyResource resource) {
        var command = SpecifySeedVarietyCommandFromResourceAssembler.toCommandFromResource(cropCampaignId, resource);
        return toResponseWithCropCampaign(cropCampaignCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Records the sowing date of a campaign in progress. It starts the satellite monitoring (policy P4).
     *
     * @param cropCampaignId the campaign id
     * @param resource       the sowing date
     * @return 200 with the campaign, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Record the sowing date of a crop campaign")
    @PutMapping("{cropCampaignId}/sowing-date")
    public ResponseEntity<?> recordSowingDate(@PathVariable Long cropCampaignId,
                                              @RequestBody RecordSowingDateResource resource) {
        var command = RecordSowingDateCommandFromResourceAssembler.toCommandFromResource(cropCampaignId, resource);
        return toResponseWithCropCampaign(cropCampaignCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * Finishes a campaign in progress.
     *
     * @param cropCampaignId the campaign id
     * @return 200 with a confirmation message, or the problem detail of the error (404 or 409)
     */
    @Operation(summary = "Close a crop campaign")
    @PostMapping("{cropCampaignId}/closures")
    public ResponseEntity<?> closeCropCampaign(@PathVariable Long cropCampaignId) {
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
