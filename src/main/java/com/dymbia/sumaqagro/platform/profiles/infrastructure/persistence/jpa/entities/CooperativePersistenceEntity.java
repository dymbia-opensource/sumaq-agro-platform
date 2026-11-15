package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA mapping of the {@code Cooperative} aggregate (table {@code cooperatives}).
 *
 * @since 1.0
 */
@Entity
@Table(name = "cooperatives", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ruc", name = "uk_cooperatives_ruc")
})
public class CooperativePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "ruc", nullable = false, length = 11)
    private String ruc;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(name = "manager_user_id", nullable = false)
    private Long managerUserId;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(name = "institutional_email", length = 150)
    private EmailAddress institutionalEmail;

    @Column(name = "legal_representative", length = 150)
    private String legalRepresentative;

    @Column(name = "headquarters_address", length = 255)
    private String headquartersAddress;

    @Column(name = "switchboard_phone", length = 20)
    private String switchboardPhone;

    @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CooperativeMemberPersistenceEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgronomistInvitationPersistenceEntity> invitations = new ArrayList<>();

    public CooperativePersistenceEntity() {
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(Long managerUserId) {
        this.managerUserId = managerUserId;
    }

    public EmailAddress getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(EmailAddress institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getHeadquartersAddress() {
        return headquartersAddress;
    }

    public void setHeadquartersAddress(String headquartersAddress) {
        this.headquartersAddress = headquartersAddress;
    }

    public String getSwitchboardPhone() {
        return switchboardPhone;
    }

    public void setSwitchboardPhone(String switchboardPhone) {
        this.switchboardPhone = switchboardPhone;
    }

    public List<CooperativeMemberPersistenceEntity> getMembers() {
        return members;
    }

    public void setMembers(List<CooperativeMemberPersistenceEntity> members) {
        this.members = members;
    }

    public List<AgronomistInvitationPersistenceEntity> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<AgronomistInvitationPersistenceEntity> invitations) {
        this.invitations = invitations;
    }
}
