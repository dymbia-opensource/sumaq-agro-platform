package com.dymbia.sumaqagro.platform.iam.domain.model.commands;

import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;

public record SignUpCommand(
        EmailAddress email,
        String password,
        Roles requestedRole,
        String firstName,
        String lastName
) {
    @Override
    public String toString() {
        return "SignUpCommand";
    }
}
