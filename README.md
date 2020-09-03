![](https://github.com/navikt/pus-fss-frontend/workflows/Test,%20build%20and%20push/badge.svg)

# pus-fss-frontend
Baseimage som server en statisk frontend bak innlogging

## Bruk
Webproxy må bli skrudd på for å nå login.microsoft.com.

Imaget forventer miljøvariblene nedenfor:
- NAIS_APP_NAME
- OPENAM_DISCOVERY_URL
- VEILARBLOGIN_OPENAM_CLIENT_ID
- VEILARBLOGIN_OPENAM_LOGIN_URL
- AAD_DISCOVERY_URL
- VEILARBLOGIN_AAD_CLIENT_ID
- VEILARBLOGIN_AAD_LOGIN_URL
- UNLEASH_API_URL

Optional miljøvariabler:
- `APP_CONTEXT_PATH` - Om ikke satt benyttes `NAIS_APP_NAME` som context-path. 

Internal-endepunkter:

```
liveness: {APP_CONTEXT_PATH}/internal/isAlive 
readiness: {APP_CONTEXT_PATH}/internal/isReady 
prometheus: {APP_CONTEXT_PATH}/internal/prometheus
```  