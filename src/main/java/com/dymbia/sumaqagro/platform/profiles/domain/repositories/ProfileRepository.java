package com.dymbia.sumaqagro.platform.profiles.domain.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;

import java.util.List;
import java.util.Optional;

/**
 * Repository port of the Profile aggregate.
 * The domain says what it needs and the infrastructure layer implements it.
 *
 * @since 1.0
 */
public interface ProfileRepository {

    /**
     * Finds a profile by its id.
     *
     * @param id the profile id
     * @return the profile, or an empty optional
     */
    Optional<Profile> findById(Long id);

    /**
     * Finds a profile by the user id.
     *
     * @param userId the user id from IAM
     * @return the profile, or an empty optional
     */
    Optional<Profile> findByUserId(Long userId);

    List<Profile> findAll();

    /**
     * Checks whether a profile already exists for the given user.
     *
     * @param userId the user id
     * @return true if a profile exists for that user
     */
    boolean existsByUserId(Long userId);

    /**
     * Saves the profile.
     *
     * @param profile the profile to save
     * @return the saved profile, with its id
     */
    Profile save(Profile profile);
}