package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * JPA persistence entity for cooperative members.
 */
@Entity
@Table(name = "cooperative_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cooperative_id", "dni"}, name = "uk_cooperative_members_cooperative_id_dni")
})
public class CooperativeMemberPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private CooperativePersistenceEntity cooperative;

    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name", nullable = false, length = 100)),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name", nullable = false, length = 100))
    })
    private PersonNamePersistenceEmbeddable name;

    @Column(nullable = false, length = 100)
    private String community;

    @Column(name = "joined_at", nullable = false)
    private LocalDate joinedAt;

    public CooperativeMemberPersistenceEntity() {
    }

    public CooperativePersistenceEntity getCooperative() {
        return cooperative;
    }

    public void setCooperative(CooperativePersistenceEntity cooperative) {
        this.cooperative = cooperative;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public PersonNamePersistenceEmbeddable getName() {
        return name;
    }

    public void setName(PersonNamePersistenceEmbeddable name) {
        this.name = name;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public LocalDate getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDate joinedAt) {
        this.joinedAt = joinedAt;
    }
}