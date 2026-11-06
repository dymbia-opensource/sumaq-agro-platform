package com.dymbia.sumaqagro.platform.profiles.domain.model.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Dni;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Cooperative member entity (part of the Cooperative aggregate).
 * Represents a socio in the padrón of the cooperative.
 *
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "cooperative_members", uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"cooperative_id", "dni"},
                name = CooperativeMember.COOPERATIVE_DNI_UNIQUE_CONSTRAINT
        )
})
public class CooperativeMember extends AuditableAbstractAggregateRoot<CooperativeMember> {

    public static final String COOPERATIVE_DNI_UNIQUE_CONSTRAINT = "uk_cooperative_members_cooperative_id_dni";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "dni", nullable = false, length = 8))
    private Dni dni;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name", nullable = false, length = 100)),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name", nullable = false, length = 100))
    })
    private PersonName name;

    @Column(nullable = false, length = 100)
    private String community;

    @Column(name = "joined_at", nullable = false)
    private LocalDate joinedAt;

    /** Required by JPA */
    protected CooperativeMember() {}

    public CooperativeMember(Cooperative cooperative,
                             Dni dni,
                             PersonName name,
                             String community,
                             LocalDate joinedAt) {
        this.cooperative = cooperative;
        this.dni = dni;
        this.name = name;
        this.community = community.trim();
        this.joinedAt = joinedAt != null ? joinedAt : LocalDate.now();
    }
}