package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.UserRepository;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository userPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public UserRepositoryImpl(
            UserPersistenceRepository userPersistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.userPersistenceRepository = userPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userPersistenceRepository.findById(id)
                .map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return userPersistenceRepository.findByEmail(email.value())
                .map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<User> findAll() {
        return userPersistenceRepository.findAll().stream()
                .map(UserPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public User save(User user) {
        var saved = userPersistenceRepository.save(
                UserPersistenceAssembler.toPersistenceFromDomain(user)
        );
        var savedUser = UserPersistenceAssembler.toDomainFromPersistence(saved);
        domainEventPublisher.publishAndClearEvents(user);
        return savedUser;
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return userPersistenceRepository.existsByEmail(email.value());
    }
}
