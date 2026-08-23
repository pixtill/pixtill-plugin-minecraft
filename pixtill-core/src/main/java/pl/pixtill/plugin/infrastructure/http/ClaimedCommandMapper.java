package pl.pixtill.plugin.infrastructure.http;

import pl.pixtill.plugin.domain.CommandId;
import pl.pixtill.plugin.domain.ConsoleCommand;
import pl.pixtill.plugin.domain.PlayerName;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.infrastructure.http.dto.ClaimedCommandDto;

public final class ClaimedCommandMapper {

    public QueuedCommand toDomain(final ClaimedCommandDto dto) {
        final PlayerName player = isBlank(dto.getPlayerNickname()) ? null : PlayerName.of(dto.getPlayerNickname());
        final boolean requiresPlayerOnline = player != null;
        return new QueuedCommand(
                CommandId.of(dto.getUuid()),
                ConsoleCommand.of(dto.getCommand()),
                player,
                requiresPlayerOnline);
    }

    private static boolean isBlank(final String value) {
        return value == null || value.trim().isEmpty();
    }
}
