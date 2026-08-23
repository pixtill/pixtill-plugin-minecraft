package pl.pixtill.plugin.domain;

import java.util.Objects;

public final class ApiKey {

    private static final int VISIBLE_PREFIX = 4;

    private final String value;

    private ApiKey(final String value) {
        this.value = value;
    }

    public static ApiKey of(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("apiKey must not be empty.");
        }
        return new ApiKey(raw.trim());
    }

    public String value() {
        return value;
    }

    public String masked() {
        if (value.length() <= VISIBLE_PREFIX) {
            return "••••";
        }
        return value.substring(0, VISIBLE_PREFIX) + "••••";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ApiKey)) {
            return false;
        }
        return value.equals(((ApiKey) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return masked();
    }
}
