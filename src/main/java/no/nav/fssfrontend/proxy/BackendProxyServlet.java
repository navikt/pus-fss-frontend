package no.nav.fssfrontend.proxy;

import lombok.extern.slf4j.Slf4j;
import no.nav.apiapp.selftest.Helsesjekk;
import no.nav.apiapp.selftest.HelsesjekkMetadata;
import no.nav.log.LogFilter;
import no.nav.log.MarkerBuilder;
import no.nav.sbl.util.EnvironmentUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.proxy.ProxyServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Optional.ofNullable;
import static no.nav.log.LogFilter.CONSUMER_ID_HEADER_NAME;
import static no.nav.log.LogFilter.PREFERRED_NAV_CALL_ID_HEADER_NAME;
import static no.nav.pus.decorator.proxy.BackendProxyConfig.RequestRewrite.REMOVE_CONTEXT_PATH;
import static no.nav.sbl.util.StringUtils.nullOrEmpty;
import static no.nav.sbl.util.StringUtils.of;

@Slf4j
public class BackendProxyServlet extends ProxyServlet implements Helsesjekk {

    private static final String CALL_ID = "callId";

    private final String applicationName = EnvironmentUtils.requireApplicationName();

    private final no.nav.pus.decorator.proxy.BackendProxyConfig backendProxyConfig;
    private final String id;
    private final HelsesjekkMetadata helsesjekkMetadata;
    private final String pingUrl;
    private final boolean removeContextPath;
    private final int contextPathLength;


    public BackendProxyServlet(no.nav.pus.decorator.proxy.BackendProxyConfig backendProxyConfig) {
        this.backendProxyConfig = backendProxyConfig;
        this.id = (BackendProxyServlet.class.getSimpleName() + "_" + backendProxyConfig.contextPath.substring(1)).toLowerCase();
        this.removeContextPath = backendProxyConfig.requestRewrite == REMOVE_CONTEXT_PATH;
        this.contextPathLength = backendProxyConfig.contextPath.length();

        this.pingUrl = backendProxyConfig.baseUrl + ofNullable(backendProxyConfig.pingRequestPath).orElseGet(this::defaultPingPath);
        this.helsesjekkMetadata = new HelsesjekkMetadata(
                "proxy_" + id,
                pingUrl,
                "ping backend for " + backendProxyConfig.contextPath,
                false
        );
    }

    private String defaultPingPath() {
        String pingContextPath = removeContextPath ? "" : backendProxyConfig.contextPath;
        return pingContextPath + "/api/ping";
    }

    private String targetUrl(String requestURI) {
        return backendProxyConfig.baseUrl + targetPath(requestURI);
    }

    private String targetPath(String requestURI) {
        return removeContextPath ? requestURI.substring(contextPathLength) : requestURI;
    }

    public no.nav.pus.decorator.proxy.BackendProxyConfig getBackendProxyConfig() {
        return backendProxyConfig;
    }

//    public String getId() {
//        return id;
//    }
//
//    public String getPingUrl() {
//        return pingUrl;
//    }

    @Override
    protected HttpClient newHttpClient() {
        return new ProxyClient();
    }

    @Override
    protected String rewriteTarget(HttpServletRequest clientRequest) {
        StringBuilder sb = new StringBuilder(targetUrl(clientRequest.getRequestURI()));
        of(clientRequest.getQueryString()).ifPresent(q -> {
            sb.append("?");
            sb.append(q);
        });
        String target = sb.toString();

        String callId = LogFilter.resolveCallId(clientRequest);
        clientRequest.setAttribute(CALL_ID, callId);
        return target;
    }

    @Override
    protected void copyRequestHeaders(HttpServletRequest clientRequest, Request proxyRequest) {
        super.copyRequestHeaders(clientRequest, proxyRequest);
        if (nullOrEmpty(clientRequest.getHeader(CONSUMER_ID_HEADER_NAME))) {
            proxyRequest.header(CONSUMER_ID_HEADER_NAME, applicationName);
        }
        if (nullOrEmpty(clientRequest.getHeader(PREFERRED_NAV_CALL_ID_HEADER_NAME))) {
            proxyRequest.header(PREFERRED_NAV_CALL_ID_HEADER_NAME, (String) clientRequest.getAttribute(CALL_ID));
        }
    }

    @Override
    public void helsesjekk() throws Throwable {
        ContentResponse contentResponse = getHttpClient().GET(pingUrl);
        int status = contentResponse.getStatus();
        if (status != 200) {
            throw new IllegalStateException(status + " != 200");
        }
    }

    @Override
    public HelsesjekkMetadata getMetadata() {
        return helsesjekkMetadata;
    }


    @Override
    protected Response.Listener newProxyResponseListener(HttpServletRequest request, HttpServletResponse response) {
        return new ApplicationProxyResponseListener(request, response);
    }

    private class ApplicationProxyResponseListener extends ProxyResponseListener {

        private final HttpServletRequest request;

        private ApplicationProxyResponseListener(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
            this.request = request;
        }

        @Override
        public void onComplete(Result result) {
            Object callId = request.getAttribute(CALL_ID);
            Response response = result.getResponse();
            Throwable responseFailure = result.getResponseFailure();

            new MarkerBuilder()
                    .field(CALL_ID, callId)
                    .field("method", request.getMethod())
                    .field("path", request.getRequestURI())
                    .field("status", response.getStatus())
                    .log((marker, message) -> {
                        if (responseFailure != null) {
                            log.warn(marker, message, responseFailure);
                        } else {
                            log.info(marker, message);
                        }
                    });

            super.onComplete(result);
        }
    }


}
