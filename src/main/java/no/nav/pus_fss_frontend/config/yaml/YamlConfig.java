package no.nav.pus_fss_frontend.config.yaml;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class YamlConfig {
    public CspConfig csp = new CspConfig();
}
