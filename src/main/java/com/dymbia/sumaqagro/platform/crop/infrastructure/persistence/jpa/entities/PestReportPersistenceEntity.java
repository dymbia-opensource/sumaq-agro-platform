package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportSeverity;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for pest reports.
 */
@Entity
@Table(name = "pest_reports")
@Getter
@Setter
@NoArgsConstructor
public class PestReportPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PestReportSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PestReportStatus status;


}
