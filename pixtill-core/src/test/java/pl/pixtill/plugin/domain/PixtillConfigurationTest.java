package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PixtillConfigurationTest {

    private static final ApiUrl URL = ApiUrl.of("https://api.pixtill.com/v1");
    private static final PollInterval INTERVAL = PollInterval.parse("30s");
    private static final ServerUuid UUID = ServerUuid.of("11111111-1111-1111-1111-111111111111");

    @Test
    void configuredExposesRequiredValues() {
        final PixtillConfiguration config =
                new PixtillConfiguration(URL, ApiKey.of("k"), UUID, INTERVAL, false);

        assertThat(config.isConfigured()).isTrue();
        assertThat(config.apiKey()).isPresent();
        assertThat(config.serverUuid()).isPresent();
        assertThat(config.requireApiKey().value()).isEqualTo("k");
        assertThat(config.requireServerUuid()).isEqualTo(UUID);
    }

    @Test
    void missingApiKeyMakesItUnconfiguredAndRequireThrows() {
        final PixtillConfiguration config =
                new PixtillConfiguration(URL, null, UUID, INTERVAL, false);

        assertThat(config.isConfigured()).isFalse();
        assertThat(config.apiKey()).isEmpty();
        assertThatThrownBy(config::requireApiKey).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void missingServerUuidMakesItUnconfiguredAndRequireThrows() {
        final PixtillConfiguration config =
                new PixtillConfiguration(URL, ApiKey.of("k"), null, INTERVAL, false);

        assertThat(config.isConfigured()).isFalse();
        assertThat(config.serverUuid()).isEmpty();
        assertThatThrownBy(config::requireServerUuid).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void equalityConsidersEveryField() {
        final PixtillConfiguration base =
                new PixtillConfiguration(URL, ApiKey.of("k"), UUID, INTERVAL, false);

        assertThat(base).isEqualTo(new PixtillConfiguration(URL, ApiKey.of("k"), UUID, INTERVAL, false));
        assertThat(base.hashCode())
                .isEqualTo(new PixtillConfiguration(URL, ApiKey.of("k"), UUID, INTERVAL, false).hashCode());

        assertThat(base).isNotEqualTo(
                new PixtillConfiguration(ApiUrl.of("https://other.example.com"), ApiKey.of("k"), UUID, INTERVAL, false));
        assertThat(base).isNotEqualTo(
                new PixtillConfiguration(URL, ApiKey.of("other"), UUID, INTERVAL, false));
        assertThat(base).isNotEqualTo(
                new PixtillConfiguration(URL, ApiKey.of("k"), UUID, PollInterval.parse("2m"), false));
        assertThat(base).isNotEqualTo(
                new PixtillConfiguration(URL, ApiKey.of("k"), UUID, INTERVAL, true));
        assertThat(base).isNotEqualTo(null);
    }

    @Test
    void toStringShowsPlaceholdersWhenNotConfigured() {
        final PixtillConfiguration config =
                new PixtillConfiguration(URL, null, null, INTERVAL, false);

        assertThat(config.toString()).contains("<none>");
    }

    @Test
    void toStringNeverLeaksTheApiKey() {
        final PixtillConfiguration config =
                new PixtillConfiguration(URL, ApiKey.of("pk_live_supersecret"), UUID, INTERVAL, false);

        assertThat(config.toString()).doesNotContain("supersecret");
    }

    @Test
    void rejectsNullUrlOrInterval() {
        assertThatThrownBy(() -> new PixtillConfiguration(null, ApiKey.of("k"), UUID, INTERVAL, false))
                .isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new PixtillConfiguration(URL, ApiKey.of("k"), UUID, null, false))
                .isInstanceOf(InvalidValueException.class);
    }
}
