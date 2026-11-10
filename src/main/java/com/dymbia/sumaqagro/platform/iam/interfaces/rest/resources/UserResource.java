package com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Represents the account returned after registration.
 *
 * @param id the generated user identifier
 * @param email the account email
 * @param roles the assigned role names
 */
@Schema(name = "UserResponse", description = "Registered IAM account")
public record UserResource(Long id, String email, List<String> roles) {
}