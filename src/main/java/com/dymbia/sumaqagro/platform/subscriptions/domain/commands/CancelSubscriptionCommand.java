package com.dymbia.sumaqagro.platform.subscriptions.domain.commands;


/**
 * Request cancellation of renewal for the authenticated account.
 *
 * @param userId the account identifier resolved from the authenticated principal
 */
public record CancelSubscriptionCommand(long userId) {
}
