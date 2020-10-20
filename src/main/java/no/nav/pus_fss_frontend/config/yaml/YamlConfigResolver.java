package no.nav.pus_fss_frontend.config.yaml;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.common.utils.NaisUtils;
import no.nav.common.yaml.YamlUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class YamlConfigResolver {

    public static final String CONFIGURATION_LOCATION_PROPERTY = "CONFIGURATION_LOCATION";

    public static YamlConfig resolveConfig() {
        YamlConfig object = doResolveConfig();
        log.info("{}", object);
        return object;
    }

    @SneakyThrows
    private static YamlConfig doResolveConfig() {
        String configurationLocation = EnvironmentUtils.getOptionalProperty(CONFIGURATION_LOCATION_PROPERTY).orElse("/decorator.yaml");
        File file = new File(configurationLocation);
        if (file.exists()) {
            log.info("Reading configuration file at: {}", configurationLocation);
            return YamlUtils.fromYaml(interpolate(NaisUtils.getFileContent(file.getPath())), YamlConfig.class);
        } else {
            log.info("No configuration found at {}, using default configuration", configurationLocation);
            return new YamlConfig();
        }
    }

    static String interpolate(String string) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern.compile("\\{\\{(.+?)}}").matcher(string);
        while (matcher.find()) {
            String trim = matcher.group(1).trim();
            matcher.appendReplacement(sb, EnvironmentUtils.getRequiredProperty(trim));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
