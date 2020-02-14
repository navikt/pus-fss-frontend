import no.nav.apiapp.ApiApp;
import no.nav.brukerdialog.security.Constants;
import no.nav.common.nais.utils.NaisUtils;
import no.nav.fssfrontend.ApplicationConfig;
import no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants;
import no.nav.sbl.util.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        loadSecret("service_user", (serviceUser) -> {
            EnvironmentUtils.setProperty(StsSecurityConstants.SYSTEMUSER_USERNAME, serviceUser.username, PUBLIC);
            EnvironmentUtils.setProperty(StsSecurityConstants.SYSTEMUSER_PASSWORD, serviceUser.password, SECRET);
        });
        loadSecret("isso-rp-user", (issoRPUser) -> {
            EnvironmentUtils.setProperty(Constants.ISSO_RP_USER_USERNAME_PROPERTY_NAME, issoRPUser.username, PUBLIC);
            EnvironmentUtils.setProperty(Constants.ISSO_RP_USER_PASSWORD_PROPERTY_NAME, issoRPUser.password, SECRET);
        });

        ApiApp.runApp(ApplicationConfig.class, args);
    }

    private static void loadSecret(String secretName, Consumer<NaisUtils.Credentials> consumer) {
        try {
            consumer.accept(NaisUtils.getCredentials(secretName));
        } catch (Exception e) {
            log.warn(String.format("Kunne ikke laste inn secret: %s", secretName), e);
        }
    }
}
