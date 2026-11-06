package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import java.math.BigDecimal;

//Command to register a new field plot
public record RegisterFieldPlotCommand(Long ownerUserId, String name, String region, BigDecimal declaredAreaHectares) {
    public RegisterFieldPlotCommand{
        if(ownerUserId==null||ownerUserId<=0)
            throw new IllegalArgumentException("ownerUserId must be greater than 0.");
        if (name==null||name.isBlank())
            throw new IllegalArgumentException("Plot name must not be blank.");
        if (region==null||region.isBlank())
            throw new IllegalArgumentException("Region must not be blank.");
        if (declaredAreaHectares==null||declaredAreaHectares.signum() < 0)
            throw new IllegalArgumentException("Area must be zero or positive.");
    }
}
