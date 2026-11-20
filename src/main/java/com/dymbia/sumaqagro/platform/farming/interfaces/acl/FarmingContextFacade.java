package com.dymbia.sumaqagro.platform.farming.interfaces.acl;

/**
 * Public entry point of the Field Management context for other bounded contexts (e.g. Profiles).
 *
 * @since 1.0
 */
public interface FieldManagementContextFacade {

    /**
     * @return the owner user id of the plot, or {@code 0L} when the plot does not exist
     */
    Long fetchOwnerUserIdByFieldPlotId(Long fieldPlotId);

    boolean existsFieldPlotById(Long fieldPlotId);
}
