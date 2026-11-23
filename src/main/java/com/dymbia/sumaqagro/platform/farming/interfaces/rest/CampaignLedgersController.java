package com.dymbia.sumaqagro.platform.farming.interfaces.rest;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.CampaignLedgerCommandService;
import com.dymbia.sumaqagro.platform.farming.application.queryservices.CampaignLedgerQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCampaignLedgerByIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.CampaignLedgerResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.EstimateCampaignYieldResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordDailyLaborExpenseResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordFieldFreightExpenseResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordInputExpenseResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.CampaignLedgerResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.EstimateCampaignYieldCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.RecordDailyLaborExpenseCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.RecordFieldFreightExpenseCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.RecordInputExpenseCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
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
 * REST controller for the cost ledgers of the crop campaigns.
 * <p>
 * A ledger is never created from here: it is opened when its campaign starts (US-36).
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/campaign-ledgers", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Campaign Ledgers", description = "Cost ledger endpoints of the crop campaigns")
public class CampaignLedgersController {
    private static final String CAMPAIGN_LEDGER = "CampaignLedger";

    private final CampaignLedgerCommandService campaignLedgerCommandService;
    private final CampaignLedgerQueryService campaignLedgerQueryService;

    public CampaignLedgersController(CampaignLedgerCommandService campaignLedgerCommandService,
                                     CampaignLedgerQueryService campaignLedgerQueryService) {
        this.campaignLedgerCommandService = campaignLedgerCommandService;
        this.campaignLedgerQueryService = campaignLedgerQueryService;
    }

