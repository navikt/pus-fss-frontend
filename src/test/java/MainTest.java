import no.nav.brukerdialog.security.Constants;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.ServiceUser;
import no.nav.testconfig.ApiAppTest;

import static no.nav.ApplicationConfig.APPLICATION_NAME_PROPERTY;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class MainTest {

    public static final String TEST_PORT = "8965";
    private static final String APPLICATION_NAME = "fssfrontend";

    public static void setupSecurity(){
        String issoHost = FasitUtils.getBaseUrl("isso-host");
        String issoJWS = FasitUtils.getBaseUrl("isso-jwks");
        String issoISSUER = FasitUtils.getBaseUrl("isso-issuer");
        String issoIsAlive = FasitUtils.getBaseUrl("isso.isalive", FasitUtils.Zone.FSS);
        ServiceUser srvveilarbdemo = FasitUtils.getServiceUser("srvveilarbdemo", "veilarbdemo");
        ServiceUser isso_rp_user = FasitUtils.getServiceUser("isso-rp-user", "veilarbdemo");

        System.setProperty(Constants.ISSO_HOST_URL_PROPERTY_NAME, issoHost);
        System.setProperty(Constants.ISSO_RP_USER_USERNAME_PROPERTY_NAME, isso_rp_user.getUsername());
        System.setProperty(Constants.ISSO_RP_USER_PASSWORD_PROPERTY_NAME, isso_rp_user.getPassword());
        System.setProperty(Constants.ISSO_JWKS_URL_PROPERTY_NAME, issoJWS);
        System.setProperty(Constants.ISSO_ISSUER_URL_PROPERTY_NAME, issoISSUER);
        System.setProperty(Constants.ISSO_ISALIVE_URL_PROPERTY_NAME, issoIsAlive);
        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, srvveilarbdemo.getUsername());
        System.setProperty(SecurityConstants.SYSTEMUSER_PASSWORD, srvveilarbdemo.getPassword());

        String loginUrl = FasitUtils.getBaseUrl("veilarblogin.redirect-url", FasitUtils.Zone.FSS);
        System.setProperty(Constants.OIDC_REDIRECT_URL_PROPERTY_NAME, loginUrl);


    }

    public static void main(String... args) throws Exception {
        ApiAppTest.setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build());
        setProperty(APPLICATION_NAME_PROPERTY, APPLICATION_NAME, PUBLIC);
        setupSecurity();
        Main.main(TEST_PORT);
    }

}
