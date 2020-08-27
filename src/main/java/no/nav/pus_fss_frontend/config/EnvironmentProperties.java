package no.nav.pus_fss_frontend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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


    private String unleashApiUrl;

}
