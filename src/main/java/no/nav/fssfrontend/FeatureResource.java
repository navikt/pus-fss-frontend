package no.nav.fssfrontend;

import no.finn.unleash.UnleashContext;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.nav.brukerdialog.security.Constants.ID_TOKEN_COOKIE_NAME;
import static no.nav.sbl.util.StringUtils.of;

/**
 * Implements equal api as https://github.com/navikt/pus-decorator/blob/master/src/main/java/no/nav/pus/decorator/feature/FeatureResource.java
 */
@Path("/feature")
@Component
public class FeatureResource {

    private static final Logger log = LoggerFactory.getLogger(FeatureResource.class);

    private static final String UNLEASH_SESSION_ID_COOKIE_NAME = "UNLEASH_SESSION_ID";
    private static final JwtConsumer JWT_PARSER = new JwtConsumerBuilder()
            .setSkipAllValidators()
            .setSkipSignatureVerification()
            .build();

    private final UnleashService unleashService;

    @Inject
    public FeatureResource(UnleashService unleashService) {
        this.unleashService = unleashService;
    }

    @GET
    public Map<String, Boolean> getFeatures(
            @QueryParam("feature") List<String> features,
            @CookieParam(UNLEASH_SESSION_ID_COOKIE_NAME) String sessionId,
            @CookieParam(ID_TOKEN_COOKIE_NAME) String issoOidcToken,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response
    ) {
        UnleashContext unleashContext = UnleashContext.builder()
                .userId(of(issoOidcToken).map(FeatureResource::getSubject).orElse(null))
                .sessionId(of(sessionId).orElseGet(() -> generateSessionId(response)))
                .remoteAddress(request.getRemoteAddr())
                .build();
        return features.stream().collect(Collectors.toMap(e -> e, e -> unleashService.isEnabled(e, unleashContext)));
    }

    private static String getSubject(String jwt) {
        try {
            return JWT_PARSER.processToClaims(jwt).getSubject();
        } catch (MalformedClaimException | InvalidJwtException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    private String generateSessionId(HttpServletResponse httpServletRequest) {
        UUID uuid = UUID.randomUUID();
        String sessionId = Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits());
        Cookie cookie = new Cookie(UNLEASH_SESSION_ID_COOKIE_NAME, sessionId);
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        httpServletRequest.addCookie(cookie);
        return sessionId;
    }

}
