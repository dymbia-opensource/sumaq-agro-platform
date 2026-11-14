package com.dymbia.sumaqagro.platform.profiles.domain.model.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Dni;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Cooperative member entity (part of the Cooperative aggregate).
 * Represents a socio in the padrón of the cooperative.
 */
@Getter
public class CooperativeMember {

    private Long id;
    private Long cooperativeId;
    private Dni dni;
    private PersonName name;
    private String community;
    private LocalDate joinedAt;

    public CooperativeMember(Long id,
                             Long cooperativeId,
                             Dni dni,
                             PersonName name,
                             String community,
                             LocalDate joinedAt) {
        this.id = id;
        this.cooperativeId = cooperativeId;
        this.dni = Objects.requireNonNull(dni, "dni must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.community = Objects.requireNonNull(community, "community must not be null").trim();
        this.joinedAt = joinedAt != null ? joinedAt : LocalDate.now();
    }

    /**
     * Reconstitutes an existing member from persistence state.
     */
    public static CooperativeMember reconstitute(Long id,
                                                 Long cooperativeId,
                                                 Dni dni,
                                                 PersonName name,
                                                 String community,
                                                 LocalDate joinedAt) {
        return new CooperativeMember(id, cooperativeId, dni, name, community, joinedAt);
    }
}