package pl.pixtill.plugin.domain;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerVersion {

    public static final String UNKNOWN = "Unknown";

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)+)");

    private final String value;

    private ServerVersion(final String value) {
        this.value = value;
    }

    public static ServerVersion parse(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return new ServerVersion(UNKNOWN);
        }
        final Matcher matcher = VERSION_PATTERN.matcher(raw);
        return new ServerVersion(matcher.find() ? matcher.group(1) : UNKNOWN);
    }

    public String value() {
        return value;
    }

    public boolean isKnown() {
        return !UNKNOWN.equals(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ServerVersion)) {
            return false;
        }
        return value.equals(((ServerVersion) other).value);
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
