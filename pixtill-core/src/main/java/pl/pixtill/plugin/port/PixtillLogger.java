package pl.pixtill.plugin.port;

public interface PixtillLogger {

    void info(String message);

    void error(String message);

    void debug(String message);

    void setDebugEnabled(boolean enabled);
}
