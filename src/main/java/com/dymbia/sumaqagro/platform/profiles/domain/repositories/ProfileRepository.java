package com.dymbia.sumaqagro.platform.profiles.domain.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import java.util.Optional;

/**
 * Domain repository for the Profile aggregate.
 *
 * @since 1.0
 */
public interface ProfileRepository {

    Profile save(Profile profile);

    Optional<Profile> findById(Long id);

    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}