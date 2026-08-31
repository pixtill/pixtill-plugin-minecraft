package pl.pixtill.plugin.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public final class ApiUrl {

    private final String value;

    private ApiUrl(final String value) {
        this.value = value;
    }

    public static ApiUrl of(final String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidValueException("apiUrl must not be empty.");
        }

        final String trimmed = raw.trim();
        final URL parsed;
        try {
            parsed = new URL(trimmed);
        } catch (final MalformedURLException exception) {
            throw new InvalidValueException("apiUrl is not a valid URL: " + trimmed);
        }

        final String protocol = parsed.getProtocol();
        if (!"http".equals(protocol) && !"https".equals(protocol)) {
            throw new InvalidValueException("apiUrl must use http or https: " + trimmed);
        }

        return new ApiUrl(stripTrailingSlash(trimmed));
    }

    private static String stripTrailingSlash(final String url) {
        String result = url;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public String value() {
        return value;
    }

    public boolean isSecure() {
        return value.regionMatches(true, 0, "https://", 0, "https://".length());
    }

    public boolean isLoopback() {
        final String lower = value.toLowerCase();
        return lower.contains("://localhost") || lower.contains("://127.") || lower.contains("://[::1]");
    }

    public String resolve(final String path) {
        if (path == null || path.isEmpty()) {
            return value;
        }
        return path.startsWith("/") ? value + path : value + "/" + path;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ApiUrl)) {
            return false;
        }
        return value.equals(((ApiUrl) other).value);
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
