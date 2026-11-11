package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

/**
 * Input for confirming an application, built from the URI path parameter.
 */
public record RegisterTechnicalPrescriptionApplicationResource(
        Long technicalPrescriptionId
) {
}
