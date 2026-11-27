package com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl;

import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

/** Crop's access to the public Profiles collaboration grants. */
@Service("cropExternalProfilesService")
public class ExternalProfilesService {
    private final ProfilesContextFacade profiles;
    public ExternalProfilesService(ProfilesContextFacade profiles) { this.profiles = profiles; }
    public boolean canViewProducerPlot(Long viewerId, Long producerId, Long plotId) {
        return profiles.canViewProducerPlot(viewerId, producerId, plotId);
    }
}
