package com.dymbia.sumaqagro.platform.shared.interfaces.rest.resources;

/**
 * Resource for the simple REST responses that only confirm something, such as a deletion.
 *
 * @param message the text of the confirmation
 * @since 1.0
 */
public record MessageResource(String message) {
}
