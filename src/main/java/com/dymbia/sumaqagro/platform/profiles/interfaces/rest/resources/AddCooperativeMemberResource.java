package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;
import java.time.LocalDate;

/**
 * Request resource to add a member to a cooperative.
 *
 * @since 1.0
 */
public record AddCooperativeMemberResource(
        String dni,
        String firstName,
        String lastName,
        String community,
        LocalDate joinedAt
){
}
