package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;
//Monitoring status of a field plot
public enum PlotStatus {
    WITHOUT_POLYGON,    // registered, boundary not drawn yet
    ACTIVE_MONITORING   // polygon linked to AgroMonitoring
}
