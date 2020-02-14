# gjør det mulig å bytte base-image slik at vi får bygd både innenfor og utenfor NAV
FROM maven as maven-builder
ADD / /source
WORKDIR /source
RUN mvn install -DskipTests

FROM navikt/pus-nais-java-app
COPY --from=maven-builder /source/target/pus-fss-frontend /app