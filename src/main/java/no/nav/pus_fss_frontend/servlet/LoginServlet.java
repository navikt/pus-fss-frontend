package no.nav.pus_fss_frontend.servlet;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import no.nav.common.auth.oidc.OidcTokenValidator;
import no.nav.common.auth.utils.CookieTokenFinder;
import no.nav.common.auth.utils.TokenFinder;
import no.nav.common.auth.utils.TokenUtils;
import no.nav.common.featuretoggle.UnleashService;
import no.nav.pus_fss_frontend.config.EnvironmentProperties;
import no.nav.pus_fss_frontend.utils.ToggleUtils;
import no.nav.pus_fss_frontend.utils.Utils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static no.nav.common.auth.Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME;
import static no.nav.common.auth.Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME;

public class LoginServlet extends HttpServlet {

    private final static long TEN_MINUTES = 10 * 60 * 1000;

    private final UnleashService unleashService;

    private final EnvironmentProperties environmentProperties;

    private final TokenFinder openAmTokenFinder;

    private final TokenFinder azureAdTokenFinder;

    private final OidcTokenValidator openAmTokenValidator;

    private final OidcTokenValidator azureAdTokenValidator;

    public LoginServlet(UnleashService unleashService, EnvironmentProperties environmentProperties) {
        this.unleashService = unleashService;
        this.environmentProperties = environmentProperties;

        openAmTokenFinder = new CookieTokenFinder(OPEN_AM_ID_TOKEN_COOKIE_NAME);
        azureAdTokenFinder = new CookieTokenFinder(AZURE_AD_ID_TOKEN_COOKIE_NAME);

        openAmTokenValidator = new OidcTokenValidator(
                environmentProperties.getOpenAmDiscoveryUrl(), List.of(environmentProperties.getOpenAmVeilarbloginClientId())
        );

        azureAdTokenValidator = new OidcTokenValidator(
                environmentProperties.getAadDiscoveryUrl(), List.of(environmentProperties.getAadVeilarbloginClientId())
        );
    }

    @Override
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        String currentLocation = Utils.urlEncode(Utils.getFullURL(request));
        Optional<String> maybeToken = openAmTokenFinder.findToken(request)
                .or(() -> azureAdTokenFinder.findToken(request));

        try {
            JWT jwtToken = JWTParser.parse(maybeToken.orElseThrow());

            List<String> tokenAudiences = jwtToken.getJWTClaimsSet().getAudience();

            boolean isOpenAmToken = tokenAudiences.contains(environmentProperties.getOpenAmVeilarbloginClientId());

            if (isOpenAmToken) {
                openAmTokenValidator.validate(jwtToken);
            } else {
                azureAdTokenValidator.validate(jwtToken);
            }

            if (TokenUtils.expiresWithin(jwtToken, TEN_MINUTES)) {
               throw new RuntimeException("Token expires soon, redirect to login");
            }

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

    private void redirectToLogin(HttpServletResponse response, String encodedReturnUrl) throws IOException {
        String loginUrl;

        if (ToggleUtils.skalBrukeAzureAd(unleashService)) {
            loginUrl = environmentProperties.getAadVeilarbloginLoginUrl() + "?returnUrl=" + encodedReturnUrl;
        } else {
            loginUrl = environmentProperties.getOpenAmVeilarbloginLoginUrl() + "?url=" + encodedReturnUrl;
        }

        response.sendRedirect(loginUrl);
    }

}
