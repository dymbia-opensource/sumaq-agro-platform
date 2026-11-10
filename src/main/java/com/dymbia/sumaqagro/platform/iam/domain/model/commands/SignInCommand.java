package com.dymbia.sumaqagro.platform.iam.domain.model.commands;

import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;

public record SignInCommand(EmailAddress email, String password) {

    @Override
    public String toString() {
        return "SignInCommand";
    }
}
