package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.SubscriptionCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.ConfirmPaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.StartPaymentCheckoutCommand;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.ConfirmPaymentResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.PaymentCheckoutResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.PaymentResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.StartPaymentCheckoutResource;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller to pay a plan with Mercado Pago Checkout Pro.
 * <ol>
 *     <li>{@code POST /checkout-sessions}: records a pending payment and answers the Mercado Pago page.</li>
 *     <li>The frontend redirects the producer there; Mercado Pago sends them back with {@code payment_id}.</li>
 *     <li>{@code POST /payment-confirmations}: the back reads that payment from Mercado Pago and applies it.</li>
 * </ol>
 *
 * @since 1.1
 */
@RestController
@RequestMapping(value = "/api/v1/subscriptions/me", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Subscription and payment endpoints")
public class PaymentCheckoutsController {

    private final SubscriptionCommandService subscriptionCommandService;

    public PaymentCheckoutsController(SubscriptionCommandService subscriptionCommandService) {
        this.subscriptionCommandService = subscriptionCommandService;
    }

    @Operation(summary = "Start paying a plan with Mercado Pago",
            description = "Records a pending payment and creates a Mercado Pago Checkout Pro preference. "
                    + "Redirect the producer to checkoutUrl; with test credentials pay with the test buyer account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Checkout created",
                    content = @Content(schema = @Schema(implementation = PaymentCheckoutResource.class))),
            @ApiResponse(responseCode = "403", description = "The plan does not match the role of the producer"),
            @ApiResponse(responseCode = "409", description = "The plan does not match the existing subscription"),
            @ApiResponse(responseCode = "500", description = "Mercado Pago is not configured or not available")
    })
    @PostMapping(value = "/checkout-sessions", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startPaymentCheckout(@AuthenticationPrincipal UserDetailsImpl principal,
                                                  @Valid @RequestBody StartPaymentCheckoutResource resource) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!PlanSelectionAccess.canSelectPlan(principal, resource.planCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var command = new StartPaymentCheckoutCommand(principal.getId(), resource.planCode(), resource.billingCycle());
        return ResponseEntityAssembler.toResponseEntityFromResult(subscriptionCommandService.handle(command),
                checkout -> new PaymentCheckoutResource(checkout.reference(), checkout.amount(), checkout.currency(),
                        checkout.checkoutUrl(), checkout.sandboxCheckoutUrl()),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Confirm a Mercado Pago payment",
            description = "Send the payment_id that Mercado Pago adds to the return URL. The back reads the payment "
                    + "from Mercado Pago, checks that it is yours and that the amount matches, and activates or "
                    + "renews the subscription when it is approved. Confirming twice changes nothing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment with its outcome (COMPLETED, REJECTED or PENDING)",
                    content = @Content(schema = @Schema(implementation = PaymentResource.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found or not yours"),
            @ApiResponse(responseCode = "409", description = "The charged amount does not match the plan")
    })
    @PostMapping(value = "/payment-confirmations", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmPayment(@AuthenticationPrincipal UserDetailsImpl principal,
                                            @Valid @RequestBody ConfirmPaymentResource resource) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var command = new ConfirmPaymentCommand(resource.paymentId(), principal.getId());
        return ResponseEntityAssembler.toResponseEntityFromResult(subscriptionCommandService.handle(command),
                PaymentResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
