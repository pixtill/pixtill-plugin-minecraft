package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.UUID;

public final class CommandId {

    private final UUID value;

    private CommandId(final UUID value) {
        this.value = value;
    }

    public static CommandId of(final UUID value) {
        if (value == null) {
            throw new InvalidValueException("command id must not be null.");
        }
        return new CommandId(value);
    }

    public static CommandId of(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("command id must not be empty.");
        }
        try {
            return new CommandId(UUID.fromString(raw.trim()));
        } catch (final IllegalArgumentException exception) {
            throw new InvalidValueException("command id is not a valid UUID: " + raw.trim());
        }
    }

    public String value() {
        return value.toString();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CommandId)) {
            return false;
        }
        return value.equals(((CommandId) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
