package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.PaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.CheckoutResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Exposes checkout for environments configured to accept university payments. */
@RestController
@Tag(name = "Subscriptions", description = "Subscription and payments of the authenticated account")
@RequestMapping("/api/v1/subscriptions/me")
@ConditionalOnProperty(name = "subscriptions.demo-payments.enabled", havingValue = "true")
public class CheckoutController {
    private final SubscriptionCommandService subscriptionCommandService;

    public CheckoutController(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }

    /** Confirms checkout for the authenticated account. */
    @Operation(summary = "Pay a plan with the demo checkout",
            description = "Only exists when subscriptions.demo-payments.enabled is true. simulatedOutcome chooses a completed or rejected payment.")
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody CheckoutResource resource
    ) {
        if (!PlanSelectionAccess.canSelectPlan(principal, resource.planCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var outcome = resource.simulatedOutcome() == null
                ? PaymentStatus.COMPLETED : resource.simulatedOutcome();
        var command = new PaymentCommand(
                principal.getId(), resource.planCode(), resource.billingCycle(), outcome);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                subscriptionCommandService.handle(command),
                PaymentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

}
