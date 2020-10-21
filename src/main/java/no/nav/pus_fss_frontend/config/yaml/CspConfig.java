package no.nav.pus_fss_frontend.config.yaml;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class CspConfig {
    public CspMode mode = CspMode.NONE;
    public String defaultSrc = "'self'";
    public String scriptSrc;
    public String imgSrc;
    public String styleSrc;
    public String fontSrc;
    public String connectSrc;
    public String frameSrc;
    public String reportUri = "/frontendlogger/api/warn";


    public void applyTo(Map<String, String> headers) {
        if (mode.header != null) {
            headers.put(mode.header, generateCspHeader());
        }
    }

    String generateCspHeader() {
        StringBuilder builder = new StringBuilder();
        append(builder, "default-src", defaultSrc);
        append(builder, "script-src", scriptSrc);
        append(builder, "img-src", imgSrc);
        append(builder, "style-src", styleSrc);
        append(builder, "font-src", fontSrc);
        append(builder, "connect-src", connectSrc);
        append(builder, "frame-src", frameSrc);
        append(builder, "report-uri", reportUri);
        return builder.toString();
    }

    private static void append(StringBuilder builder, String directive, String value) {
        if (value != null) {
            builder.append(" " + directive + " " + value + ";");
        }
    }
}