    /**
     * Gets a cost ledger by its id.
     *
     * @param campaignLedgerId the ledger id
     * @return 200 with the ledger, or 404 when it does not exist
     */
    @Operation(summary = "Get a campaign ledger by ID",
            description = "Retrieves a cost ledger with its expenses, total investment and breakeven price.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign ledger found",
                    content = @Content(schema = @Schema(implementation = CampaignLedgerResource.class))),
            @ApiResponse(responseCode = "404", description = "Campaign ledger not found")
    })
    @GetMapping("{campaignLedgerId}")
    public ResponseEntity<?> getCampaignLedgerById(
            @PathVariable
            @Parameter(description = "Unique campaign ledger identifier", example = "1", required = true)
            Long campaignLedgerId) {
        return campaignLedgerQueryService.handle(new GetCampaignLedgerByIdQuery(campaignLedgerId))
                .<ResponseEntity<?>>map(campaignLedger -> ResponseEntity.ok(
                        CampaignLedgerResourceFromEntityAssembler.toResourceFromEntity(campaignLedger)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound(CAMPAIGN_LEDGER, campaignLedgerId.toString())));
    }

    /**
     * Records an input expense: seeds, fertilizers, pesticides (US-37).
     *
     * @param campaignLedgerId the ledger id
     * @param resource         the expense data
     * @return 201 with the ledger, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Record an input expense",
            description = "Records the purchase of an input. Sending the same clientSyncId twice records it once.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Input expense recorded",
                    content = @Content(schema = @Schema(implementation = CampaignLedgerResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid expense data"),
            @ApiResponse(responseCode = "404", description = "Campaign ledger not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The campaign is finished")
    })
    @PostMapping("{campaignLedgerId}/expenses/inputs")
    public ResponseEntity<?> recordInputExpense(
            @PathVariable
            @Parameter(description = "Unique campaign ledger identifier", example = "1", required = true)
            Long campaignLedgerId,
            @RequestBody RecordInputExpenseResource resource) {
        var command = RecordInputExpenseCommandFromResourceAssembler.toCommandFromResource(campaignLedgerId, resource);
        return toResponseWithCampaignLedger(campaignLedgerCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Records a daily labor expense: workers by days by daily wage (US-38).
     *
     * @param campaignLedgerId the ledger id
     * @param resource         the expense data
     * @return 201 with the ledger, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Record a daily labor expense",
            description = "Records the wages of a field activity: workers by days by daily wage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Daily labor expense recorded",
                    content = @Content(schema = @Schema(implementation = CampaignLedgerResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid expense data"),
            @ApiResponse(responseCode = "404", description = "Campaign ledger not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The campaign is finished")
    })
    @PostMapping("{campaignLedgerId}/expenses/labor")
    public ResponseEntity<?> recordDailyLaborExpense(
            @PathVariable
            @Parameter(description = "Unique campaign ledger identifier", example = "1", required = true)
            Long campaignLedgerId,
            @RequestBody RecordDailyLaborExpenseResource resource) {
        var command = RecordDailyLaborExpenseCommandFromResourceAssembler
                .toCommandFromResource(campaignLedgerId, resource);
        return toResponseWithCampaignLedger(campaignLedgerCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Records a field freight expense (US-39).
     *
     * @param campaignLedgerId the ledger id
     * @param resource         the expense data
     * @return 201 with the ledger, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Record a field freight expense", description = "Records the cost of a freight.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Field freight expense recorded",
                    content = @Content(schema = @Schema(implementation = CampaignLedgerResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid expense data"),
            @ApiResponse(responseCode = "404", description = "Campaign ledger not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The campaign is finished")
    })
    @PostMapping("{campaignLedgerId}/expenses/freight")
    public ResponseEntity<?> recordFieldFreightExpense(
            @PathVariable
            @Parameter(description = "Unique campaign ledger identifier", example = "1", required = true)
            Long campaignLedgerId,
            @RequestBody RecordFieldFreightExpenseResource resource) {
        var command = RecordFieldFreightExpenseCommandFromResourceAssembler
                .toCommandFromResource(campaignLedgerId, resource);
        return toResponseWithCampaignLedger(campaignLedgerCommandService.handle(command), HttpStatus.CREATED);
    }

    /**
     * Records the expected and actual yield of the campaign (US-40).
     *
     * @param campaignLedgerId the ledger id
     * @param resource         the yields
     * @return 200 with the ledger, or the problem detail of the error (400, 404 or 409)
     */
    @Operation(summary = "Record the yield of a campaign",
            description = "Replaces the expected and actual yield. They are used to compute the breakeven price.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Yield recorded",
                    content = @Content(schema = @Schema(implementation = CampaignLedgerResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid yield"),
            @ApiResponse(responseCode = "404", description = "Campaign ledger not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - The campaign is finished")
    })
    @PutMapping("{campaignLedgerId}/yield-estimate")
    public ResponseEntity<?> estimateCampaignYield(
            @PathVariable
            @Parameter(description = "Unique campaign ledger identifier", example = "1", required = true)
            Long campaignLedgerId,
            @RequestBody EstimateCampaignYieldResource resource) {
        var command = EstimateCampaignYieldCommandFromResourceAssembler
                .toCommandFromResource(campaignLedgerId, resource);
        return toResponseWithCampaignLedger(campaignLedgerCommandService.handle(command), HttpStatus.OK);
    }

    /**
     * The commands only return the id; the ledger is read back through the query service (CQRS).
     *
     * @param commandResult the result of the command
     * @param successStatus the HTTP status of a successful response
     * @return the ledger response, or the problem detail of the error
     */
    private ResponseEntity<?> toResponseWithCampaignLedger(Result<Long, ApplicationError> commandResult,
                                                           HttpStatusCode successStatus) {
        Result<CampaignLedger, ApplicationError> result = commandResult.flatMap(campaignLedgerId ->
                campaignLedgerQueryService.handle(new GetCampaignLedgerByIdQuery(campaignLedgerId))
                        .<Result<CampaignLedger, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(
                                ApplicationError.notFound(CAMPAIGN_LEDGER, campaignLedgerId.toString()))));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                CampaignLedgerResourceFromEntityAssembler::toResourceFromEntity, successStatus);
    }
}
