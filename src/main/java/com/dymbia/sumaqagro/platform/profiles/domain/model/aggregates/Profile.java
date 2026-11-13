package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.ProfileUpdatedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PhoneNumber;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Objects;

/**
 * Profile aggregate root.
 *
 * <p>Extends {@link AbstractDomainAggregateRoot} to gain domain event registration
 * support. No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code ProfilePersistenceEntity}.</p>
 *
 * <p>There is exactly one profile per IAM user.</p>
 */
public class Profile extends AbstractDomainAggregateRoot<Profile> {

    @Getter
    private Long id;

    @Getter
    private Long userId;

    @Getter
    private PersonName name;

    private PhoneNumber phoneNumber;

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

    /**
     * Updates the profile data from the given command and registers a domain event.
     *
     * @param command the update data
     */
    public void update(UpdateProfileCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        this.name = new PersonName(command.firstName(), command.lastName());
        this.phoneNumber = command.phoneNumber() != null && !command.phoneNumber().isBlank()
                ? new PhoneNumber(command.phoneNumber())
                : null;
        this.photoUrl = command.photoUrl();
        this.preferredLanguage = command.preferredLanguage() != null
                ? command.preferredLanguage()
                : this.preferredLanguage;

        registerDomainEvent(new ProfileUpdatedEvent(this.id, this.userId));
    }

    /**
     * Returns the phone number value object (could be null).
     *
     * @return the phone number, or {@code null} if not set
     */
    public PhoneNumber getPhoneNumberValue() {
        return phoneNumber;
    }

    /**
     * Returns the raw phone number string (could be null).
     *
     * @return the phone number value, or {@code null} if not set
     */
    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber.value() : null;
    }

    /**
     * Returns the photo URL (could be null).
     *
     * @return the photo URL
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Full name convenience getter.
     *
     * @return full name
     */
    public String getFullName() {
        return name.firstName() + " " + name.lastName();
    }

    /**
     * Signals that this profile has just been updated and persisted.
     *
     * <p>Called by the repository adapter after the aggregate has been saved.
     * Registers a {@link ProfileUpdatedEvent} so the infrastructure can publish it
     * to interested subscribers in other bounded contexts.</p>
     */
    public void onUpdated() {
        registerDomainEvent(new ProfileUpdatedEvent(this.id, this.userId));
    }
}