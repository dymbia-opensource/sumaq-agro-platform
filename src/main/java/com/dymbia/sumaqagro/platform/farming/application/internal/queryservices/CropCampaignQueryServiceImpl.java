package com.dymbia.sumaqagro.platform.farming.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.farming.application.queryservices.CropCampaignQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetAllCropCampaignsByFieldPlotIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCropCampaignByIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CropCampaignRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the crop campaign query service.
 */
@Service
public class CropCampaignQueryServiceImpl implements CropCampaignQueryService {
    private final CropCampaignRepository cropCampaignRepository;

    public CropCampaignQueryServiceImpl(CropCampaignRepository cropCampaignRepository) {
        this.cropCampaignRepository = cropCampaignRepository;
    }

    @Override
    public Optional<CropCampaign> handle(GetCropCampaignByIdQuery query) {
        return cropCampaignRepository.findById(query.cropCampaignId());
    }

    @Override
    public List<CropCampaign> handle(GetAllCropCampaignsByFieldPlotIdQuery query) {
        return cropCampaignRepository.findAllByFieldPlotId(new FieldPlotId(query.fieldPlotId()));
    }
}
