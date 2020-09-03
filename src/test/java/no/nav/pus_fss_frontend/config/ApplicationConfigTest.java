package no.nav.pus_fss_frontend.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigTest {

    @Test
    void getContextPath_shouldReturnEmpty_whenNaisAppNameAndContextPathIsNull() {
        EnvironmentProperties environmentProperties = new EnvironmentProperties();
        String contextPath = ApplicationConfig.getContextPath(environmentProperties);
        assertEquals("", contextPath);
    }

    @Test
    void getContextPath_shouldReturnEmpty_whenNaisAppNameIsEmpty() {
        EnvironmentProperties environmentProperties = new EnvironmentProperties();
        environmentProperties.setNaisAppName("");

        String contextPath = ApplicationConfig.getContextPath(environmentProperties);
        assertEquals("", contextPath);
    }

    @Test
    void getContextPath_shouldReturnNaisAppName_whenNoContextPathIsSet() {
        EnvironmentProperties environmentProperties = new EnvironmentProperties();
        environmentProperties.setNaisAppName("/pus-fss-frontend");

        String contextPath = ApplicationConfig.getContextPath(environmentProperties);
        assertEquals("/pus-fss-frontend", contextPath);
    }

    @Test
    void getContextPath_shouldReturnContextPath_whenOnlyContextPathIsSet() {
        EnvironmentProperties environmentProperties = new EnvironmentProperties();
        environmentProperties.setContextPath("annen/context-path");

        String contextPath = ApplicationConfig.getContextPath(environmentProperties);
        assertEquals("/annen/context-path", contextPath);
    }

    @Test
    void getContextPath_shouldReturnContextPath_whenBothNaisAppNameAndContextPathIsSet() {
        EnvironmentProperties environmentProperties = new EnvironmentProperties();
        environmentProperties.setContextPath("/annen-context/path");
        environmentProperties.setNaisAppName("/pus-fss-frontend");

        String contextPath = ApplicationConfig.getContextPath(environmentProperties);
        assertEquals("/annen-context/path", contextPath);
    }
}