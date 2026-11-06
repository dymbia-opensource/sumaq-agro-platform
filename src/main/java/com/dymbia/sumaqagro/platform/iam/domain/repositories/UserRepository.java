package com.dymbia.sumaqagro.platform.iam.domain.repositories;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);

    User save(User user);
}
