package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest;

import com.dymbia.sumaqagro.platform.subscriptions.application.queryservices.PlanQueryService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetAllPlansQuery;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.PlanResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform.PlanResourceFromEntityAssembler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Exposes the subscription plan catalog. */
@RestController
@RequestMapping("/api/v1/plans")
public class PlansController {

    private final PlanQueryService planQueryService;

    public PlansController(PlanQueryService planQueryService) {
        this.planQueryService = planQueryService;
    }

    /** Returns the persisted catalog for the authenticated checkout flow. */
    @GetMapping
    public List<PlanResource> getAllPlans() {
        return planQueryService.handle(new GetAllPlansQuery()).stream()
                .map(PlanResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }
}