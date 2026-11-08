package com.dymbia.sumaqagro.platform.certification.interfaces.rest;

import com.dymbia.sumaqagro.platform.certification.application.commandservices.HarvestBatchCommandService;
import com.dymbia.sumaqagro.platform.certification.application.queryservices.HarvestCertificationQueryService;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetHarvestBatchByIdQuery;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.CupCoffeeResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.ExtractRepresentativeSampleResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.GradePotatoCalibersResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.RegisterHarvestBatchResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.WeighHarvestBatchResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.CupCoffeeCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.ExtractRepresentativeSampleCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.GradePotatoCalibersCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.HarvestBatchResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.RegisterHarvestBatchCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.WeighHarvestBatchCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for harvest batches.
 * It only translates HTTP: the rules live in the services and the errors are built by the shared assemblers.
 */
@RestController
@RequestMapping(value = "/api/v1/harvest-batches", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Harvest Batches", description = "Harvest batch registration, weighing, sampling and grading")
public class HarvestBatchesController {

    private final HarvestBatchCommandService harvestBatchCommandService;
    private final HarvestCertificationQueryService harvestCertificationQueryService;

    public HarvestBatchesController(HarvestBatchCommandService harvestBatchCommandService,
                                    HarvestCertificationQueryService harvestCertificationQueryService) {
        this.harvestBatchCommandService = harvestBatchCommandService;
        this.harvestCertificationQueryService = harvestCertificationQueryService;
    }

    /**
     * Registers a harvest batch.
     *
     * @param resource the batch data sent by the client
     * @return 201 with the new batch, or the problem detail of the error
     */
    @Operation(summary = "Register a harvest batch")
    @PostMapping
    public ResponseEntity<?> registerHarvestBatch(@RequestBody RegisterHarvestBatchResource resource) {
        var command = RegisterHarvestBatchCommandFromResourceAssembler.toCommandFromResource(resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                harvestBatchCommandService.handle(command),
                HarvestBatchResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    /**
     * Gets a harvest batch by its id.
     *
     * @param harvestBatchId the batch id
     * @return 200 with the batch, or 404 when it does not exist
     */
    @Operation(summary = "Get a harvest batch by ID")
    @GetMapping("{harvestBatchId}")
    public ResponseEntity<?> getHarvestBatchById(@PathVariable Long harvestBatchId) {
        return harvestCertificationQueryService.handle(new GetHarvestBatchByIdQuery(harvestBatchId))
                .<ResponseEntity<?>>map(batch -> ResponseEntity.ok(HarvestBatchResourceFromEntityAssembler.toResourceFromEntity(batch)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("HarvestBatch", harvestBatchId.toString())));
    }

    /**
     * Records the net weight of a harvest batch.
     *
     * @param harvestBatchId the batch id
     * @param resource gross weight and tare
     * @return 200 with the weighed batch, or the problem detail of the error
     */
    @Operation(summary = "Weigh a harvest batch")
    @PostMapping("{harvestBatchId}/weight")
    public ResponseEntity<?> weighHarvestBatch(@PathVariable Long harvestBatchId,
                                               @RequestBody WeighHarvestBatchResource resource) {
        var command = WeighHarvestBatchCommandFromResourceAssembler.toCommandFromResource(harvestBatchId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                harvestBatchCommandService.handle(command),
                HarvestBatchResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    /**
     * Attaches the representative sample of a harvest batch.
     *
     * @param harvestBatchId the batch id
     * @param resource sample identifier and weight
     * @return 200 with the sampled batch, or the problem detail of the error
     */
    @Operation(summary = "Extract a representative sample")
    @PostMapping("{harvestBatchId}/sample")
    public ResponseEntity<?> extractRepresentativeSample(@PathVariable Long harvestBatchId,
                                                         @RequestBody ExtractRepresentativeSampleResource resource) {
        var command = ExtractRepresentativeSampleCommandFromResourceAssembler.toCommandFromResource(harvestBatchId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                harvestBatchCommandService.handle(command),
                HarvestBatchResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    /**
     * Records the potato calibers of a harvest batch.
     *
     * @param harvestBatchId the batch id
     * @param resource caliber percentages
     * @return 200 with the graded batch, or the problem detail of the error
     */
    @Operation(summary = "Grade potato calibers")
    @PostMapping("{harvestBatchId}/potato-calibers")
    public ResponseEntity<?> gradePotatoCalibers(@PathVariable Long harvestBatchId,
                                                 @RequestBody GradePotatoCalibersResource resource) {
        var command = GradePotatoCalibersCommandFromResourceAssembler.toCommandFromResource(harvestBatchId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                harvestBatchCommandService.handle(command),
                HarvestBatchResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    /**
     * Records the coffee cupping of a harvest batch.
     *
     * @param harvestBatchId the batch id
     * @param resource cupping scores
     * @return 200 with the cupped batch, or the problem detail of the error
     */
    @Operation(summary = "Record a coffee cupping")
    @PostMapping("{harvestBatchId}/coffee-cupping")
    public ResponseEntity<?> cupCoffee(@PathVariable Long harvestBatchId, @RequestBody CupCoffeeResource resource) {
        var command = CupCoffeeCommandFromResourceAssembler.toCommandFromResource(harvestBatchId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                harvestBatchCommandService.handle(command),
                HarvestBatchResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
