package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class QueuedCommandTest {

    private static final PlayerName NOTCH = PlayerName.of("Notch");

    private QueuedCommand command(final PlayerName player, final boolean requiresOnline) {
        return new QueuedCommand(
                CommandId.of(UUID.randomUUID()),
                ConsoleCommand.of("say hi"),
                player,
                requiresOnline);
    }

    @Test
    void blockedWhenRequiredPlayerOffline() {
        final QueuedCommand cmd = command(NOTCH, true);
        assertThat(cmd.isBlockedBy(player -> false)).isTrue();
    }

    @Test
    void notBlockedWhenRequiredPlayerOnline() {
        final QueuedCommand cmd = command(NOTCH, true);
        assertThat(cmd.isBlockedBy(player -> true)).isFalse();
    }

    @Test
    void notBlockedWhenDoesNotRequireOnline() {
        final QueuedCommand cmd = command(NOTCH, false);
        assertThat(cmd.isBlockedBy(player -> false)).isFalse();
    }

    @Test
    void notBlockedWhenNoPlayerEvenIfRequiresOnline() {
        final QueuedCommand cmd = command(null, true);
        assertThat(cmd.isBlockedBy(player -> false)).isFalse();
        assertThat(cmd.player()).isEmpty();
    }

    @Test
    void rejectsNullIdOrCommand() {
        assertThatThrownBy(() -> new QueuedCommand(null, ConsoleCommand.of("x"), NOTCH, true))
                .isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new QueuedCommand(CommandId.of(UUID.randomUUID()), null, NOTCH, true))
                .isInstanceOf(InvalidValueException.class);
    }
}
