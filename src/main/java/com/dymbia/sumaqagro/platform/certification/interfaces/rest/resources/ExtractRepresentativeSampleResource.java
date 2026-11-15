package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Request body used to attach the representative sample of a harvest batch.
 */
public record ExtractRepresentativeSampleResource(String sampleIdentifier, BigDecimal sampleWeightKg) {
}
