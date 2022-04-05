package no.nav.pus_fss_frontend.filter;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import no.nav.common.auth.oidc.OidcTokenValidator;
import no.nav.common.auth.utils.CookieTokenFinder;
import no.nav.common.auth.utils.TokenFinder;
import no.nav.common.auth.utils.TokenUtils;
import no.nav.pus_fss_frontend.config.EnvironmentProperties;
import no.nav.pus_fss_frontend.config.yaml.IdTokenNames;
import no.nav.pus_fss_frontend.utils.ToggleUtils;
import no.nav.pus_fss_frontend.utils.Utils;
import no.nav.pus_fss_frontend.service.UnleashService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LoginRedirectFilter implements Filter {

    private final static long TEN_MINUTES = 10 * 60 * 1000;

    private final UnleashService unleashService;

    private final EnvironmentProperties environmentProperties;

    private final TokenFinder openAmTokenFinder;

    private final TokenFinder azureAdTokenFinder;

    private final OidcTokenValidator openAmTokenValidator;

    private final OidcTokenValidator azureAdTokenValidator;

    public LoginRedirectFilter(UnleashService unleashService, EnvironmentProperties environmentProperties, IdTokenNames cookieConfig) {
        this.unleashService = unleashService;
        this.environmentProperties = environmentProperties;

        openAmTokenFinder = new CookieTokenFinder(cookieConfig.openAm);
        azureAdTokenFinder = new CookieTokenFinder(cookieConfig.azureAd);

        openAmTokenValidator = new OidcTokenValidator(
                environmentProperties.getOpenAmDiscoveryUrl(),
                environmentProperties.getOpenAmVeilarbloginClientId()
        );

        azureAdTokenValidator = new OidcTokenValidator(
                environmentProperties.getAadDiscoveryUrl(),
                environmentProperties.getAadVeilarbloginClientId()
        );
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // We dont need login for /internal requests
        if (isInternalRequest(httpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String currentLocation = Utils.urlEncode(Utils.getFullURL(httpServletRequest));
        Optional<String> maybeToken = openAmTokenFinder.findToken(httpServletRequest)
                .or(() -> azureAdTokenFinder.findToken(httpServletRequest));

        try {
            JWT jwtToken = JWTParser.parse(maybeToken.orElseThrow());

            List<String> tokenAudiences = jwtToken.getJWTClaimsSet().getAudience();

            boolean isOpenAmToken = environmentProperties
                    .getOpenAmVeilarbloginClientId()
                    .stream()
                    .anyMatch(tokenAudiences::contains);

            if (isOpenAmToken) {
                openAmTokenValidator.validate(jwtToken);
            } else {
                azureAdTokenValidator.validate(jwtToken);
            }

            if (TokenUtils.expiresWithin(jwtToken, TEN_MINUTES)) {
                throw new RuntimeException("Token expires soon, redirect to login");
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            redirectToLogin(httpServletResponse, currentLocation);
        }
    }

    private boolean isInternalRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/internal");
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
