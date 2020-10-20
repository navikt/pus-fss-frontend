package no.nav.pus_fss_frontend.config.yaml;

public enum CspMode {
    ENFORCE("Content-Security-Policy"),
    REPORT_ONLY("Content-Security-Policy-Report-Only"),
    NONE(null);

    public final String header;

    CspMode(String header) {
        this.header = header;
    }
}
