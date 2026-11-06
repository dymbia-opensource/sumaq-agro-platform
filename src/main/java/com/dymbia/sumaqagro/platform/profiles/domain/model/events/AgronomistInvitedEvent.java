package com.dymbia.sumaqagro.platform.profiles.domain.model.events;

/**
 * Domain event raised when an agronomist is invited to a cooperative.
 *
 * @param cooperativeId   identity of the cooperative
 * @param email           email of the invited agronomist
 * @param invitationToken token of the invitation
 * @since 1.0
 */
public record AgronomistInvitedEvent(Long cooperativeId, String email, String invitationToken) {
}