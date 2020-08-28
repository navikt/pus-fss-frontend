package no.nav.pus_fss_frontend.config;

import no.nav.common.health.selftest.SelfTestChecks;
import no.nav.common.health.selftest.SelfTestMeterBinder;
import no.nav.pus_fss_frontend.controller.InternalController;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
@Import({ InternalController.class })
public class TestApplicationConfig {

//    @Bean
//    public ServletRegistrationBean<LoginServlet> authenticationServletServletRegistrationBean(
//            UnleashService unleashService, EnvironmentProperties environmentProperties
//    ) {
//        return new ServletRegistrationBean<>(new LoginServlet(unleashService, environmentProperties),"/*");
//    }
//
//    @Bean
//    public UnleashService unleashService(EnvironmentProperties environmentProperties) {
//        String unleashUrl = ofNullable(environmentProperties.getUnleashApiUrl()).orElse("https://unleash.nais.adeo.no/api/");
//        return new UnleashService(UnleashServiceConfig.builder()
//                .applicationName(requireApplicationName())
//                .unleashApiUrl(unleashUrl)
//                .build()
//        );
//    }

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
