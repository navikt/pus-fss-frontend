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

## Konfigurasjon

pus-fss-frontend sjekker om det finnes en `/config.yaml` så sant ikke noe annet er spesifisert 
via miljøvariablen `CONFIGURATION_LOCATION`. 
Hvis den ikke finner en konfigurasjonsfil eller noen av feltene er udefinerte, så vil pus-fss-frontend bruke en predefinert konfigurasjon.   
Anbefalingen er derfor å bruke minimalistisk konfigurasjon.

pus-fss-frontend er satt opp med interpolasjon av miljøvariabler, via `{{ ENV_VAR }}` syntaksen. 
Dette gjøres av applikasjoner, og krever derfor at miljøvariablene er tilgjengelig ved kjøretid.

- [Komplett eksempel på konfig kan ses her](https://github.com/navikt/pus-fss-frontend/blob/master/fss-frontend.example.yaml).
- [Minimalt eksempel som skrur på CSP](https://github.com/navikt/pus-fss-frontend/blob/master/fss-frontend-minimal.example.yaml)


For å bruke via docker trenger man bare å legge til følgende; 
```ADD config.yaml /config.yaml```
