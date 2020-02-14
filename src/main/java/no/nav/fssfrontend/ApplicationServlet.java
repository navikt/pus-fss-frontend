package no.nav.fssfrontend;

import no.nav.brukerdialog.security.jaspic.TokenLocator;
import no.nav.common.oidc.OidcTokenValidator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static no.nav.brukerdialog.security.Constants.ID_TOKEN_COOKIE_NAME;
import static no.nav.fssfrontend.ApplicationConfig.*;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class ApplicationServlet extends HttpServlet {

    private OidcTokenValidator openAmTokenValidator;
    private String openAmLoginUrl;
    private TokenLocator openAmTokenLocator;

    public ApplicationServlet() {
        String openAmClientId = getRequiredProperty(OPENAM_CLIENT_ID_PROPERTY);
        String openAmDiscoveryUrl = getRequiredProperty(OPENAM_DISCOVERY_URL_PROPERTY);

        openAmTokenLocator = new TokenLocator(ID_TOKEN_COOKIE_NAME, null);
        openAmLoginUrl = getRequiredProperty(OPENAM_LOGIN_URL_PROPERTY);
        openAmTokenValidator = new OidcTokenValidator(openAmDiscoveryUrl, openAmClientId);
    }

    @Override
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        String currentLocation = Utils.urlEncode(Utils.getFullURL(request));
        Optional<String> token = openAmTokenLocator.getToken(request);

        try {
            openAmTokenValidator.validate(token.orElseThrow(() -> new RuntimeException("Missing id_token")));
        } catch (Exception e) {
            redirectToLogin(response, currentLocation);
            return;
        }

        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("default");
        String fileRequestPattern = "^(.+\\..{1,4})$";

        if (!request.getRequestURI().matches(fileRequestPattern)) {
            RequestDispatcher index = getServletContext().getRequestDispatcher("/index.html");
            index.forward(request, response);
        } else {
            dispatcher.forward(request, response);
        }
    }

    private void redirectToLogin(HttpServletResponse response, String returnUrl) throws IOException {
        String loginUrl = openAmLoginUrl + "?url=" + returnUrl;
        response.sendRedirect(loginUrl);
    }

}
