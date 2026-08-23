package pl.pixtill.plugin.port;

import pl.pixtill.plugin.domain.PixtillConfiguration;

public interface ConfigProvider {

    PixtillConfiguration load();
}
