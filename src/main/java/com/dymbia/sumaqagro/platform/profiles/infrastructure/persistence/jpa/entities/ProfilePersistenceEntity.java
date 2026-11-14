package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

/**
 * JPA persistence entity for profiles.
 */
@Entity
@Table(name = "profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id", name = "uk_profiles_user_id")
})
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name", nullable = false, length = 100)),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name", nullable = false, length = 100))
    })
    private PersonNamePersistenceEmbeddable name;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 2)
    private PreferredLanguage preferredLanguage;

    public ProfilePersistenceEntity() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PersonNamePersistenceEmbeddable getName() {
        return name;
    }

    public void setName(PersonNamePersistenceEmbeddable name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public PreferredLanguage getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(PreferredLanguage preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}