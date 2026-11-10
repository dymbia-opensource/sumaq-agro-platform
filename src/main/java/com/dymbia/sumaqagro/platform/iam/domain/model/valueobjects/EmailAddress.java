package com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.Objects;

public record EmailAddress(String value) {

    public EmailAddress {
        value= Objects.requireNonNull(value,"Email is required").strip().toLowerCase(Locale.ROOT);

        if(value.isBlank()){
            throw new IllegalArgumentException("Email must not be blank");
        }
    }
}
