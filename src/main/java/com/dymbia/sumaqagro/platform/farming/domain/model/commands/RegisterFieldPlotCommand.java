package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import java.math.BigDecimal;

//Command to register a new field plot
public record RegisterFieldPlotCommand(Long ownerUserId, String name, String region, BigDecimal declaredAreaHectares) {
    public RegisterFieldPlotCommand{
        if(ownerUserId==null||ownerUserId<=0)
            throw new IllegalArgumentException("field.plot.error.ownerUserId.invalid");
        if (name==null||name.isBlank())
            throw new IllegalArgumentException("field.plot.error.name.notBlank");
        if (region==null||region.isBlank())
            throw new IllegalArgumentException("field.plot.error.region.notBlank");
        if (declaredAreaHectares==null||declaredAreaHectares.signum() < 0)
            throw new IllegalArgumentException("field.plot.error.area.invalid");
    }
}
