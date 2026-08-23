package pl.pixtill.plugin.port;

import pl.pixtill.plugin.domain.PixtillConfiguration;

public interface PixtillApiClientFactory {

    PixtillApiClient create(PixtillConfiguration config, PlatformDataProvider platform);
}
