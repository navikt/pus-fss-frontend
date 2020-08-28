package no.nav.pus_fss_frontend.config;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.featuretoggle.UnleashServiceConfig;
import no.nav.common.health.selftest.SelfTestChecks;
import no.nav.common.health.selftest.SelfTestMeterBinder;
import no.nav.pus_fss_frontend.servlet.LoginServlet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

import static java.util.Optional.ofNullable;
import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;

@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
public class ApplicationConfig {

    @Bean
    public ServletRegistrationBean<LoginServlet> authenticationServletServletRegistrationBean(
            UnleashService unleashService, EnvironmentProperties environmentProperties
    ) {
        return new ServletRegistrationBean<>(new LoginServlet(unleashService, environmentProperties),"/*");
    }

    @Bean
    public UnleashService unleashService(EnvironmentProperties environmentProperties) {
        String unleashUrl = ofNullable(environmentProperties.getUnleashApiUrl()).orElse("https://unleash.nais.adeo.no/api/");
        return new UnleashService(UnleashServiceConfig.builder()
                .applicationName(requireApplicationName())
                .unleashApiUrl(unleashUrl)
                .build()
        );
    }

    @Bean
    public SelfTestChecks selfTestChecks() {
        // Vi har ingen avhengigheter som vi trenger å sjekke
        return new SelfTestChecks(Collections.emptyList());
    }

    @Bean
    public SelfTestMeterBinder selfTestMeterBinder(SelfTestChecks selfTestChecks) {
        return new SelfTestMeterBinder(selfTestChecks);
    }

}
