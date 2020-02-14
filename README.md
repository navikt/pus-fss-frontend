![](https://github.com/navikt/pus-fss-frontend/workflows/Test,%20build%20and%20push/badge.svg)

# pus-fss-frontend
Baseimage som server en statisk frontend bak innlogging

## Bruk
Imaget forventer miljøvariblene nedenfor;
- ISSO_HOST_URL
- ISSO_ISALIVE_URL
- ISSO_ISSUER_URL
- ISSO_JWKS_URL
- OIDC_REDIRECT_URL
- no.nav.modig.security.systemuser.username
- no.nav.modig.security.systemuser.password
- isso-rp-user.username
- isso-rp-user.password 

Om naiserator blir brukt så vil appen prøve å laste brukernavn og passord fra `service_user` og `isso-rp-user`.
