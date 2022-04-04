package no.nav.pus_fss_frontend.service;

import no.finn.unleash.UnleashContext;
import no.nav.common.featuretoggle.UnleashClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnleashService {

    private static final String VEILARBVEDTAKSSTOTTE_DOK_DIST_SCHEDULE_ENABLED_TOGGLE = "veilarbvedtaksstotte.dok_dist_schedule_enabled";

    private final UnleashClient unleashClient;

    @Autowired
    public UnleashService(UnleashClient unleashClient) {
        this.unleashClient = unleashClient;
    }

    public boolean isEnabled() {
        return unleashClient.isEnabled(VEILARBVEDTAKSSTOTTE_DOK_DIST_SCHEDULE_ENABLED_TOGGLE);
    }

    public Object isEnabled(String e, UnleashContext unleashContext) {
    }
}