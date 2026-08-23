package pl.pixtill.plugin.infrastructure.config;

import pl.pixtill.plugin.domain.ApiKey;
import pl.pixtill.plugin.domain.ApiUrl;
import pl.pixtill.plugin.domain.PixtillConfiguration;
import pl.pixtill.plugin.domain.PollInterval;
import pl.pixtill.plugin.domain.ServerUuid;

public final class PixtillConfigurationMapper {

    public PixtillConfiguration map(
            final String apiUrl,
            final String apiKey,
            final String serverUuid,
            final String pollInterval,
            final boolean debug) {
        return new PixtillConfiguration(
                ApiUrl.of(apiUrl),
                isBlank(apiKey) ? null : ApiKey.of(apiKey),
                isBlank(serverUuid) ? null : ServerUuid.of(serverUuid),
                PollInterval.parse(pollInterval),
                debug);
    }

    private static boolean isBlank(final String value) {
        return value == null || value.trim().isEmpty();
    }
}
