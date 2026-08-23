package pl.pixtill.plugin.domain;

public final class InvalidValueException extends RuntimeException {

    public InvalidValueException(final String message) {
        super(message);
    }
}
