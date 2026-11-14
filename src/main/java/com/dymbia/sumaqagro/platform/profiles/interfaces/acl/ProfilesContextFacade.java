package com.dymbia.sumaqagro.platform.profiles.interfaces.acl;

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
}
