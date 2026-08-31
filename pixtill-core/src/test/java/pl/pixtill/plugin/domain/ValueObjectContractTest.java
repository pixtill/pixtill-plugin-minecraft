package pl.pixtill.plugin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValueObjectContractTest {

    private static void valueSemantics(final Object a, final Object aCopy, final Object different) {
        assertThat(a).isEqualTo(aCopy);
        assertThat(a.hashCode()).isEqualTo(aCopy.hashCode());
        assertThat(a).isNotEqualTo(different);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("some string of another type");
    }

    @Test
    void apiUrlHasValueSemantics() {
        valueSemantics(
                ApiUrl.of("https://api.pixtill.com/v1"),
                ApiUrl.of("https://api.pixtill.com/v1/"),
                ApiUrl.of("https://other.example.com"));
    }

    @Test
    void apiKeyHasValueSemantics() {
        valueSemantics(ApiKey.of("same"), ApiKey.of("same"), ApiKey.of("different"));
    }

    @Test
    void serverUuidHasValueSemantics() {
        final String uuid = "11111111-1111-1111-1111-111111111111";
        valueSemantics(ServerUuid.of(uuid), ServerUuid.of(uuid), ServerUuid.of(UUID.randomUUID().toString()));
    }

    @Test
    void commandIdHasValueSemantics() {
        final String uuid = "22222222-2222-2222-2222-222222222222";
        valueSemantics(CommandId.of(uuid), CommandId.of(uuid), CommandId.of(UUID.randomUUID().toString()));
    }

    @Test
    void playerNameHasValueSemantics() {
        valueSemantics(PlayerName.of("Notch"), PlayerName.of("Notch"), PlayerName.of("Herobrine"));
    }

    @Test
    void consoleCommandHasValueSemantics() {
        valueSemantics(ConsoleCommand.of("say hi"), ConsoleCommand.of("/say hi"), ConsoleCommand.of("say bye"));
    }

    @Test
    void pollIntervalHasValueSemantics() {
        valueSemantics(PollInterval.parse("1m"), PollInterval.parse("60s"), PollInterval.parse("2m"));
    }

    @Test
    void serverVersionHasValueSemantics() {
        valueSemantics(ServerVersion.parse("1.8.8"), ServerVersion.parse("git-1.8.8-x"), ServerVersion.parse("26.2"));
    }

    @Test
    void queuedCommandHasValueSemantics() {
        final CommandId id = CommandId.of("33333333-3333-3333-3333-333333333333");
        final ConsoleCommand cmd = ConsoleCommand.of("say hi");
        valueSemantics(
                new QueuedCommand(id, cmd, PlayerName.of("Notch"), true),
                new QueuedCommand(id, cmd, PlayerName.of("Notch"), true),
                new QueuedCommand(CommandId.of(UUID.randomUUID().toString()), cmd, PlayerName.of("Notch"), true));
    }

    @Test
    void playerNameCaseInsensitiveComparisonHandlesNull() {
        assertThat(PlayerName.of("Notch").equalsIgnoreCase(null)).isFalse();
    }

    @Test
    void apiUrlResolveWithoutPathReturnsBase() {
        final ApiUrl url = ApiUrl.of("https://api.pixtill.com/v1");
        assertThat(url.resolve("")).isEqualTo("https://api.pixtill.com/v1");
        assertThat(url.resolve(null)).isEqualTo("https://api.pixtill.com/v1");
    }
}
