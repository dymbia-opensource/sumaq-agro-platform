package com.dymbia.sumaqagro.platform.certification.application.acl;

/**
 * Outbound port to the profiles context.
 * Certification uses it to check that a member belongs to the receiving cooperative.
 *
 * @since 1.0
 */
public interface ExternalProfilesService {

    /**
     * Checks that the member is registered in the cooperative padrón.
     *
     * @param cooperativeId cooperative that receives the harvest batch
     * @param memberId      delivering member
     * @return {@code true} when the member belongs to that cooperative
     */
    boolean memberBelongsToCooperative(Long cooperativeId, Long memberId);
}
