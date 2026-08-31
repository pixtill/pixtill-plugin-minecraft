package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public final class PlayerName {

    private static final Pattern VALID = Pattern.compile("[A-Za-z0-9_]{3,16}");

    private final String value;

    private PlayerName(final String value) {
        this.value = value;
    }

    public static PlayerName of(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("player name must not be empty.");
        }
        final String trimmed = raw.trim();
        if (!VALID.matcher(trimmed).matches()) {
            throw new InvalidValueException("invalid player name: " + trimmed);
        }
        return new PlayerName(trimmed);
    }

    public String value() {
        return value;
    }

    public boolean equalsIgnoreCase(final PlayerName other) {
        return other != null && value.equalsIgnoreCase(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlayerName)) {
            return false;
        }
        return value.equals(((PlayerName) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
