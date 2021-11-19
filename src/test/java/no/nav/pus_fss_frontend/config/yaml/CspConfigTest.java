package no.nav.pus_fss_frontend.config.yaml;


import no.nav.common.test.junit.SystemPropertiesRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static no.nav.pus_fss_frontend.config.yaml.YamlConfigResolver.CONFIGURATION_LOCATION_PROPERTY;
import static no.nav.pus_fss_frontend.config.yaml.YamlConfigResolver.resolveConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class CspConfigTest {
    @Rule
    public SystemPropertiesRule systemPropertiesRule = new SystemPropertiesRule();

    @Test
    public void dontAddHeaderIfDisabled() {
        HashMap<String, String> headers = new HashMap<>();
        CspConfig config = new CspConfig();
        config.applyTo(headers);

        assertThat(headers).isEmpty();
    }

    @Test
    public void addHeaderIfEnabled() {
        HashMap<String, String> headers = new HashMap<>();
        CspConfig config = new CspConfig()
                .setMode(CspMode.ENFORCE);
        config.applyTo(headers);

        assertThat(headers).hasSize(1);
        assertThat(headers.get(CspMode.ENFORCE.header)).isEqualTo("" +
                " default-src 'self';" +
                " report-uri /frontendlogger/api/warn;"
        );
    }

    @Test
    public void generateCspHeader() {
        systemPropertiesRule.setProperty(CONFIGURATION_LOCATION_PROPERTY, YamlConfigResolverTest.class.getResource("/config/rich.config.yaml").getFile());
        YamlConfig config = resolveConfig();
        assertThat(config.csp.generateCspHeader()).isEqualTo(""+
                " default-src 'self' url.no;" +
                " child-src url.no;" +
                " connect-src 'self' www.url.no;" +
                " font-src 'self' *.url.no;" +
                " frame-src www.url.no;" +
                " img-src 'self' url.no data:;" +
                " manifest-src url.no;" +
                " media-src url.no;" +
                " object-src url.no;" +
                " prefetch-src url.no;" +
                " script-src 'self' 'unsafe-inline' 'unsafe-eval' url1.no *.url2.no;" +
                " script-src-elem *.url2.no;" +
                " script-src-attr 'none';" +
                " style-src 'self' *.url.domain.no;" +
                " style-src-elem *.url.domain.no;" +
                " style-src-attr *.url.domain.no;" +
                " report-uri /frontendlogger/api/warn;"
        );
    }

}
