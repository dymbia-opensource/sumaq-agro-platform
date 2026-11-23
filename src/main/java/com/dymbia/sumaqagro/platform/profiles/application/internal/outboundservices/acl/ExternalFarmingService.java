package com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl;

import com.dymbia.sumaqagro.platform.farming.interfaces.acl.FarmingContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Outbound ACL: what Profiles needs from Field Management (Informe 4.7.1.3).
 *
 * @since 1.0
 */
@Service
public class ExternalFarmingService {

    private final FarmingContextFacade farmingContextFacade;

    public ExternalFarmingService(FarmingContextFacade farmingContextFacade) {
        this.farmingContextFacade = farmingContextFacade;
    }

    /**
     * @return the owner (producer) of the plot, or empty when the plot does not exist
     */
    public Optional<Long> fetchProducerUserIdByPlotId(Long plotId) {
        if (plotId == null || plotId <= 0) return Optional.empty();
        var ownerUserId = farmingContextFacade.fetchOwnerUserIdByFieldPlotId(plotId);
        return ownerUserId == null || ownerUserId == 0L ? Optional.empty() : Optional.of(ownerUserId);
    }
}
