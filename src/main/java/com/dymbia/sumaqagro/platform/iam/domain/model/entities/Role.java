package com.dymbia.sumaqagro.platform.iam.domain.model.entities;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
/**
 * Represents an IAM role independently of its persistence mechanism.
 */
@Getter
@EqualsAndHashCode(of = "name")
@ToString
public final class Role {
    private final Long id;
    private final Roles name;

    /**
     * Creates a role that has not been persisted yet.
     */
    public Role(Roles name){
        this(null,name);
    }

    /**
     * Creates or restores a role from its identity and name.
     */
    public Role(Long id, Roles name) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Role ID must be positive");
        }

        this.id = id;
        this.name = Objects.requireNonNull(name, "Role name is required");
    }

    public String getStringName() {
        return name.name();
    }

}
