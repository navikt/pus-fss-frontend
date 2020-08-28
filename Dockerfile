FROM docker.pkg.github.com/navikt/pus-nais-java-app/pus-nais-java-app:java11
COPY /target/pus-fss-frontend.jar app.jar