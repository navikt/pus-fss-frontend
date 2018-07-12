package no.nav.fssfrontend;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.sbl.util.EnvironmentUtils;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;

import static no.nav.apiapp.ServletUtil.leggTilServlet;
import static no.nav.sbl.util.EnvironmentUtils.getOptionalProperty;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class ApplicationConfig implements ApiApplication.NaisApiApplication {

    @Override
    public boolean brukSTSHelsesjekk() {
        return false;
    }

    @Override
    public String getContextPath() {
        return EnvironmentUtils.requireApplicationName();
    }

    @Override
    public void startup(ServletContext servletContext) {
        leggTilServlet(servletContext, ApplicationServlet.class, "/*");
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator.issoLogin();
    }

}
