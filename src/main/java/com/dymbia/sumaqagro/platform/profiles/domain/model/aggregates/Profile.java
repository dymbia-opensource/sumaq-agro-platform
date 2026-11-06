package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.ProfileUpdatedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PhoneNumber;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Profile aggregate root.
 * Stores contact data and preferences of a user.
 * There is one profile per user.
 *
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id", name = Profile.USER_ID_UNIQUE_CONSTRAINT)
})
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    public static final String USER_ID_UNIQUE_CONSTRAINT = "uk_profiles_user_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name", nullable = false, length = 100)),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name", nullable = false, length = 100))
    })
    private PersonName name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number", length = 20))
    private PhoneNumber phoneNumber;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 2)
    private PreferredLanguage preferredLanguage;

    /** Required by JPA */
    protected Profile() {}

    /**
     * Creates a new profile for a user.
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
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;
        this.preferredLanguage = preferredLanguage != null ? preferredLanguage : PreferredLanguage.ES;
    }

    /**
     * Updates the profile data from the given command and registers a domain event.
     *
     * @param command update data
     */
    public void update(UpdateProfileCommand command) {
        this.name = new PersonName(command.firstName(), command.lastName());
        this.phoneNumber = command.phoneNumber() != null && !command.phoneNumber().isBlank()
                ? new PhoneNumber(command.phoneNumber())
                : null;
        this.photoUrl = command.photoUrl();
        this.preferredLanguage = command.preferredLanguage() != null
                ? command.preferredLanguage()
                : this.preferredLanguage;

        registerEvent(new ProfileUpdatedEvent(this.id, this.userId));
    }
}