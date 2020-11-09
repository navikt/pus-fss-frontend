package no.nav.pus_fss_frontend.config.yaml;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class CspConfig {
    public CspMode mode = CspMode.NONE;
    public String defaultSrc = "'self'";
    public String childSrc;
    public String connectSrc;
    public String fontSrc;
    public String frameSrc;
    public String imgSrc;
    public String manifestSrc;
    public String mediaSrc;
    public String objectSrc;
    public String prefetchSrc;
    public String scriptSrc;
    public String scriptSrcElem;
    public String scriptSrcAttr;
    public String styleSrc;
    public String styleSrcElem;
    public String styleSrcAttr;
    public String workerSrc;
    public String reportUri = "/frontendlogger/api/warn";


    public void applyTo(Map<String, String> headers) {
        if (mode.header != null) {
            headers.put(mode.header, generateCspHeader());
        }
    }

    String generateCspHeader() {
        StringBuilder builder = new StringBuilder();
        append(builder, "default-src", defaultSrc);
        append(builder, "child-src", childSrc);
        append(builder, "connect-src", connectSrc);
        append(builder, "font-src", fontSrc);
        append(builder, "frame-src", frameSrc);
        append(builder, "img-src", imgSrc);
        append(builder, "manifest-src", manifestSrc);
        append(builder, "media-src", mediaSrc);
        append(builder, "object-src", objectSrc);
        append(builder, "prefetch-src", prefetchSrc);
        append(builder, "script-src", scriptSrc);
        append(builder, "script-src-elem", scriptSrcElem);
        append(builder, "script-src-attr", scriptSrcAttr);
        append(builder, "style-src", styleSrc);
        append(builder, "style-src-elem", styleSrcElem);
        append(builder, "style-src-attr", styleSrcAttr);
        append(builder, "worker-src", workerSrc);
        append(builder, "report-uri", reportUri);
        return builder.toString();
    }

    private static void append(StringBuilder builder, String directive, String value) {
        if (value != null) {
            builder.append(" " + directive + " " + value + ";");
        }
    }
}
