package com.dymbia.sumaqagro.platform.certification.application.acl;

import java.util.List;
import java.util.Optional;

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

    /**
     * Checks that the user manages the cooperative.
     *
     * @param userId        the user that makes the request
     * @param cooperativeId the cooperative
     * @return {@code true} when the user is the director of that cooperative
     */
    boolean managesCooperative(Long userId, Long cooperativeId);

    /**
     * Finds the user behind an active member of the cooperative.
     *
     * @param cooperativeId the cooperative
     * @param memberId      the member
     * @return the user id, or empty when the member is not an active member of that cooperative
     */
    Optional<Long> fetchUserIdOfActiveMember(Long cooperativeId, Long memberId);

    /**
     * Finds the active memberships of a user in a cooperative.
     *
     * @param userId        the user
     * @param cooperativeId the cooperative
     * @return the member ids, or an empty list
     */
    List<Long> fetchActiveMemberIds(Long userId, Long cooperativeId);
}
