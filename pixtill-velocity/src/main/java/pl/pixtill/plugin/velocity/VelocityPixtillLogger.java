package pl.pixtill.plugin.velocity;

import org.slf4j.Logger;
import pl.pixtill.plugin.port.PixtillLogger;

public final class VelocityPixtillLogger implements PixtillLogger {

    private final Logger logger;
    private boolean debugEnabled;

    public VelocityPixtillLogger(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final String message) {
        logger.info(message);
    }

    @Override
    public void error(final String message) {
        logger.error(message);
    }

    @Override
    public void debug(final String message) {
        if (debugEnabled) {
            logger.info("[DEBUG] " + message);
        }
    }

    @Override
    public void setDebugEnabled(final boolean enabled) {
        this.debugEnabled = enabled;
    }
}
