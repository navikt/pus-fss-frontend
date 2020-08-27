package no.nav.pus_fss_frontend.config;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.featuretoggle.UnleashServiceConfig;
import no.nav.pus_fss_frontend.config.EnvironmentProperties;
import no.nav.pus_fss_frontend.servlet.LoginServlet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Optional.ofNullable;
import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;

@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
public class TestApplicationConfig {

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

}
