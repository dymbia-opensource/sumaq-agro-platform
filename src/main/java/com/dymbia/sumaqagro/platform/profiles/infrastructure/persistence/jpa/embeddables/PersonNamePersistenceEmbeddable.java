package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Persistence representation for a person name.
 */
@Embeddable
public class PersonNamePersistenceEmbeddable {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    public PersonNamePersistenceEmbeddable() {
    }

    public PersonNamePersistenceEmbeddable(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}