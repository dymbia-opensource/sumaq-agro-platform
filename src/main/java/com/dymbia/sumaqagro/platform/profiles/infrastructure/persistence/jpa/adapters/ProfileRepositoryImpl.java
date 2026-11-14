package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.ProfileRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link ProfileRepository} with Spring Data JPA.
 *
 * @since 1.0
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

    @Override
    public Optional<Profile> findById(Long id) {
        return profilePersistenceRepository.findById(id)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByUserId(Long userId) {
        return profilePersistenceRepository.findByUserId(userId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Profile> findAll() {
        return profilePersistenceRepository.findAll().stream()
                .map(ProfilePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return profilePersistenceRepository.existsByUserId(userId);
    }

    @Override
    public Profile save(Profile profile) {
        var savedEntity = profilePersistenceRepository.save(
                ProfilePersistenceAssembler.toPersistenceFromDomain(profile));
        var savedProfile = ProfilePersistenceAssembler.toDomainFromPersistence(savedEntity);
        // The events were registered on the object we received (e.g. by update()), not on the copy read back.
        domainEventPublisher.publishAndClearEvents(profile);
        return savedProfile;
    }
}
