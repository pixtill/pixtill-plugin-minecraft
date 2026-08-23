package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QueuedCommandContractTest {

    private static final CommandId ID = CommandId.of("33333333-3333-3333-3333-333333333333");
    private static final ConsoleCommand CMD = ConsoleCommand.of("say hi");
    private static final PlayerName NOTCH = PlayerName.of("Notch");

    @Test
    void equalityConsidersEveryField() {
        final QueuedCommand base = new QueuedCommand(ID, CMD, NOTCH, true);

        assertThat(base).isEqualTo(new QueuedCommand(ID, CMD, NOTCH, true));
        assertThat(base.hashCode()).isEqualTo(new QueuedCommand(ID, CMD, NOTCH, true).hashCode());

        assertThat(base).isNotEqualTo(new QueuedCommand(ID, ConsoleCommand.of("say bye"), NOTCH, true));
        assertThat(base).isNotEqualTo(new QueuedCommand(ID, CMD, PlayerName.of("Herobrine"), true));
        assertThat(base).isNotEqualTo(new QueuedCommand(ID, CMD, NOTCH, false));
        assertThat(base).isNotEqualTo(new QueuedCommand(ID, CMD, null, true));
    }

    @Test
    void toStringIncludesIdAndDashForMissingPlayer() {
        final QueuedCommand withoutPlayer = new QueuedCommand(ID, CMD, null, false);

        assertThat(withoutPlayer.toString()).contains(ID.value());
        assertThat(withoutPlayer.toString()).contains("player=-");
    }
}
