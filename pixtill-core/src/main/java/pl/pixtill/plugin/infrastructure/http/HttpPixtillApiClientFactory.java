package pl.pixtill.plugin.infrastructure.http;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import pl.pixtill.plugin.domain.PixtillConfiguration;
import pl.pixtill.plugin.port.PixtillApiClient;
import pl.pixtill.plugin.port.PixtillApiClientFactory;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;

public final class HttpPixtillApiClientFactory implements PixtillApiClientFactory {

    private final PixtillLogger logger;

    public HttpPixtillApiClientFactory(final PixtillLogger logger) {
        this.logger = logger;
    }

    @Override
    public PixtillApiClient create(final PixtillConfiguration config, final PlatformDataProvider platform) {
        final OkHttpClient http = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(15))
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(15))
                .build();

        final Map<String, String> telemetry = new LinkedHashMap<>();
        telemetry.put("X-Plugin-Version", platform.pluginVersion());
        telemetry.put("X-Engine", platform.engineName());
        telemetry.put("X-Engine-Version", platform.engineVersion().value());

        return new HttpPixtillApiClient(
                http, config.apiUrl(), config.requireApiKey(), telemetry, logger, config.debug());
    }
}
