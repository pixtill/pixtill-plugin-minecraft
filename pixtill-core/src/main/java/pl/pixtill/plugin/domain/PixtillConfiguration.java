package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.Optional;

public final class PixtillConfiguration {

    private final ApiUrl apiUrl;
    private final ApiKey apiKey;
    private final ServerUuid serverUuid;
    private final PollInterval pollInterval;
    private final boolean debug;

    public PixtillConfiguration(
            final ApiUrl apiUrl,
            final ApiKey apiKey,
            final ServerUuid serverUuid,
            final PollInterval pollInterval,
            final boolean debug) {
        if (apiUrl == null) {
            throw new InvalidValueException("configuration: apiUrl is required.");
        }
        if (pollInterval == null) {
            throw new InvalidValueException("configuration: pollInterval is required.");
        }
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.serverUuid = serverUuid;
        this.pollInterval = pollInterval;
        this.debug = debug;
    }

    public ApiUrl apiUrl() {
        return apiUrl;
    }

    public Optional<ApiKey> apiKey() {
        return Optional.ofNullable(apiKey);
    }

    public Optional<ServerUuid> serverUuid() {
        return Optional.ofNullable(serverUuid);
    }

    public PollInterval pollInterval() {
        return pollInterval;
    }

    public boolean debug() {
        return debug;
    }

    public boolean isConfigured() {
        return apiKey != null && serverUuid != null;
    }

    public ApiKey requireApiKey() {
        if (apiKey == null) {
            throw new InvalidValueException("apiKey is missing in configuration.");
        }
        return apiKey;
    }

    public ServerUuid requireServerUuid() {
        if (serverUuid == null) {
            throw new InvalidValueException("serverUuid is missing in configuration.");
        }
        return serverUuid;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PixtillConfiguration)) {
            return false;
        }
        final PixtillConfiguration that = (PixtillConfiguration) other;
        return debug == that.debug
                && apiUrl.equals(that.apiUrl)
                && Objects.equals(apiKey, that.apiKey)
                && Objects.equals(serverUuid, that.serverUuid)
                && pollInterval.equals(that.pollInterval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiUrl, apiKey, serverUuid, pollInterval, debug);
    }

    @Override
    public String toString() {
        return "PixtillConfiguration{apiUrl=" + apiUrl
                + ", apiKey=" + (apiKey == null ? "<none>" : apiKey.masked())
                + ", serverUuid=" + (serverUuid == null ? "<none>" : serverUuid)
                + ", pollInterval=" + pollInterval
                + ", debug=" + debug + "}";
    }
}
