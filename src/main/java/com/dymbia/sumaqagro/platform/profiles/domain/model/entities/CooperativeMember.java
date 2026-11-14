package com.dymbia.sumaqagro.platform.profiles.domain.model.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Dni;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Member of a cooperative (entity inside the {@code Cooperative} aggregate).
 *
 * <p>A member is identified by its DNI inside the cooperative. {@code userId} links the member
 * to an IAM account when the member has one; it may be {@code null}.</p>
 *
 * @since 1.0
 */
@Getter
public class CooperativeMember {
    private Long id;
    private Long cooperativeId;
    private Dni dni;
    private PersonName name;
    private String community;
    private LocalDate joinedAt;
    private boolean active;
    private Long userId;

    public CooperativeMember(Long id,
                             Long cooperativeId,
                             Dni dni,
                             PersonName name,
                             String community,
                             LocalDate joinedAt,
                             boolean active,
                             Long userId) {
        this.id = id;
        this.cooperativeId = cooperativeId;
        this.dni = Objects.requireNonNull(dni, "dni must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.community = Objects.requireNonNull(community, "community must not be null").trim();
        this.joinedAt = joinedAt != null ? joinedAt : LocalDate.now();
        this.active = active;
        if (userId != null && userId <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
        this.userId = userId;
    }

    public static CooperativeMember reconstitute(Long id,
                                                 Long cooperativeId,
                                                 Dni dni,
                                                 PersonName name,
                                                 String community,
                                                 LocalDate joinedAt,
                                                 boolean active,
                                                 Long userId) {
        return new CooperativeMember(id, cooperativeId, dni, name, community, joinedAt, active, userId);
    }

    public String getFullName() {
        return name.getFullName();
    }

    public void deactivate() {
        this.active = false;
    }
}
