package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.PaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
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
@RequestMapping("/api/v1/subscriptions/me")
@ConditionalOnProperty(name = "subscriptions.demo-payments.enabled", havingValue = "true")
public class CheckoutController {
    private final SubscriptionCommandService subscriptionCommandService;

    public CheckoutController(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }

    /** Confirms checkout for the authenticated account. */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody CheckoutResource resource
    ) {
        if (!canSelectPlan(principal, resource.planCode())) {
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

    private boolean canSelectPlan(UserDetailsImpl principal, PlanCode planCode) {
        var authority = switch (planCode) {
            case SEED -> "ROLE_FARMER";
            case COOPERATIVE_PRO -> "ROLE_COOPERATIVE_DIRECTOR";
            case TECHNICAL_ADVISOR -> "ROLE_AGRONOMIST";
        };
        return principal.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }
}
