package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ConsoleCommandSecurityTest {

    @Test
    void rejectsNewlineToPreventCommandInjection() {
        final String withNewline = "say hi" + (char) 10 + "op attacker";
        assertThatThrownBy(() -> ConsoleCommand.of(withNewline)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsCarriageReturnCharacter() {
        final String withCarriageReturn = "say hi" + (char) 13 + "op attacker";
        assertThatThrownBy(() -> ConsoleCommand.of(withCarriageReturn)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void rejectsNullCharacter() {
        final String withNull = "say hi" + (char) 0 + "op";
        assertThatThrownBy(() -> ConsoleCommand.of(withNull)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void acceptsNormalCommand() {
        assertThat(ConsoleCommand.of("give Notch diamond 1").value()).isEqualTo("give Notch diamond 1");
    }
}
