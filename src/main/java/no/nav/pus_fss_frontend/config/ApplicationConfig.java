package no.nav.pus_fss_frontend.config;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.featuretoggle.UnleashServiceConfig;
import no.nav.common.health.selftest.SelfTestChecks;
import no.nav.common.health.selftest.SelfTestMeterBinder;
import no.nav.common.rest.filter.HttpFilterHeaders;
import no.nav.common.rest.filter.SetHeaderFilter;
import no.nav.pus_fss_frontend.config.yaml.YamlConfig;
import no.nav.pus_fss_frontend.filter.LoginRedirectFilter;
import no.nav.pus_fss_frontend.utils.Utils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;
import static no.nav.pus_fss_frontend.config.yaml.YamlConfigResolver.resolveConfig;

@Configuration
@EnableConfigurationProperties({EnvironmentProperties.class})
public class ApplicationConfig {
    private final YamlConfig config = resolveConfig();

    @Bean
    public FilterRegistrationBean<SetHeaderFilter> setStandardHttpHeadersFilter() {
        Map<String, String> headers = new HashMap<>(HttpFilterHeaders.STANDARD_HEADERS);
        config.csp.applyTo(headers);

        FilterRegistrationBean<SetHeaderFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SetHeaderFilter(headers));
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<LoginRedirectFilter> loginRedirectFilter(UnleashService unleashService, EnvironmentProperties environmentProperties) {
        FilterRegistrationBean<LoginRedirectFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LoginRedirectFilter(unleashService, environmentProperties));
        registration.setOrder(2);
        registration.addUrlPatterns("/*");
        return registration;
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

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer(EnvironmentProperties environmentProperties) {
        return (factory) -> {
            String contextPath = getContextPath(environmentProperties);
            factory.setContextPath(contextPath);
        };
    }

    static String getContextPath(EnvironmentProperties environmentProperties) {
        String appContextPathEnv = environmentProperties.getContextPath();
        String contextPath = !StringUtils.isEmpty(appContextPathEnv)? appContextPathEnv : environmentProperties.getNaisAppName();
        if (StringUtils.isEmpty(contextPath)) return "";
        return Utils.ensureStringIsPrefixed(contextPath, "/");
    }

}
