package no.nav.pus_fss_frontend.config.yaml;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.common.auth.Constants;

@Data
@Accessors(chain = true)
public class IdTokenNames {
    public String openAm = Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME;
    public String azureAd = Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME;
}
