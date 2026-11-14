package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.commands.CupCoffeeCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CoffeeCuppingScores;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.CupCoffeeResource;

import java.util.Objects;

public final class CupCoffeeCommandFromResourceAssembler {

    private CupCoffeeCommandFromResourceAssembler() {
    }

    public static CupCoffeeCommand toCommandFromResource(Long harvestBatchId, CupCoffeeResource resource) {
        Objects.requireNonNull(resource, "Cup coffee request body is required.");
        return new CupCoffeeCommand(
                harvestBatchId,
                CoffeeCuppingScores.from(
                        resource.aroma(),
                        resource.flavor(),
                        resource.acidity(),
                        resource.body(),
                        resource.totalScore()),
                resource.fermentDefect());
    }
}
