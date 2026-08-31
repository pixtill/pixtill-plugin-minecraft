package pl.pixtill.plugin.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.pixtill.plugin.domain.PixtillConfiguration;

class YamlConfigProviderTest {

    @Test
    void writesDefaultConfigWhenMissingAndReportsNotConfigured(@TempDir final Path dir) {
        final PixtillConfiguration config = new YamlConfigProvider(dir).load();

        assertThat(Files.exists(dir.resolve("config.yml"))).isTrue();
        assertThat(config.isConfigured()).isFalse();
        assertThat(config.apiUrl().value()).isEqualTo("https://api.pixtill.com/v1");
        assertThat(config.pollInterval().value().getSeconds()).isEqualTo(30);
    }

    @Test
    void readsExistingConfig(@TempDir final Path dir) throws IOException {
        final String yaml = "apiUrl: \"https://api.pixtill.com/v1\"\n"
                + "apiKey: \"pk_live_abc\"\n"
                + "serverUuid: \"11111111-1111-1111-1111-111111111111\"\n"
                + "pollInterval: \"1m\"\n"
                + "debug: true\n";
        Files.write(dir.resolve("config.yml"), yaml.getBytes(StandardCharsets.UTF_8));

        final PixtillConfiguration config = new YamlConfigProvider(dir).load();

        assertThat(config.isConfigured()).isTrue();
        assertThat(config.requireApiKey().value()).isEqualTo("pk_live_abc");
        assertThat(config.requireServerUuid().value()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(config.pollInterval().value().getSeconds()).isEqualTo(60);
        assertThat(config.debug()).isTrue();
    }
}
