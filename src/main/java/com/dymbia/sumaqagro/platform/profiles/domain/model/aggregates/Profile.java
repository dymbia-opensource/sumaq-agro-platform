package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.ProfileUpdatedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PhoneNumber;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Objects;

/**
 * Profile aggregate root: contact data of one IAM user.
 *
 * <p>No JPA annotation lives here; the table is mapped by {@code ProfilePersistenceEntity}.</p>
 *
 * @since 1.0
 */

public class Profile extends AbstractDomainAggregateRoot<Profile> {

    @Getter
    private Long id;

    @Getter
    private Long userId;

    @Getter
    private PersonName name;

    private PhoneNumber phoneNumber;

    @Getter
    private String photoUrl;

    @Getter
    private PreferredLanguage preferredLanguage;

    /**
     * Creates a profile from the provided domain values.
     *
     * @param id                the unique identifier (null when creating a new profile)
     * @param userId            identity of the user (from IAM)
     * @param name              person name
     * @param phoneNumber       contact phone (optional)
     * @param photoUrl          profile photo URL (optional)
     * @param preferredLanguage preferred UI language
     */
    public Profile(Long id,
                   Long userId,
                   PersonName name,
                   PhoneNumber phoneNumber,
                   String photoUrl,
                   PreferredLanguage preferredLanguage) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;
        this.preferredLanguage = preferredLanguage != null ? preferredLanguage : PreferredLanguage.ES;
    }

    /**
     * Reconstitutes an existing profile from persistence state.
     *
     * @param id                the unique identifier
     * @param userId            identity of the user (from IAM)
     * @param name              person name
     * @param phoneNumber       contact phone (optional)
     * @param photoUrl          profile photo URL (optional)
     * @param preferredLanguage preferred UI language
     * @return the reconstituted Profile instance
     */
    public static Profile reconstitute(Long id,
                                       Long userId,
                                       PersonName name,
                                       PhoneNumber phoneNumber,
                                       String photoUrl,
                                       PreferredLanguage preferredLanguage) {
        return new Profile(id, userId, name, phoneNumber, photoUrl, preferredLanguage);
    }

    /**
     * Creates a new profile for a user (no identity assigned yet).
     *
     * @param userId            identity of the user (from IAM)
     * @param name              person name
     * @param phoneNumber       contact phone (optional)
     * @param photoUrl          profile photo URL (optional)
     * @param preferredLanguage preferred UI language
     */
    public Profile(Long userId,
                   PersonName name,
                   PhoneNumber phoneNumber,
                   String photoUrl,
                   PreferredLanguage preferredLanguage) {
        this(null, userId, name, phoneNumber, photoUrl, preferredLanguage);
    }

    public Profile(CreateProfileCommand command) {
        this(command.userId(),
                new PersonName(command.firstName(), command.lastName()),
                null,
                null,
                PreferredLanguage.ES);
    }

    /**
     * Updates the contact data. Values that are not sent (photo, language) keep their current value.
     */
    public void update(UpdateProfileCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        this.name = new PersonName(command.firstName(), command.lastName());
        this.phoneNumber = command.phoneNumber() != null && !command.phoneNumber().isBlank()
                ? new PhoneNumber(command.phoneNumber())
                : null;
        if (command.photoUrl() != null) {
            this.photoUrl = command.photoUrl().isBlank() ? null : command.photoUrl().trim();
        }
        if (command.preferredLanguage() != null) {
            this.preferredLanguage = command.preferredLanguage();
        }
        registerDomainEvent(new ProfileUpdatedEvent(this.id, this.userId));
    }

    public String getFullName() {
        return name.getFullName();
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.value() : null;
    }

    public PhoneNumber getPhoneNumberValue() {
        return phoneNumber;
    }
}