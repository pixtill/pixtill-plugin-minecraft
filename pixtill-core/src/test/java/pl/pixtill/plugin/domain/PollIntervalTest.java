package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class PollIntervalTest {

    @Test
    void parsesSeconds() {
        assertThat(PollInterval.parse("30s").value()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void parsesMinutes() {
        assertThat(PollInterval.parse("2m").value()).isEqualTo(Duration.ofMinutes(2));
    }

    @Test
    void parsesCombined() {
        assertThat(PollInterval.parse("1m30s").value()).isEqualTo(Duration.ofSeconds(90));
    }

    @Test
    void parsesIso8601() {
        assertThat(PollInterval.parse("PT1M").value()).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    void clampsBelowMinimumUpToMinimum() {
        assertThat(PollInterval.parse("1s").value()).isEqualTo(PollInterval.MINIMUM);
        assertThat(PollInterval.of(Duration.ofSeconds(2)).value()).isEqualTo(PollInterval.MINIMUM);
    }

    @Test
    void rejectsZeroOrNegative() {
        assertThatThrownBy(() -> PollInterval.of(Duration.ZERO)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> PollInterval.of(Duration.ofSeconds(-5))).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsEmptyOrGarbage() {
        assertThatThrownBy(() -> PollInterval.parse("")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> PollInterval.parse("soon")).isInstanceOf(InvalidValueException.class);
    }
}
