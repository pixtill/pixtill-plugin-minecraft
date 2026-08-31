package pl.pixtill.plugin.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.pixtill.plugin.domain.PixtillConfiguration;

class YamlConfigProviderExtraTest {

    @Test
    void fallsBackToDefaultsWhenFileIsNotAMap(@TempDir final Path dir) throws IOException {
        Files.write(dir.resolve("config.yml"), "just a scalar".getBytes(StandardCharsets.UTF_8));

        final PixtillConfiguration config = new YamlConfigProvider(dir).load();

        assertThat(config.isConfigured()).isFalse();
        assertThat(config.apiUrl().value()).isEqualTo("https://api.pixtill.com/v1");
        assertThat(config.pollInterval().value().getSeconds()).isEqualTo(30);
    }

    @Test
    void usesDefaultsForMissingKeys(@TempDir final Path dir) throws IOException {
        Files.write(dir.resolve("config.yml"),
                "apiKey: \"only-key\"\n".getBytes(StandardCharsets.UTF_8));

        final PixtillConfiguration config = new YamlConfigProvider(dir).load();

        assertThat(config.apiUrl().value()).isEqualTo("https://api.pixtill.com/v1");
        assertThat(config.pollInterval().value().getSeconds()).isEqualTo(30);
        assertThat(config.serverUuid()).isEmpty();
        assertThat(config.isConfigured()).isFalse();
    }

    @Test
    void emptyFileFallsBackToDefaults(@TempDir final Path dir) throws IOException {
        Files.write(dir.resolve("config.yml"), new byte[0]);

        final PixtillConfiguration config = new YamlConfigProvider(dir).load();

        assertThat(config.apiUrl().value()).isEqualTo("https://api.pixtill.com/v1");
        assertThat(config.isConfigured()).isFalse();
    }

    @Test
    void parsesDebugFlagWrittenAsString(@TempDir final Path dir) throws IOException {
        Files.write(dir.resolve("config.yml"),
                "debug: \"true\"\n".getBytes(StandardCharsets.UTF_8));

        assertThat(new YamlConfigProvider(dir).load().debug()).isTrue();
    }

    @Test
    void blankIntervalFallsBackToDefault(@TempDir final Path dir) throws IOException {
        Files.write(dir.resolve("config.yml"),
                "pollInterval: \"\"\n".getBytes(StandardCharsets.UTF_8));

        assertThat(new YamlConfigProvider(dir).load().pollInterval().value().getSeconds()).isEqualTo(30);
    }
}
