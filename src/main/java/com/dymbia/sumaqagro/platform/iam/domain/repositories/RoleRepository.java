package com.dymbia.sumaqagro.platform.iam.domain.repositories;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    /**
     * IAM role repository port.
     */
    Optional<Role> findByName(Roles name);

    boolean existsByName(Roles name);

    List<Role> findAll();

    Role save(Role role);
}
