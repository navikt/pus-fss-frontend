package no.nav.fssfrontend;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import no.nav.sbl.featuretoggle.unleash.UnleashServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;

import static no.nav.apiapp.ServletUtil.leggTilServlet;
import static no.nav.sbl.featuretoggle.unleash.UnleashServiceConfig.UNLEASH_API_URL_PROPERTY_NAME;
import static no.nav.sbl.util.EnvironmentUtils.getOptionalProperty;
import static no.nav.sbl.util.EnvironmentUtils.requireApplicationName;

@Configuration
@Import({ FeatureResource.class, ApplicationServlet.class })
public class ApplicationConfig implements ApiApplication {

    public static final String OPENAM_DISCOVERY_URL_PROPERTY = "OPENAM_DISCOVERY_URL";
    public static final String OPENAM_CLIENT_ID_PROPERTY = "VEILARBLOGIN_OPENAM_CLIENT_ID";
    public static final String OPENAM_LOGIN_URL_PROPERTY = "VEILARBLOGIN_OPENAM_LOGIN_URL";

    @Override
    public void startup(ServletContext servletContext) {
        leggTilServlet(servletContext, new ApplicationServlet(), "/*");
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {}

    @Bean
    public UnleashService unleashService() {
        return new UnleashService(UnleashServiceConfig.builder()
                .applicationName(requireApplicationName())
                .unleashApiUrl(getOptionalProperty(UNLEASH_API_URL_PROPERTY_NAME).orElse("https://unleash.nais.adeo.no/api/"))
                .build()
        );
    }

}
