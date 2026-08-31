package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.Optional;

public final class CommandResult {

    private final CommandId id;
    private final CommandStatus status;
    private final String error;

    private CommandResult(final CommandId id, final CommandStatus status, final String error) {
        if (id == null) {
            throw new InvalidValueException("CommandResult: id must not be null.");
        }
        if (status == null) {
            throw new InvalidValueException("CommandResult: status must not be null.");
        }
        this.id = id;
        this.status = status;
        this.error = error;
    }

    public static CommandResult completed(final CommandId id) {
        return new CommandResult(id, CommandStatus.COMPLETED, null);
    }

    public static CommandResult failed(final CommandId id, final String error) {
        return new CommandResult(id, CommandStatus.FAILED, error);
    }

    public static CommandResult deferred(final CommandId id) {
        return new CommandResult(id, CommandStatus.DEFERRED, null);
    }

    public CommandId id() {
        return id;
    }

    public CommandStatus status() {
        return status;
    }

    public Optional<String> error() {
        return Optional.ofNullable(error);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CommandResult)) {
            return false;
        }
        final CommandResult that = (CommandResult) other;
        return id.equals(that.id) && status == that.status && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, error);
    }

    @Override
    public String toString() {
        return "CommandResult{id=" + id + ", status=" + status + ", error=" + error + "}";
    }
}
