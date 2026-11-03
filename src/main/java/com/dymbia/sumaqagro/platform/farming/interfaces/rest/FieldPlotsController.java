package com.dymbia.sumaqagro.platform.farming.interfaces.rest;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.FieldPlotCommandService;
import com.dymbia.sumaqagro.platform.farming.application.queryservices.FieldPlotQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetAllFieldPlotsByOwnerUserIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.FieldPlotResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RegisterFieldPlotResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.FieldPlotResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform.RegisterFieldPlotCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.shared.application.error.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.advice.ApplicationErrorHttpMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Inbound service in the interface layer for the field plots of the field management bounded context.
 * POST operations delegate to {@link FieldPlotCommandService}; GET operations delegate
 * to {@link FieldPlotQueryService}. Translates between HTTP resources and domain models
 * using assemblers.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/field-plots", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Field Plots", description = "Field plot registration and lookup endpoints")
public class FieldPlotsController {
    private final FieldPlotCommandService fieldPlotCommandService;
    private final FieldPlotQueryService fieldPlotQueryService;
    private final MessageSource messageSource;

    /**
     * Constructor for FieldPlotsController.
     *
     * @param fieldPlotCommandService field plot command service
     * @param fieldPlotQueryService   field plot query service
     * @param messageSource           source for localized response messages
     * @see FieldPlotCommandService
     * @see FieldPlotQueryService
     * @since 1.0
     */
    public FieldPlotsController(FieldPlotCommandService fieldPlotCommandService,
                                FieldPlotQueryService fieldPlotQueryService, MessageSource messageSource) {
        this.fieldPlotCommandService = fieldPlotCommandService;
        this.fieldPlotQueryService = fieldPlotQueryService;
        this.messageSource = messageSource;
    }

    /**
     * Registers a new field plot for a producer.
     *
     * @param resource the request payload with the owner, name, region and declared area
     * @return 201 with the registered field plot, 400 when the data is invalid,
     * or 409 when the name is duplicated or the plot quota was reached
     * @see RegisterFieldPlotResource
     * @see FieldPlotResource
     * @since 1.0
     */
    @Operation(summary = "Register a field plot")
    @PostMapping
    public ResponseEntity<?> registerFieldPlot(@RequestBody RegisterFieldPlotResource resource) {
        var command = RegisterFieldPlotCommandFromResourceAssembler.toCommandFromResource(resource);
        return fieldPlotCommandService.handle(command).fold(fieldPlot -> ResponseEntity.status(HttpStatus.CREATED).body(FieldPlotResourceFromEntityAssembler.toResourceFromEntity(fieldPlot)), this::toProblemDetail);
    }

    /**
     * Gets a field plot by its ID.
     *
     * @param fieldPlotId field plot ID
     * @return 200 with the field plot resource if found, or 404 otherwise
     * @see FieldPlotResource
     * @since 1.0
     */
    @Operation(summary = "Get a field plot by ID")
    @GetMapping("{fieldPlotId}")
    public ResponseEntity<?> getFieldPlotById(@PathVariable Long fieldPlotId) {
        return fieldPlotQueryService.handle(new GetFieldPlotByIdQuery(fieldPlotId)).<ResponseEntity<?>>map(fieldPlot -> ResponseEntity.ok(FieldPlotResourceFromEntityAssembler.toResourceFromEntity(fieldPlot))).orElseGet(() -> toProblemDetail(ApplicationError.notFound("field.plot.error.notFound")));
    }

    /**
     * Gets all the field plots of a producer.
     *
     * @param ownerUserId ID of the producer who owns the plots
     * @return 200 with the list of field plot resources; empty when the producer has no plots
     * @see FieldPlotResource
     * @since 1.0
     */
    @Operation(summary = "Get the field plots of a producer")
    @GetMapping
    public ResponseEntity<List<FieldPlotResource>> getFieldPlotsByOwnerUserId(@RequestParam Long ownerUserId) {
        var fieldPlots = fieldPlotQueryService.handle(new GetAllFieldPlotsByOwnerUserIdQuery(ownerUserId));
        return ResponseEntity.ok(fieldPlots.stream().map(FieldPlotResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    /**
     * Converts an application error into a localized problem detail response.
     *
     * @param error application error with a message key
     * @return a response entity with the HTTP status of the error and its translated message
     */
    private ResponseEntity<ProblemDetail> toProblemDetail(ApplicationError error) {
        var status = ApplicationErrorHttpMapper.toHttpStatus(error);
        var detail = messageSource.getMessage(error.message(), null, error.message(), LocaleContextHolder.getLocale());

        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

}
