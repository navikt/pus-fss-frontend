package no.nav.pus_fss_frontend.config.yaml;

import lombok.SneakyThrows;
import no.nav.common.yaml.YamlUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;

import static no.nav.pus_fss_frontend.config.yaml.YamlConfigResolver.CONFIGURATION_LOCATION_PROPERTY;
import static no.nav.pus_fss_frontend.config.yaml.YamlConfigResolver.resolveConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class YamlConfigResolverTest {

    @Test
    public void emptyConfig() {
        YamlConfig config = resolveConfig();
        assertThat(config).isEqualTo(new YamlConfig());
        assertThat(YamlUtils.toYaml(config).trim())
                .isEqualTo(readResource("/config/default.config.yaml").trim());
    }

    @Test
    public void enabledDefault() {
        System.setProperty(CONFIGURATION_LOCATION_PROPERTY, YamlConfigResolverTest.class.getResource("/config/enable.config.yaml").getFile());

        YamlConfig config = resolveConfig();

        assertThat(config).isEqualTo(new YamlConfig()
                .setCsp(new CspConfig()
                        .setMode(CspMode.REPORT_ONLY)
                        .setReportUri("/frontendlogger/api/warn")
                )
        );

        System.clearProperty(CONFIGURATION_LOCATION_PROPERTY);
    }

    @Test
    public void interpolatedValues() {
        System.setProperty(CONFIGURATION_LOCATION_PROPERTY, YamlConfigResolverTest.class.getResource("/config/interpolated.config.yaml").getFile());
        System.setProperty("ENVIRONMENT", "q");

        YamlConfig config = resolveConfig();
        assertThat(config).isEqualTo(new YamlConfig()
                .setCsp(new CspConfig()
                        .setMode(CspMode.ENFORCE)
                        .setScriptSrc("'self' app.url.no app-q.url.no")
                        .setReportUri("/frontendlogger/api/warn")
                )
        );

        System.clearProperty(CONFIGURATION_LOCATION_PROPERTY);
        System.clearProperty("ENVIRONMENT");
    }

    @Test
    public void richConfig() {
        System.setProperty(CONFIGURATION_LOCATION_PROPERTY, YamlConfigResolverTest.class.getResource("/config/rich.config.yaml").getFile());
        YamlConfig config = resolveConfig();

        assertThat(config).isEqualTo(new YamlConfig()
                .setCsp(new CspConfig()
                        .setMode(CspMode.ENFORCE)
                        .setDefaultSrc("'self' url.no")
                        .setScriptSrc("'self' 'unsafe-inline' 'unsafe-eval' url1.no *.url2.no")
                        .setScriptSrcElem("*.url2.no")
                        .setScriptSrcAttr("'none'")
                        .setImgSrc("'self' url.no data:")
                        .setStyleSrc("'self' *.url.domain.no")
                        .setStyleSrcElem("*.url.domain.no")
                        .setStyleSrcAttr("*.url.domain.no")
                        .setFontSrc("'self' *.url.no")
                        .setConnectSrc("'self' www.url.no")
                        .setFrameSrc("www.url.no")
                        .setChildSrc("url.no")
                        .setManifestSrc("url.no")
                        .setMediaSrc("url.no")
                        .setObjectSrc("url.no")
                        .setPrefetchSrc("url.no")
                        .setReportUri("/frontendlogger/api/warn")
                )
                .setIdTokenName(new IdTokenNames()
                        .setOpenAm("CUSTOM_ID_TOKEN")
                        .setAzureAd("CUSTOM_AAD_TOKEN")
                )
        );

        System.clearProperty(CONFIGURATION_LOCATION_PROPERTY);
    }

    @Test
    public void interpolate() {
        System.setProperty("A","1");
        System.setProperty("B","2");
        System.setProperty("C","3");
        System.setProperty("D","4");

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
