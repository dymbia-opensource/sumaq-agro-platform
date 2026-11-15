package com.dymbia.sumaqagro.platform.farming.infrastructure.agromonitoring.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Part of the AgroMonitoring response that the platform uses.
 *
 * @param id the polygon id in AgroMonitoring
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgroMonitoringPolygonResponse(String id) {
}
