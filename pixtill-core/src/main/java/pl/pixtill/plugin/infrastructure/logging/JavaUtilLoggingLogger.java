package pl.pixtill.plugin.infrastructure.logging;

import java.util.logging.Level;
import java.util.logging.Logger;
import pl.pixtill.plugin.port.PixtillLogger;

public final class JavaUtilLoggingLogger implements PixtillLogger {

    private final Logger logger;
    private boolean debugEnabled;

    public JavaUtilLoggingLogger(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final String message) {
        logger.log(Level.INFO, message);
    }

    @Override
    public void error(final String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void debug(final String message) {
        if (debugEnabled) {
            logger.log(Level.INFO, "[DEBUG] " + message);
        }
    }

    @Override
    public void setDebugEnabled(final boolean enabled) {
        this.debugEnabled = enabled;
    }
}
