import no.nav.brukerdialog.security.Constants;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.ServiceUser;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.testconfig.ApiAppTest;

import static java.lang.System.setProperty;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class MainTest {

    private static final String TEST_PORT = "8965";
    private static final String APPLICATION_NAME = "fssfrontend";

    private static void setupSecurity(){
        String issoHost = FasitUtils.getBaseUrl("isso-host");
        String issoJWS = FasitUtils.getBaseUrl("isso-jwks");
        String issoISSUER = FasitUtils.getBaseUrl("isso-issuer");
        String issoIsAlive = FasitUtils.getBaseUrl("isso.isalive", FasitUtils.Zone.FSS);
        ServiceUser srvveilarbdemo = FasitUtils.getServiceUser("srvveilarbdemo", "veilarbdemo");
        ServiceUser isso_rp_user = FasitUtils.getServiceUser("isso-rp-user", "veilarbdemo");

        setProperty(Constants.ISSO_HOST_URL_PROPERTY_NAME, issoHost, PUBLIC);
        setProperty(Constants.ISSO_RP_USER_USERNAME_PROPERTY_NAME, isso_rp_user.getUsername(), PUBLIC);
        setProperty(Constants.ISSO_RP_USER_PASSWORD_PROPERTY_NAME, isso_rp_user.getPassword(), SECRET);
        setProperty(Constants.ISSO_JWKS_URL_PROPERTY_NAME, issoJWS, PUBLIC);
        setProperty(Constants.ISSO_ISSUER_URL_PROPERTY_NAME, issoISSUER, PUBLIC);
        setProperty(Constants.ISSO_ISALIVE_URL_PROPERTY_NAME, issoIsAlive, PUBLIC);
        setProperty(SecurityConstants.SYSTEMUSER_USERNAME, srvveilarbdemo.getUsername(), PUBLIC);
        setProperty(SecurityConstants.SYSTEMUSER_PASSWORD, srvveilarbdemo.getPassword(), SECRET);

        String loginUrl = FasitUtils.getBaseUrl("veilarblogin.redirect-url", FasitUtils.Zone.FSS);
        setProperty(Constants.OIDC_REDIRECT_URL_PROPERTY_NAME, loginUrl);
    }

    public static void main(String... args) throws Exception {
        ApiAppTest.setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build());
        setProperty(EnvironmentUtils.APP_NAME_PROPERTY_NAME, APPLICATION_NAME, PUBLIC);
        setupSecurity();
        Main.main(TEST_PORT);
    }

}
