package pl.pixtill.plugin.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import pl.pixtill.plugin.domain.InvalidValueException;
import pl.pixtill.plugin.domain.PixtillConfiguration;

class PixtillConfigurationMapperTest {

    private final PixtillConfigurationMapper mapper = new PixtillConfigurationMapper();

    @Test
    void mapsFullyConfigured() {
        final PixtillConfiguration config = mapper.map(
                "https://api.pixtill.com/v1",
                "pk_live_abc",
                "11111111-1111-1111-1111-111111111111",
                "45s",
                true);

        assertThat(config.isConfigured()).isTrue();
        assertThat(config.apiUrl().value()).isEqualTo("https://api.pixtill.com/v1");
        assertThat(config.requireApiKey().value()).isEqualTo("pk_live_abc");
        assertThat(config.requireServerUuid().value()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(config.pollInterval().value()).isEqualTo(Duration.ofSeconds(45));
        assertThat(config.debug()).isTrue();
    }

    @Test
    void treatsBlankKeyAndUuidAsNotConfigured() {
        final PixtillConfiguration config = mapper.map(
                "https://api.pixtill.com/v1", "", "  ", "30s", false);

        assertThat(config.isConfigured()).isFalse();
        assertThat(config.apiKey()).isEmpty();
        assertThat(config.serverUuid()).isEmpty();
    }

    @Test
    void clampsIntervalBelowMinimum() {
        final PixtillConfiguration config = mapper.map(
                "https://api.pixtill.com/v1", "k", "11111111-1111-1111-1111-111111111111", "1s", false);

        assertThat(config.pollInterval().value()).isEqualTo(pl.pixtill.plugin.domain.PollInterval.MINIMUM);
    }

    @Test
    void rejectsInvalidUrl() {
        assertThatThrownBy(() -> mapper.map("nope", "", "", "30s", false))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsInvalidServerUuidWhenPresent() {
        assertThatThrownBy(() -> mapper.map("https://api.pixtill.com/v1", "k", "not-a-uuid", "30s", false))
                .isInstanceOf(InvalidValueException.class);
    }
}
