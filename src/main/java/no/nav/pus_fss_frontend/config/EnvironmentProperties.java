package no.nav.pus_fss_frontend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.env")
public class EnvironmentProperties {

    private String openAmDiscoveryUrl;

    private String openAmVeilarbloginClientId;

    private String openAmVeilarbloginLoginUrl;


    private String aadDiscoveryUrl;

    private String aadVeilarbloginClientId;

    private String aadVeilarbloginLoginUrl;


    private String unleashUrl;

    private String naisAppName;

    private String contextPath;

    public List<String> getOpenAmVeilarbloginClientId() {
        return parseKommaListe(openAmVeilarbloginClientId);
    }

    public List<String> getAadVeilarbloginClientId() {
        return parseKommaListe(aadVeilarbloginClientId);
    }

    private static List<String> parseKommaListe(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
