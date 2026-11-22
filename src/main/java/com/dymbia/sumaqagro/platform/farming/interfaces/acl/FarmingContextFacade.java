package com.dymbia.sumaqagro.platform.farming.interfaces.acl;

import java.util.List;

/**
 * Public entry point of the Farming context for other bounded contexts (Profiles, Crop, Certification).
 * <p>
 * It only exposes simple types: other contexts never see the field plot aggregate.
 * </p>
 *
 * @since 1.0
 */
public interface FarmingContextFacade {

    /**
     * @return the owner user id of the plot, or {@code 0L} when the plot does not exist
     */
    Long fetchOwnerUserIdByFieldPlotId(Long fieldPlotId);

    boolean existsFieldPlotById(Long fieldPlotId);

    /**
     * @return the AgroMonitoring polygon id of the plot, or an empty string when the plot does not exist
     * or has no polygon linked yet
     * @since 1.1
     */
    String fetchAgroMonitoringPolygonIdByFieldPlotId(Long fieldPlotId);

    /**
     * @return the name of the plot, or an empty string when the plot does not exist
     * @since 1.1
     */
    String fetchFieldPlotNameById(Long fieldPlotId);

    /**
     * @return the region of the plot, or an empty string when the plot does not exist
     * @since 1.1
     */
    String fetchFieldPlotRegionById(Long fieldPlotId);

    /**
     * @return the producers that own at least one plot in the region, without repeats, or an empty list
     * @since 1.1
     */
    List<Long> fetchOwnerUserIdsByRegion(String region);
}
