package pl.pixtill.plugin.port;

public class ApiClientException extends RuntimeException {

    public ApiClientException(final String message) {
        super(message);
    }

    public ApiClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
