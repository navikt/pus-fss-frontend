# gjør det mulig å bytte base-image slik at vi får bygd både innenfor og utenfor NAV
ARG BASE_IMAGE_PREFIX=""
FROM ${BASE_IMAGE_PREFIX}maven as maven-builder
ADD / /source
WORKDIR /source
RUN mvn install -DskipTests

FROM docker.adeo.no:5000/pus/nais-java-app
COPY --from=maven-builder /source/target/fss-frontend /app