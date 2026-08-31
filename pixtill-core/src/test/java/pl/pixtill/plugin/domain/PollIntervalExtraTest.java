package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class PollIntervalExtraTest {

    @Test
    void parsesHours() {
        assertThat(PollInterval.parse("2h").value()).isEqualTo(Duration.ofHours(2));
    }

    @Test
    void parsesHoursMinutesSeconds() {
        assertThat(PollInterval.parse("1h30m15s").value())
                .isEqualTo(Duration.ofHours(1).plusMinutes(30).plusSeconds(15));
    }

    @Test
    void rejectsNullDuration() {
        assertThatThrownBy(() -> PollInterval.of(null)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsZeroValuedText() {
        assertThatThrownBy(() -> PollInterval.parse("0s")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsIncompleteIso8601() {
        assertThatThrownBy(() -> PollInterval.parse("PT")).isInstanceOf(InvalidValueException.class);
    }
}
