package no.nav.pus_fss_frontend.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void ensureStringIsPrefixed() {
        String prefixedContextPath1 = Utils.ensureStringIsPrefixed("context/path", "/");
        String prefixedContextPath2 = Utils.ensureStringIsPrefixed("/context/path", "/");
        String prefixedContextPath3 = Utils.ensureStringIsPrefixed("context-path", "/");
        String prefixedContextPath4 = Utils.ensureStringIsPrefixed("/context-path", "/");
        assertEquals("/context/path", prefixedContextPath1);
        assertEquals("/context/path", prefixedContextPath2);
        assertEquals("/context-path", prefixedContextPath3);
        assertEquals("/context-path", prefixedContextPath4);
    }
}