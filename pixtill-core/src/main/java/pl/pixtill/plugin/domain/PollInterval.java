package pl.pixtill.plugin.domain;

import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PollInterval {

    public static final Duration MINIMUM = Duration.ofSeconds(5);

    private static final Pattern HUMAN =
            Pattern.compile("(?i)\\s*(?:(\\d+)\\s*h)?\\s*(?:(\\d+)\\s*m)?\\s*(?:(\\d+)\\s*s)?\\s*");

    private final Duration value;

    private PollInterval(final Duration value) {
        this.value = value;
    }

    public static PollInterval of(final Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new InvalidValueException("pollInterval must be positive.");
        }
        return new PollInterval(duration.compareTo(MINIMUM) < 0 ? MINIMUM : duration);
    }

    public static PollInterval parse(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("pollInterval must not be empty.");
        }

        final String trimmed = raw.trim();
        if (trimmed.regionMatches(true, 0, "P", 0, 1)) {
            try {
                return of(Duration.parse(trimmed));
            } catch (final Exception exception) {
                throw new InvalidValueException("pollInterval is invalid: " + trimmed);
            }
        }

        final Matcher matcher = HUMAN.matcher(trimmed);
        if (!matcher.matches() || (matcher.group(1) == null && matcher.group(2) == null && matcher.group(3) == null)) {
            throw new InvalidValueException(
                    "pollInterval is invalid: " + trimmed + " (use e.g. 30s, 1m, 2m30s).");
        }

        final long hours = parseGroup(matcher.group(1));
        final long minutes = parseGroup(matcher.group(2));
        final long seconds = parseGroup(matcher.group(3));
        return of(Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds));
    }

    private static long parseGroup(final String group) {
        return group == null ? 0L : Long.parseLong(group);
    }

    public Duration value() {
        return value;
    }

    public long toSeconds() {
        return value.getSeconds();
    }

    public long toMillis() {
        return value.toMillis();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PollInterval)) {
            return false;
        }
        return value.equals(((PollInterval) other).value);
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
