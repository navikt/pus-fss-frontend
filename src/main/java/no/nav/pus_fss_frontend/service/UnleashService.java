package no.nav.pus_fss_frontend.service;

import no.finn.unleash.UnleashContext;
import no.nav.common.featuretoggle.UnleashClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnleashService {
    private final UnleashClient unleashClient;

    @Autowired
    public UnleashService(UnleashClient unleashClient) {
        this.unleashClient = unleashClient;
    }

    public boolean isEnabled(String toggleName) {
        return unleashClient.isEnabled(toggleName);
    }

    public boolean isEnabled(String toggleName, UnleashContext unleashContext) {
        return unleashClient.isEnabled(toggleName, unleashContext);
    }
}
