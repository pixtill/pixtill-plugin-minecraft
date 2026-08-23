package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.UUID;

public final class ServerUuid {

    private final UUID value;

    private ServerUuid(final UUID value) {
        this.value = value;
    }

    public static ServerUuid of(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("serverUuid must not be empty.");
        }
        try {
            return new ServerUuid(UUID.fromString(raw.trim()));
        } catch (final IllegalArgumentException exception) {
            throw new InvalidValueException("serverUuid is not a valid UUID: " + raw.trim());
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
        if (!(other instanceof ServerUuid)) {
            return false;
        }
        return value.equals(((ServerUuid) other).value);
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
