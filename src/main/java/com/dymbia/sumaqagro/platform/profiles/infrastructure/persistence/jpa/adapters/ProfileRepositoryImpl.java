package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.ProfileRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository adapter that bridges the domain profile repository port with Spring Data JPA.
 *
 * <p>Also acts as the event-publishing boundary: after a profile is persisted,
 * registered domain events are dispatched via {@link DomainEventPublisher}.</p>
 */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository,
                                 DomainEventPublisher domainEventPublisher) {
        this.profilePersistenceRepository = profilePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Profile> findById(Long id) {
        return profilePersistenceRepository.findById(id)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Profile> findByUserId(Long userId) {
        return profilePersistenceRepository.findByUserId(userId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByUserId(Long userId) {
        return profilePersistenceRepository.existsByUserId(userId);
    }

    /** {@inheritDoc} */
    @Override
    public Profile save(Profile profile) {
        var savedEntity = profilePersistenceRepository.save(
                ProfilePersistenceAssembler.toPersistenceFromDomain(profile));
        var savedProfile = ProfilePersistenceAssembler.toDomainFromPersistence(savedEntity);

        // Publish any domain events that were registered (e.g. from update())
        domainEventPublisher.publishAndClearEvents(savedProfile);

        return savedProfile;
    }
}