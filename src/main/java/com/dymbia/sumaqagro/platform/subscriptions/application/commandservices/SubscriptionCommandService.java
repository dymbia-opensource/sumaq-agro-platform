package com.dymbia.sumaqagro.platform.subscriptions.application.commandservices;

import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.PaymentCommand;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Payment;

/** Handles subscription checkout commands. */
public interface SubscriptionCommandService {
    Result<Payment, ApplicationError> handle(PaymentCommand command);
}
