package pl.pixtill.plugin.domain;

import java.util.Objects;

public final class ConsoleCommand {

    private final String value;

    private ConsoleCommand(final String value) {
        this.value = value;
    }

    public static ConsoleCommand of(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("command must not be empty.");
        }
        String normalized = raw.trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1).trim();
        }
        if (normalized.isEmpty()) {
            throw new InvalidValueException("command must not consist only of '/'.");
        }
        for (int index = 0; index < normalized.length(); index++) {
            if (Character.isISOControl(normalized.charAt(index))) {
                throw new InvalidValueException("command must not contain control characters.");
            }
        }
        return new ConsoleCommand(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConsoleCommand)) {
            return false;
        }
        return value.equals(((ConsoleCommand) other).value);
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
