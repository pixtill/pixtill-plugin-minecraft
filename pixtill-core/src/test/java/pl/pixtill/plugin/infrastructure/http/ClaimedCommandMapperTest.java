package pl.pixtill.plugin.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import pl.pixtill.plugin.domain.InvalidValueException;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.infrastructure.http.dto.ClaimedCommandDto;

class ClaimedCommandMapperTest {

    private final ClaimedCommandMapper mapper = new ClaimedCommandMapper();
    private final Gson gson = new Gson();

    private ClaimedCommandDto dto(final String json) {
        return gson.fromJson(json, ClaimedCommandDto.class);
    }

    @Test
    void mapsPlayerAndExplicitRequiresOnlineFlag() {
        final QueuedCommand command = mapper.toDomain(dto(
                "{\"uuid\":\"22222222-2222-2222-2222-222222222222\",\"command\":\"say hi\","
                        + "\"playerIdentifier\":\"Notch\",\"requiresOnlinePlayer\":true}"));

        assertThat(command.requiresPlayerOnline()).isTrue();
        assertThat(command.player()).isPresent();
        assertThat(command.command().value()).isEqualTo("say hi");
    }

    @Test
    void honoursRequiresOnlinePlayerFalseEvenWithPlayer() {
        final QueuedCommand command = mapper.toDomain(dto(
                "{\"uuid\":\"22222222-2222-2222-2222-222222222222\",\"command\":\"broadcast hi\","
                        + "\"playerIdentifier\":\"Notch\",\"requiresOnlinePlayer\":false}"));

        assertThat(command.requiresPlayerOnline()).isFalse();
        assertThat(command.player()).isPresent();
    }

    @Test
    void defaultsToRequiringOnlineWhenFlagAbsent() {
        final QueuedCommand command = mapper.toDomain(dto(
                "{\"uuid\":\"22222222-2222-2222-2222-222222222222\",\"command\":\"say hi\",\"playerIdentifier\":\"Notch\"}"));

        assertThat(command.requiresPlayerOnline()).isTrue();
    }

    @Test
    void mapsCommandWithoutPlayer() {
        final QueuedCommand command = mapper.toDomain(dto(
                "{\"uuid\":\"22222222-2222-2222-2222-222222222222\",\"command\":\"broadcast hi\","
                        + "\"playerIdentifier\":null,\"requiresOnlinePlayer\":false}"));

        assertThat(command.player()).isEmpty();
    }

    @Test
    void rejectsMalformedUuid() {
        assertThatThrownBy(() -> mapper.toDomain(dto("{\"uuid\":\"nope\",\"command\":\"say hi\"}")))
                .isInstanceOf(InvalidValueException.class);
    }
}
