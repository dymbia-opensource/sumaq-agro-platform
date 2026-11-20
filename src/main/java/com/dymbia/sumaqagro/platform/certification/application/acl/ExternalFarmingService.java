package com.dymbia.sumaqagro.platform.certification.application.acl;

import java.util.Optional;

/**
 * Outbound port to field management.
 * Certification uses it to show the plot origin and the planted variety on the public traceability view.
 *
 * @since 1.0
 */
public interface ExternalFieldManagementService {

    /**
     * Reads the public origin of a plot.
     *
     * @param plotId source plot of the harvest batch
     * @return the plot name, region and planted variety, or empty when the plot does not exist
     */
    Optional<FieldOrigin> findOriginByPlotId(Long plotId);

    /**
     * Public origin of a plot. The planted variety is absent when field management has no campaign for the plot.
     *
     * @param plotName    plot name
     * @param region      plot region
     * @param seedVariety planted variety, may be null
     */
    record FieldOrigin(String plotName, String region, String seedVariety) {
    }
}
