package com.dymbia.sumaqagro.platform.iam.domain.repositories;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
}
