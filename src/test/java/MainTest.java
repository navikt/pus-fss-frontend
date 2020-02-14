import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.fasit.FasitUtils;
import no.nav.fasit.ServiceUser;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.testconfig.ApiAppTest;

import static java.lang.System.setProperty;
import static no.nav.fssfrontend.ApplicationConfig.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class MainTest {

    private static final String TEST_PORT = "8965";
    private static final String APPLICATION_NAME = "fssfrontend";

    private static void setupSecurity(){

        ServiceUser srvveilarbdemo = FasitUtils.getServiceUser("srvveilarbdemo", "veilarbdemo");
        setProperty(SecurityConstants.SYSTEMUSER_USERNAME, srvveilarbdemo.getUsername(), PUBLIC);
        setProperty(SecurityConstants.SYSTEMUSER_PASSWORD, srvveilarbdemo.getPassword(), SECRET);

        setProperty(OPENAM_CLIENT_ID_PROPERTY, "veilarblogin-q0");
        setProperty(OPENAM_DISCOVERY_URL_PROPERTY, "https://isso-q.adeo.no/isso/oauth2/.well-known/openid-configuration");
        setProperty(OPENAM_LOGIN_URL_PROPERTY, "https://app-q0.adeo.no/veilarblogin/api/start");
    }

    public static void main(String... args) {
        ApiAppTest.setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build());
        setProperty(EnvironmentUtils.APP_NAME_PROPERTY_NAME, APPLICATION_NAME, PUBLIC);
        setupSecurity();
        Main.main(TEST_PORT);
    }

}
