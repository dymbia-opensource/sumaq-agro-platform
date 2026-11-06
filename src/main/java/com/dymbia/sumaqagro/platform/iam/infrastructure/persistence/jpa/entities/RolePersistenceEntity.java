package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "roles")
public class RolePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 40)
    private Roles name;
}
