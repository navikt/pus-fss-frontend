package no.nav.fssfrontend.config;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.pus.decorator.proxy.BackendProxyConfig;

import javax.validation.Valid;
import java.util.List;

@Data
@Accessors(chain = true)
public class Config {

    @Valid
    public List<BackendProxyConfig> proxy;

}
