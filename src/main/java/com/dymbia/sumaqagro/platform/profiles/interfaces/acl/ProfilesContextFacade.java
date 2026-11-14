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

    Optional<Long> fetchAgronomistUserIdByPlotId(Long plotId);

    boolean isMemberOfCooperative(Long cooperativeId, String dni);

    Optional<String> fetchPhoneNumberByUserId(Long userId);
}
