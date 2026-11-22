package com.dymbia.sumaqagro.platform.certification.infrastructure.acl;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Reads cooperatives and members from the profiles context.
 * It only uses {@link ProfilesContextFacade}: certification never sees the cooperative aggregate.
 *
 * @since 1.0
 */
@Service
public class ExternalProfilesServiceImpl implements ExternalProfilesService {

    private final ProfilesContextFacade profilesContextFacade;

    public ExternalProfilesServiceImpl(ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    @Override
    public boolean memberBelongsToCooperative(Long cooperativeId, Long memberId) {
        if (cooperativeId == null || cooperativeId <= 0 || memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("Cooperative id and member id must be greater than zero");
        }
        return profilesContextFacade.isActiveMemberOfCooperative(cooperativeId, memberId);
    }

    @Override
    public boolean managesCooperative(Long userId, Long cooperativeId) {
        return profilesContextFacade.managesCooperative(userId, cooperativeId);
    }

    @Override
    public Optional<Long> fetchUserIdOfActiveMember(Long cooperativeId, Long memberId) {
        var userId = profilesContextFacade.fetchUserIdOfActiveCooperativeMember(cooperativeId, memberId);
        return userId == null || userId == 0L ? Optional.empty() : Optional.of(userId);
    }

    @Override
    public List<Long> fetchActiveMemberIds(Long userId, Long cooperativeId) {
        return profilesContextFacade.fetchActiveMemberIdsByUserIdAndCooperativeId(userId, cooperativeId);
    }
}
