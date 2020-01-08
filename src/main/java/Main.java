import no.nav.apiapp.ApiApp;
import no.nav.common.nais.utils.NaisUtils;
import no.nav.fssfrontend.ApplicationConfig;
import no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants;
import no.nav.sbl.util.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        setupServiceUser();
        ApiApp.runApp(ApplicationConfig.class, args);
    }

    private static void setupServiceUser() {
        try {
            NaisUtils.Credentials serviceUser = NaisUtils.getCredentials("service_user");
            EnvironmentUtils.setProperty(StsSecurityConstants.SYSTEMUSER_USERNAME, serviceUser.username, PUBLIC);
            EnvironmentUtils.setProperty(StsSecurityConstants.SYSTEMUSER_PASSWORD, serviceUser.password, SECRET);
        } catch (Exception e) {
            log.warn("Kunne ikke laste inn service bruker.", e);
        }
    }

}
