package pl.pixtill.plugin.infrastructure.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JavaUtilLoggingLoggerTest {

    private final List<LogRecord> records = new ArrayList<>();
    private JavaUtilLoggingLogger logger;

    @BeforeEach
    void setUp() {
        final Logger jul = Logger.getAnonymousLogger();
        jul.setUseParentHandlers(false);
        jul.setLevel(Level.ALL);
        jul.addHandler(new Handler() {
            @Override
            public void publish(final LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
        logger = new JavaUtilLoggingLogger(jul);
    }

    @Test
    void mapsInfoAndErrorToJulLevels() {
        logger.info("hello");
        logger.error("boom");

        assertThat(records).hasSize(2);
        assertThat(records.get(0).getLevel()).isEqualTo(Level.INFO);
        assertThat(records.get(0).getMessage()).isEqualTo("hello");
        assertThat(records.get(1).getLevel()).isEqualTo(Level.SEVERE);
        assertThat(records.get(1).getMessage()).isEqualTo("boom");
    }

    @Test
    void suppressesDebugUntilEnabled() {
        logger.debug("hidden");
        assertThat(records).isEmpty();

        logger.setDebugEnabled(true);
        logger.debug("shown");

        assertThat(records).hasSize(1);
        assertThat(records.get(0).getMessage()).isEqualTo("[DEBUG] shown");
    }
}
