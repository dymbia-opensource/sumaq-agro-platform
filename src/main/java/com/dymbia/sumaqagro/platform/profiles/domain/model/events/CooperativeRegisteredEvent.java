package com.dymbia.sumaqagro.platform.profiles.domain.model.events;

/**
 * Domain event raised when a cooperative is registered.
 *
 * @param cooperativeId identity of the cooperative (may be null before persistence)
 * @param ruc           RUC of the cooperative
 * @param managerUserId user id of the manager
 * @since 1.0
 */
public record CooperativeRegisteredEvent(Long cooperativeId, String ruc, Long managerUserId) {
}