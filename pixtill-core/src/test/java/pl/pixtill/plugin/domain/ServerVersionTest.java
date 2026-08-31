package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ServerVersionTest {

    @Test
    void parsesLegacySemverFromNoisyString() {
        assertThat(ServerVersion.parse("git-Paper-1.8.8-R0.1-SNAPSHOT").value()).isEqualTo("1.8.8");
        assertThat(ServerVersion.parse("1.21.4").value()).isEqualTo("1.21.4");
    }

    @Test
    void parsesNewCalverScheme() {
        assertThat(ServerVersion.parse("26.2").value()).isEqualTo("26.2");
        assertThat(ServerVersion.parse("Paper 26.10 (build 42)").value()).isEqualTo("26.10");
    }

    @Test
    void unknownForEmptyOrGarbage() {
        assertThat(ServerVersion.parse("").value()).isEqualTo(ServerVersion.UNKNOWN);
        assertThat(ServerVersion.parse(null).value()).isEqualTo(ServerVersion.UNKNOWN);
        assertThat(ServerVersion.parse("no-digits-here").isKnown()).isFalse();
    }

    @Test
    void knownWhenParsed() {
        assertThat(ServerVersion.parse("26.2").isKnown()).isTrue();
    }
}
