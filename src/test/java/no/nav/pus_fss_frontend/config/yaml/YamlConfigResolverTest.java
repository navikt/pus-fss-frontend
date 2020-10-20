package no.nav.pus_fss_frontend.config.yaml;

import lombok.SneakyThrows;
import no.nav.common.test.junit.SystemPropertiesRule;
import no.nav.common.yaml.YamlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import java.io.InputStream;

import static no.nav.pus_fss_frontend.config.yaml.YamlConfigResolver.CONFIGURATION_LOCATION_PROPERTY;
import static no.nav.pus_fss_frontend.config.yaml.YamlConfigResolver.resolveConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class YamlConfigResolverTest {
    @Rule
    public SystemPropertiesRule systemPropertiesRule = new SystemPropertiesRule();

    @Test
    public void emptyConfig() {
        YamlConfig config = resolveConfig();
        assertThat(config).isEqualTo(new YamlConfig()
                .setCsp(new CspConfig()
                        .setMode(CspMode.NONE)
                        .setReportUri("/frontendlogger/api/warn")
                )
        );
        assertThat(YamlUtils.toYaml(config)).isEqualTo(readResource("/config/default.config.yaml"));
    }

    @Test
    public void enabledDefault() {
        systemPropertiesRule.setProperty(CONFIGURATION_LOCATION_PROPERTY, YamlConfigResolverTest.class.getResource("/config/enable.config.yaml").getFile());
        YamlConfig config = resolveConfig();
        assertThat(config).isEqualTo(new YamlConfig()
                .setCsp(new CspConfig()
                        .setMode(CspMode.REPORT_ONLY)
                        .setReportUri("/frontendlogger/api/warn")
                )
        );
    }

    @Test
    public void interpolatedValues() {
        systemPropertiesRule.setProperty(CONFIGURATION_LOCATION_PROPERTY, YamlConfigResolverTest.class.getResource("/config/interpolated.config.yaml").getFile());
        systemPropertiesRule.setProperty("ENVIRONMENT", "q");

        YamlConfig config = resolveConfig();
        assertThat(config).isEqualTo(new YamlConfig()
                .setCsp(new CspConfig()
                        .setMode(CspMode.ENFORCE)
                        .setScriptSrc("'self' app.url.no app-q.url.no")
                        .setReportUri("/frontendlogger/api/warn")
                )
        );
    }

    @Test
    public void richConfig() {
        systemPropertiesRule.setProperty(CONFIGURATION_LOCATION_PROPERTY, YamlConfigResolverTest.class.getResource("/config/rich.config.yaml").getFile());
        YamlConfig config = resolveConfig();

        assertThat(config).isEqualTo(new YamlConfig()
                .setCsp(new CspConfig()
                        .setMode(CspMode.ENFORCE)
                        .setDefaultSrc("'self' url.no")
                        .setScriptSrc("'self' 'unsafe-inline' 'unsafe-eval' url1.no *.url2.no")
                        .setImgSrc("'self' url.no data:")
                        .setStyleSrc("'self' *.url.domain.no")
                        .setFontSrc("'self' *.url.no")
                        .setConnectSrc("'self' www.url.no")
                        .setFrameSrc("www.url.no")
                        .setReportUri("/frontendlogger/api/warn")
                )
        );
    }

    @Test
    public void interpolate() {
        systemPropertiesRule.setProperty("A","1");
        systemPropertiesRule.setProperty("B","2");
        systemPropertiesRule.setProperty("C","3");
        systemPropertiesRule.setProperty("D","4");

        assertThat(YamlConfigResolver.interpolate("a:{{ A }} b:{{B}} c:{{ C}} d:{{D }}")).isEqualTo("a:1 b:2 c:3 d:4");
        assertThat(YamlConfigResolver.interpolate("{{ A }}{{B}}{{ C}}{{D }}")).isEqualTo("1234");
        assertThat(YamlConfigResolver.interpolate("abcd")).isEqualTo("abcd");

        assertThatThrownBy(() -> YamlConfigResolver.interpolate("{{ NOT_AVAILABLE_PROPERTY }}")).hasMessageContaining("NOT_AVAILABLE_PROPERTY");
    }


    @SneakyThrows
    private String readResource(String name) {
        InputStream resourceAsStream = YamlConfigResolverTest.class.getResourceAsStream(name);
        return IOUtils.toString(resourceAsStream, "UTF-8");
    }
}
