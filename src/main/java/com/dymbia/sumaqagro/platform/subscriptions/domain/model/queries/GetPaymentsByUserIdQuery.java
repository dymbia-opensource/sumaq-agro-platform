package com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries;

/**
 * Requests the payment history of an account.
 *
 * @param userId the account identifier resolved from the authenticated principal
 */
public record GetPaymentsByUserIdQuery(long userId) {
}