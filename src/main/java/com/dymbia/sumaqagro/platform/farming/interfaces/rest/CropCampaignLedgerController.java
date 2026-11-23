package com.dymbia.sumaqagro.platform.farming.interfaces.rest;

import com.dymbia.sumaqagro.platform.farming.application.queryservices.CampaignLedgerQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCampaignLedgerByCropCampaignIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.CampaignLedgerResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.CampaignLedgerResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for the cost ledger of one crop campaign.
 */
@RestController
@RequestMapping(value = "/api/v1/crop-campaigns/{cropCampaignId}/campaign-ledger", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Campaign Ledgers", description = "Cost ledger endpoints of the crop campaigns")
public class CropCampaignLedgerController {
    private final CampaignLedgerQueryService campaignLedgerQueryService;

    public CropCampaignLedgerController(CampaignLedgerQueryService campaignLedgerQueryService) {
        this.campaignLedgerQueryService = campaignLedgerQueryService;
    }

    /**
     * Gets the cost ledger of a crop campaign.
     *
     * @param cropCampaignId the campaign id
     * @return 200 with the ledger, or 404 when the campaign has none
     */
    @Operation(summary = "Get the campaign ledger of a crop campaign",
            description = "Retrieves the cost ledger that was opened when the crop campaign started.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign ledger found",
                    content = @Content(schema = @Schema(implementation = CampaignLedgerResource.class))),
            @ApiResponse(responseCode = "404", description = "The crop campaign has no ledger")
    })
    @GetMapping
    public ResponseEntity<?> getCampaignLedgerByCropCampaignId(
            @PathVariable
            @Parameter(description = "Unique crop campaign identifier", example = "1", required = true)
            Long cropCampaignId) {
        return campaignLedgerQueryService.handle(new GetCampaignLedgerByCropCampaignIdQuery(cropCampaignId))
                .<ResponseEntity<?>>map(campaignLedger -> ResponseEntity.ok(
                        CampaignLedgerResourceFromEntityAssembler.toResourceFromEntity(campaignLedger)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("CampaignLedger", cropCampaignId.toString())));
    }
}
