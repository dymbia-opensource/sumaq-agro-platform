package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * JPA persistence entity for IAM roles.
 */
@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
public class RolePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 40)
    private Roles name;
}