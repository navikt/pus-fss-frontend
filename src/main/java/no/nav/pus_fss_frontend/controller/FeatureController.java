package no.nav.pus_fss_frontend.controller;

import com.nimbusds.jwt.JWTParser;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.UnleashContext;
import no.nav.common.auth.utils.CookieUtils;
import no.nav.pus_fss_frontend.service.UnleashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.nav.common.auth.Constants.AAD_NAV_IDENT_CLAIM;
import static no.nav.common.auth.Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME;

/**
 * Implements equal api as https://github.com/navikt/pus-decorator/blob/master/src/main/java/no/nav/pus/decorator/feature/FeatureResource.java
 */
@Slf4j
@RestController
@RequestMapping("/api/feature")
public class FeatureController {

    private static final String UNLEASH_SESSION_ID_COOKIE_NAME = "UNLEASH_SESSION_ID";

    private final UnleashService unleashService;

    @Autowired
    public FeatureController(UnleashService unleashService) {
        this.unleashService = unleashService;
    }

    @GetMapping
    public Map<String, Boolean> getFeatures(
            @RequestParam("feature") List<String> features,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String userId = CookieUtils.getCookie(AAD_NAV_IDENT_CLAIM, request)
                .map((cookie) -> getSubject(cookie.getValue(), "NAVident"))
                .orElse(
                        CookieUtils.getCookie(OPEN_AM_ID_TOKEN_COOKIE_NAME, request)
                                .map((cookie) -> getSubject(cookie.getValue(), "sub"))
                                .orElse("")
                );


        String sessionId = CookieUtils.getCookie(UNLEASH_SESSION_ID_COOKIE_NAME, request)
                .map(Cookie::getValue)
                .orElseGet(() -> generateSessionId(response));

        UnleashContext unleashContext = UnleashContext.builder()
                .userId(userId)
                .sessionId(sessionId)
                .remoteAddress(request.getRemoteAddr())
                .build();

        return features.stream().collect(Collectors.toMap(e -> e, e -> unleashService.isEnabled(e, unleashContext)));
    }

    private static String getSubject(String jwt, String claim) {
        try {
            return JWTParser.parse(jwt).getJWTClaimsSet().getStringClaim(claim);
        } catch (ParseException e) {
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
