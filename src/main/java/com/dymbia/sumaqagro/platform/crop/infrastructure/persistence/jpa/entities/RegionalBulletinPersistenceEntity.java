package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for regional bulletins.
 */
@Entity
@Table(name = "regional_bulletins")
@Getter
@Setter
@NoArgsConstructor
public class RegionalBulletinPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long agronomistId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String region;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant publishedAt;
}
