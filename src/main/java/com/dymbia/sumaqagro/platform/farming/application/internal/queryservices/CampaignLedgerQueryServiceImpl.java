package com.dymbia.sumaqagro.platform.farming.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.farming.application.queryservices.CampaignLedgerQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCampaignLedgerByCropCampaignIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCampaignLedgerByIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropCampaignId;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CampaignLedgerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Query service implementation of the cost ledger aggregate.
 *
 * @since 1.1
 */
@Service
public class CampaignLedgerQueryServiceImpl implements CampaignLedgerQueryService {
    private final CampaignLedgerRepository campaignLedgerRepository;

    public CampaignLedgerQueryServiceImpl(CampaignLedgerRepository campaignLedgerRepository) {
        this.campaignLedgerRepository = campaignLedgerRepository;
    }

    @Override
    public Optional<CampaignLedger> handle(GetCampaignLedgerByIdQuery query) {
        return campaignLedgerRepository.findById(query.campaignLedgerId());
    }

    @Override
    public Optional<CampaignLedger> handle(GetCampaignLedgerByCropCampaignIdQuery query) {
        return campaignLedgerRepository.findByCropCampaignId(new CropCampaignId(query.cropCampaignId()));
    }
}
