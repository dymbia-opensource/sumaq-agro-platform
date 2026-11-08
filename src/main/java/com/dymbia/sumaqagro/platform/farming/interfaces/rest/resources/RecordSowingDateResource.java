package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Request body to record the sowing date of a campaign.
 *
 * @param sowingDate the sowing date, in {@code YYYY-MM-DD} format
 */
public record RecordSowingDateResource(LocalDate sowingDate) {
}
