package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public final class PlayerIdentifier {

    private static final Pattern VALID = Pattern.compile("[A-Za-z0-9_.:-]{3,100}");

    private final String value;

    private PlayerIdentifier(final String value) {
        this.value = value;
    }

    public static PlayerIdentifier of(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("player identifier must not be empty.");
        }
        final String trimmed = raw.trim();
        if (!VALID.matcher(trimmed).matches()) {
            throw new InvalidValueException("invalid player identifier: " + trimmed);
        }
        return new PlayerIdentifier(trimmed);
    }

    public String value() {
        return value;
    }

    public boolean equalsIgnoreCase(final PlayerIdentifier other) {
        return other != null && value.equalsIgnoreCase(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlayerIdentifier)) {
            return false;
        }
        return value.equals(((PlayerIdentifier) other).value);
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
