package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Request resource used to register a field plot. Field names match the web app.
 */
public record RegisterFieldPlotResource(
        Long ownerUserId,
        String name,
        String region,
        BigDecimal areaHectares,
        Long cooperativeId,
        Long cooperativeMemberId) {
}
