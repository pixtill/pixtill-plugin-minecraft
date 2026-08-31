package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.Optional;

public final class QueuedCommand {

    private final CommandId id;
    private final ConsoleCommand command;
    private final PlayerName player;
    private final boolean requiresPlayerOnline;

    public QueuedCommand(
            final CommandId id,
            final ConsoleCommand command,
            final PlayerName player,
            final boolean requiresPlayerOnline) {
        if (id == null) {
            throw new InvalidValueException("QueuedCommand: id must not be null.");
        }
        if (command == null) {
            throw new InvalidValueException("QueuedCommand: command must not be null.");
        }
        this.id = id;
        this.command = command;
        this.player = player;
        this.requiresPlayerOnline = requiresPlayerOnline;
    }

    public CommandId id() {
        return id;
    }

    public ConsoleCommand command() {
        return command;
    }

    public Optional<PlayerName> player() {
        return Optional.ofNullable(player);
    }

    public boolean requiresPlayerOnline() {
        return requiresPlayerOnline;
    }

    public boolean isBlockedBy(final PlayerPresence presence) {
        if (!requiresPlayerOnline || player == null) {
            return false;
        }
        return !presence.isOnline(player);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof QueuedCommand)) {
            return false;
        }
        final QueuedCommand that = (QueuedCommand) other;
        return requiresPlayerOnline == that.requiresPlayerOnline
                && id.equals(that.id)
                && command.equals(that.command)
                && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, command, player, requiresPlayerOnline);
    }

    @Override
    public String toString() {
        return "QueuedCommand{id=" + id
                + ", player=" + (player == null ? "-" : player)
                + ", requiresOnline=" + requiresPlayerOnline
                + ", command='" + command + "'}";
    }

    public interface PlayerPresence {
        boolean isOnline(PlayerName player);
    }
}
