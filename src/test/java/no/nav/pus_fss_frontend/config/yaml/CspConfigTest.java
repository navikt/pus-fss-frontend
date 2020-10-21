package no.nav.pus_fss_frontend.config.yaml;


import lombok.SneakyThrows;
import no.nav.common.test.junit.SystemPropertiesRule;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;
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
    public void addHeaderIfEabled() {
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
                " script-src 'self' 'unsafe-inline' 'unsafe-eval' url1.no *.url2.no;" +
                " img-src 'self' url.no data:;" +
                " style-src 'self' *.url.domain.no;" +
                " font-src 'self' *.url.no;" +
                " connect-src 'self' www.url.no;" +
                " frame-src www.url.no;" +
                " report-uri /frontendlogger/api/warn;"
        );
    }

    @SneakyThrows
    private String readResource(String name) {
        InputStream resourceAsStream = YamlConfigResolverTest.class.getResourceAsStream(name);
        return IOUtils.toString(resourceAsStream, "UTF-8");
    }
}
