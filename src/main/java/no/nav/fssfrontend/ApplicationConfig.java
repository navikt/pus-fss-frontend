package no.nav.fssfrontend;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.fssfrontend.config.Config;
import no.nav.fssfrontend.config.ConfigResolver;
import no.nav.fssfrontend.proxy.BackendProxyServlet;
import no.nav.pus.decorator.proxy.BackendProxyConfig;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import no.nav.sbl.featuretoggle.unleash.UnleashServiceConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;
import static no.nav.apiapp.ServletUtil.leggTilServlet;
import static no.nav.sbl.featuretoggle.unleash.UnleashServiceConfig.UNLEASH_API_URL_PROPERTY_NAME;
import static no.nav.sbl.util.EnvironmentUtils.getOptionalProperty;
import static no.nav.sbl.util.EnvironmentUtils.requireApplicationName;

@Configuration
@Import({
        FeatureResource.class
})
public class ApplicationConfig implements ApiApplication.NaisApiApplication {

    private Config config = ConfigResolver.resolveConfig();

    @Override
    public boolean brukSTSHelsesjekk() {
        return false;
    }

    @Override
    public void startup(ServletContext servletContext) {
        leggTilServlet(servletContext, ApplicationServlet.class, "/*");
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator.issoLogin();

        apiAppConfigurator.customizeJetty(jetty -> {
            List<BackendProxyConfig> backendProxyConfigs = ofNullable(config.proxy).orElseGet(Collections::emptyList);
            if (!backendProxyConfigs.isEmpty()) {
                addBackendProxies(apiAppConfigurator, jetty, backendProxyConfigs);
            }
        });
    }

    private void addBackendProxies(ApiAppConfigurator apiAppConfigurator, Jetty jetty, List<BackendProxyConfig> backendProxyConfigs) {
        HandlerCollection handlerCollection = new HandlerCollection();
        backendProxyConfigs.stream()
                .map(BackendProxyServlet::new)
                .forEach(backendProxyServlet -> {
                    handlerCollection.addHandler(proxyHandler(backendProxyServlet));
                    apiAppConfigurator.selfTest(backendProxyServlet);
                });

        Server server = jetty.server;
        handlerCollection.addHandler(server.getHandler());
        server.setHandler(handlerCollection);
    }

    private static ServletContextHandler proxyHandler(BackendProxyServlet backendProxyServlet) {
        BackendProxyConfig backendProxyConfig = backendProxyServlet.getBackendProxyConfig();
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setAllowNullPathInfo(true);
        servletContextHandler.addServlet(new ServletHolder(backendProxyServlet), "/*");
        servletContextHandler.setContextPath(backendProxyConfig.getContextPath());
        return servletContextHandler;
    }

    @Bean
    public UnleashService unleashService() {
        return new UnleashService(UnleashServiceConfig.builder()
                .applicationName(requireApplicationName())
                .unleashApiUrl(getOptionalProperty(UNLEASH_API_URL_PROPERTY_NAME).orElse("https://unleash.nais.adeo.no/api/"))
                .build()
        );
    }

}
