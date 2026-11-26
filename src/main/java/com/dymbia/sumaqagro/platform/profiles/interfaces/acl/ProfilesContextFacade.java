package com.dymbia.sumaqagro.platform.profiles.interfaces.acl;

import java.util.List;
import java.util.Optional;

/**
 * Public entry point of the Profiles context for other bounded contexts.
 *
 * <ul>
 *     <li>IAM creates the profile when a user signs up.</li>
 *     <li>Crop Health finds the agronomist of a plot and his phone number.</li>
 *     <li>Harvest Certification checks if a person is a member of a cooperative.</li>
 * </ul>
 *
 * @since 1.0
 */
public interface ProfilesContextFacade {

    /**
     * @return the profile id, or {@code 0L} when it could not be created
     */
    Long createProfile(Long userId, String firstName, String lastName);

    /**
     * Accepts the pending cooperative invitations sent to the email of a new agronomist account,
     * so the agronomist joins their technical teams (US-19).
     *
     * @param agronomistUserId IAM user id of the agronomist
     * @param email            email of the agronomist account
     * @return how many cooperatives the agronomist joined
     */
    int acceptAgronomistInvitations(Long agronomistUserId, String email);

    /** Returns the full name stored in the user profile, when present. */
    Optional<String> fetchFullNameByUserId(Long userId);

    /**
     * Returns the user id of the agronomist currently assigned to the given plot.
     *
     * @param plotId identity of the plot
     * @return Optional containing the agronomist user id if an active assignment exists
     */
    Optional<Long> fetchAgronomistUserIdByPlotId(Long plotId);

    boolean isMemberOfCooperative(Long cooperativeId, String dni);

    /** Checks that the director manages the cooperative and the member is on its roster. */
    boolean managesCooperativeMember(long directorId, long cooperativeId, long memberId);

    /**
     * Returns the profile phone number of a user (useful for notifications).
     *
     * @param userId identity of the user
     * @return Optional containing the phone number if the profile exists and has one
     */
    Optional<String> fetchPhoneNumberByUserId(Long userId);

    /**
     * @return true when the user is the manager of the cooperative
     * @since 1.1
     */
    boolean managesCooperative(Long userId, Long cooperativeId);

    /**
     * @return true when the member is an active member of the cooperative, with or without a user account
     * @since 1.1
     */
    boolean isActiveMemberOfCooperative(Long cooperativeId, Long memberId);

    /**
     * @return the user id of an active member of the cooperative, or {@code 0L} when the member does not exist,
     * belongs to another cooperative or is inactive
     * @since 1.1
     */
    Long fetchUserIdOfActiveCooperativeMember(Long cooperativeId, Long memberId);

    /**
     * @return the ids of the active memberships of the user in the cooperative, or an empty list
     * @since 1.1
     */
    List<Long> fetchActiveMemberIdsByUserIdAndCooperativeId(Long userId, Long cooperativeId);
    /** Checks an active advisor assignment or the director's active producer membership. */
    boolean canViewProducerPlot(Long viewerUserId, Long producerUserId, Long plotId);
}
