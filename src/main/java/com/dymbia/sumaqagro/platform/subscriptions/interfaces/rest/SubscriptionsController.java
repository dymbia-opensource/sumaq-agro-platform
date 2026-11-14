package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.subscriptions.application.queryservices.SubscriptionQueryService;
import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.CancelSubscriptionCommand;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetPaymentsByUserIdQuery;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.PaymentResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.SubscriptionResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes subscription operations for the authenticated account.
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionsController {

    private final SubscriptionQueryService subscriptionQueryService;

    private final SubscriptionCommandService subscriptionCommandService;

    public SubscriptionsController(
            SubscriptionQueryService subscriptionQueryService,
            SubscriptionCommandService subscriptionCommandService
    ) {
        this.subscriptionQueryService = subscriptionQueryService;
        this.subscriptionCommandService = subscriptionCommandService;
    }

    /** Cancels renewal for the authenticated account. */
    @PostMapping("/me/cancel")
    public ResponseEntity<?> cancelMySubscription(@AuthenticationPrincipal UserDetailsImpl principal) {
        var result = subscriptionCommandService.handle(new CancelSubscriptionCommand(principal.getId()));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK
        );
    }

    /**
     * Returns the caller's subscription, or 204 if no subscription exists.
     */
    @GetMapping("/me")
    public ResponseEntity<SubscriptionResource> getMySubscription(
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        return subscriptionQueryService.handle(new GetSubscriptionByUserIdQuery(principal.getId()))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** Returns the authenticated account's checkout history, newest first. */
    @GetMapping("/me/payments")
    public List<PaymentResource> getMyPayments(@AuthenticationPrincipal UserDetailsImpl principal) {
        return subscriptionQueryService.handle(new GetPaymentsByUserIdQuery(principal.getId()))
                .stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }
}
