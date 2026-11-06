package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.ProfileRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapts the profile repository contract to Spring Data JPA.
 */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository profilePersistenceRepository;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository) {
        this.profilePersistenceRepository = profilePersistenceRepository;
    }

    /** {@inheritDoc} */
    @Override
    public Profile save(Profile profile) {
        return profilePersistenceRepository.save(profile);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Profile> findById(Long id) {
        return profilePersistenceRepository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Profile> findByUserId(Long userId) {
        return profilePersistenceRepository.findByUserId(userId);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByUserId(Long userId) {
        return profilePersistenceRepository.existsByUserId(userId);
    }
}