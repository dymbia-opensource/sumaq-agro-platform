package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Request body used to record the potato calibers of a harvest batch.
 */
public record GradePotatoCalibersResource(
        BigDecimal firstPercentage,
        BigDecimal secondPercentage,
        BigDecimal thirdPercentage,
        BigDecimal pestDamagePercentage
) {
}
