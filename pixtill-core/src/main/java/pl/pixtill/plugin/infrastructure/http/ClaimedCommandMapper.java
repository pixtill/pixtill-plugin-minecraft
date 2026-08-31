package pl.pixtill.plugin.infrastructure.http;

import pl.pixtill.plugin.domain.CommandId;
import pl.pixtill.plugin.domain.ConsoleCommand;
import pl.pixtill.plugin.domain.PlayerIdentifier;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.infrastructure.http.dto.ClaimedCommandDto;

public final class ClaimedCommandMapper {

    public QueuedCommand toDomain(final ClaimedCommandDto dto) {
        final PlayerIdentifier player = isBlank(dto.getPlayerIdentifier()) ? null : PlayerIdentifier.of(dto.getPlayerIdentifier());
        return new QueuedCommand(
                CommandId.of(dto.getUuid()),
                ConsoleCommand.of(dto.getCommand()),
                player,
                dto.requiresOnlinePlayer());
    }

    private static boolean isBlank(final String value) {
        return value == null || value.trim().isEmpty();
    }
}
