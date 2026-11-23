package com.dymbia.sumaqagro.platform.certification.infrastructure.acl;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.farming.interfaces.acl.FarmingContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Reads the plot origin from the Farming context.
 * It only uses {@link FarmingContextFacade}: certification never sees the field plot aggregate.
 *
 * @since 1.0
 */
@Service
public class ExternalFarmingServiceImpl implements ExternalFarmingService {

    private final FarmingContextFacade farmingContextFacade;

    public ExternalFarmingServiceImpl(FarmingContextFacade farmingContextFacade) {
        this.farmingContextFacade = farmingContextFacade;
    }

    @Override
    public Optional<FieldOrigin> findOriginByPlotId(Long plotId) {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be greater than zero");
        }
        if (!farmingContextFacade.existsFieldPlotById(plotId)) {
            return Optional.empty();
        }
        return Optional.of(new FieldOrigin(farmingContextFacade.fetchFieldPlotNameById(plotId),
                farmingContextFacade.fetchFieldPlotRegionById(plotId), null));
    }

    @Override
    public Optional<Long> fetchOwnerUserIdByPlotId(Long plotId) {
        var ownerUserId = farmingContextFacade.fetchOwnerUserIdByFieldPlotId(plotId);
        return ownerUserId == null || ownerUserId == 0L ? Optional.empty() : Optional.of(ownerUserId);
    }
}
