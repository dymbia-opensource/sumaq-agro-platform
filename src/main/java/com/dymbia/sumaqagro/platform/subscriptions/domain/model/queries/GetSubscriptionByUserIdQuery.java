package com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries;


/**
 * Request the subscription associated with a user.
 * @param userId the account identifier of the user whose subscription is being requested.
 */
public record GetSubscriptionByUserIdQuery(long userId) {
}
