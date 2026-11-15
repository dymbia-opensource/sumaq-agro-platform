package com.dymbia.sumaqagro.platform.profiles.domain.model.events;

/**
 * Domain event raised when a member is added to a cooperative.
 *
 * @param cooperativeId identity of the cooperative
 * @param dni           DNI of the new member
 * @since 1.0
 */
public record CooperativeMemberAddedEvent(Long cooperativeId, String dni) {
}