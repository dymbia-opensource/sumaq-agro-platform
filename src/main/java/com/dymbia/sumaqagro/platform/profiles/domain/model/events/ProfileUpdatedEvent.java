package com.dymbia.sumaqagro.platform.profiles.domain.model.events;

/**
 * Domain event raised when a profile is updated.
 *
 * @param profileId identity of the updated profile
 * @param userId    identity of the user who owns the profile
 * @since 1.0
 */
public record ProfileUpdatedEvent(Long profileId, Long userId) {
}