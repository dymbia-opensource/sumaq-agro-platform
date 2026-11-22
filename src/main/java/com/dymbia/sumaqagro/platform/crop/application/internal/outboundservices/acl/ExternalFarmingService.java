package com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl;

import com.dymbia.sumaqagro.platform.farming.interfaces.acl.FarmingContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Anti-corruption layer of Crop towards Farming.
 * <p>
 * It is the only class of Crop that knows the Farming context, and it only uses its public facade.
 * </p>
 * <p>
 * The bean has its own name because Profiles also has an {@code ExternalFarmingService}: two beans with the same
 * default name stop the application from starting.
 * </p>
 *
 * @since 1.1
 */
@Service("cropExternalFarmingService")
public class ExternalFarmingService {

    private final FarmingContextFacade farmingContextFacade;

    public ExternalFarmingService(FarmingContextFacade farmingContextFacade) {
        this.farmingContextFacade = farmingContextFacade;
    }

    /**
     * Finds the producer that owns a field plot.
     *
     * @param plotId the field plot id
     * @return the owner user id, or empty when the plot does not exist
     */
    public Optional<Long> fetchOwnerUserIdByPlotId(Long plotId) {
        var ownerUserId = farmingContextFacade.fetchOwnerUserIdByFieldPlotId(plotId);
        return ownerUserId == null || ownerUserId == 0L ? Optional.empty() : Optional.of(ownerUserId);
    }

    /**
     * Finds the AgroMonitoring polygon of a field plot.
     *
     * @param plotId the field plot id
     * @return the polygon id, or empty when the plot has no polygon linked
     */
    public Optional<String> fetchMonitoringPolygonIdByPlotId(Long plotId) {
        var polygonId = farmingContextFacade.fetchAgroMonitoringPolygonIdByFieldPlotId(plotId);
        return polygonId == null || polygonId.isBlank() ? Optional.empty() : Optional.of(polygonId);
    }
}
