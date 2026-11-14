package com.dymbia.sumaqagro.platform.iam.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.iam.application.commandservices.RoleCommandService;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SeedRolesCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void handle(SeedRolesCommand command) {
        for (Roles name : Roles.values()) {
            if (!roleRepository.existsByName(name)) {
                roleRepository.save(new Role(name));
            }
        }
    }
}