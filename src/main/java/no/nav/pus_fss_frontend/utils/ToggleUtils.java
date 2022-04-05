package no.nav.pus_fss_frontend.utils;

import no.nav.pus_fss_frontend.service.UnleashService;

import java.util.List;

import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;

public class ToggleUtils {

    private static final String PTO_USE_AZURE_AD_TOGGLE = "pto.use-azure-ad";

    private static final String MODIA_USE_AZURE_AD_TOGGLE = "modia.use-azure-ad";

    private static final List<String> MODIA_APPS = List.of("modiapersonoversikt", "modiaflatefs");

    public static boolean skalBrukeAzureAd(UnleashService unleashService) {
        boolean erModiaApp = MODIA_APPS.contains(requireApplicationName());
        String toggleName = erModiaApp ? MODIA_USE_AZURE_AD_TOGGLE : PTO_USE_AZURE_AD_TOGGLE;

        return unleashService.isEnabled(toggleName);
    }

}
