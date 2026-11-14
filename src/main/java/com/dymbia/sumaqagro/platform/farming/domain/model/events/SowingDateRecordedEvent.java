package com.dymbia.sumaqagro.platform.farming.domain.model.events;

import java.time.LocalDate;

/**
 * Raised when the sowing date of a crop campaign is recorded.
 * <p>
 * Policy P4: Crop Health listens to it to start the satellite monitoring of the plot.
 * </p>
 *
 * @param fieldPlotId the plot that was sown
 * @param sowingDate  the sowing date
 * @since 1.0
 */
public record SowingDateRecordedEvent(Long fieldPlotId, LocalDate sowingDate) {
}
