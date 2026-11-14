package com.dymbia.sumaqagro.platform.profiles.interfaces.acl;

import java.util.Optional;

/**
 * ACL facade that exposes Profiles bounded context capabilities to other contexts.
 * Other bounded contexts must depend only on this interface, never on internal
 * Profiles models or repositories.
 *
 * @since 1.0
 */
public interface ProfilesContextFacade {

    /**
     * Creates the initial profile for a newly registered user.
     *
     * @param userId    the identifier assigned by IAM
     * @param firstName the user's given names
     * @param lastName  the user's family names
     */
    void createProfile(Long userId, String firstName, String lastName);

    /** Returns the full name stored in the user profile, when present. */
    Optional<String> fetchFullNameByUserId(Long userId);

    /**
     * Returns the user id of the agronomist currently assigned to the given plot.
     *
     * @param plotId identity of the plot
     * @return Optional containing the agronomist user id if an active assignment exists
     */
    Optional<Long> fetchAgronomistUserIdByPlotId(Long plotId);

    /**
     * Checks whether a member (identified by DNI) belongs to the given cooperative.
     *
     * @param cooperativeId identity of the cooperative
     * @param dni           DNI of the member
     * @return true if the member belongs to the cooperative
     */
    boolean isMemberOfCooperative(Long cooperativeId, String dni);

    /**
     * Returns the profile phone number of a user (useful for notifications).
     *
     * @param userId identity of the user
     * @return Optional containing the phone number if the profile exists and has one
     */
    Optional<String> fetchPhoneNumberByUserId(Long userId);
}