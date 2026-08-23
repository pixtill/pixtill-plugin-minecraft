package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Nested
    class ServerUuidTests {
        @Test
        void acceptsValidUuid() {
            assertThat(ServerUuid.of("11111111-1111-1111-1111-111111111111").value())
                    .isEqualTo("11111111-1111-1111-1111-111111111111");
        }

        @Test
        void rejectsInvalid() {
            assertThatThrownBy(() -> ServerUuid.of("not-a-uuid")).isInstanceOf(InvalidValueException.class);
            assertThatThrownBy(() -> ServerUuid.of("")).isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class PlayerNameTests {
        @Test
        void acceptsValidNick() {
            assertThat(PlayerName.of("Notch_01").value()).isEqualTo("Notch_01");
        }

        @Test
        void caseInsensitiveComparison() {
            assertThat(PlayerName.of("Notch").equalsIgnoreCase(PlayerName.of("notch"))).isTrue();
        }

        @Test
        void rejectsTooShortTooLongOrIllegalChars() {
            assertThatThrownBy(() -> PlayerName.of("ab")).isInstanceOf(InvalidValueException.class);
            assertThatThrownBy(() -> PlayerName.of("ThisNameIsWayTooLong")).isInstanceOf(InvalidValueException.class);
            assertThatThrownBy(() -> PlayerName.of("bad name!")).isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class ConsoleCommandTests {
        @Test
        void stripsLeadingSlashes() {
            assertThat(ConsoleCommand.of("/give Notch diamond").value()).isEqualTo("give Notch diamond");
            assertThat(ConsoleCommand.of("//broadcast hi").value()).isEqualTo("broadcast hi");
        }

        @Test
        void rejectsEmptyOrOnlySlashes() {
            assertThatThrownBy(() -> ConsoleCommand.of("   ")).isInstanceOf(InvalidValueException.class);
            assertThatThrownBy(() -> ConsoleCommand.of("///")).isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class CommandIdTests {
        @Test
        void acceptsUuidStringAndValue() {
            assertThat(CommandId.of("22222222-2222-2222-2222-222222222222").value())
                    .isEqualTo("22222222-2222-2222-2222-222222222222");
        }

        @Test
        void rejectsInvalid() {
            assertThatThrownBy(() -> CommandId.of("x")).isInstanceOf(InvalidValueException.class);
        }
    }
}
