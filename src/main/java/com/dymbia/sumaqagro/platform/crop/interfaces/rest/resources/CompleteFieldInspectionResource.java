package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

/**
 * API request for completing a field inspection.
 */
public record CompleteFieldInspectionResource(String findings) {

    public CompleteFieldInspectionResource {
        if (findings == null || findings.trim().length() < 10) {
            throw new IllegalArgumentException(
                    "Inspection findings must contain at least 10 characters"
            );
        }

        findings = findings.trim();
    }
}