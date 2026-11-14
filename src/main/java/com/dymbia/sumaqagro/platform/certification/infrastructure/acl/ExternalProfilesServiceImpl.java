package com.dymbia.sumaqagro.platform.certification.infrastructure.acl;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalProfilesService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Asks the profiles context whether a member belongs to a cooperative.
 * Certification does not use the cooperative aggregate; it only reads the membership.
 *
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class ExternalProfilesServiceImpl implements ExternalProfilesService {

    private final EntityManager entityManager;

    public ExternalProfilesServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean memberBelongsToCooperative(Long cooperativeId, Long memberId) {
        if (cooperativeId == null || cooperativeId <= 0 || memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("Cooperative id and member id must be greater than zero");
        }
        Long matches = entityManager.createQuery(
                        "select count(member) from CooperativeMember member "
                                + "where member.id = :memberId and member.cooperative.id = :cooperativeId",
                        Long.class)
                .setParameter("memberId", memberId)
                .setParameter("cooperativeId", cooperativeId)
                .getSingleResult();
        return matches > 0;
    }
}
