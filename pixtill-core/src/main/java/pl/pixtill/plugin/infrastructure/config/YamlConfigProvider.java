package pl.pixtill.plugin.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import pl.pixtill.plugin.domain.InvalidValueException;
import pl.pixtill.plugin.domain.PixtillConfiguration;
import pl.pixtill.plugin.port.ConfigProvider;

public final class YamlConfigProvider implements ConfigProvider {

    private static final String CONFIG_FILE_NAME = "config.yml";
    private static final String DEFAULT_API_URL = "https://api.pixtill.com/v1";
    private static final String DEFAULT_POLL_INTERVAL = "30s";
    private static final String DEFAULT_CONFIG_TEMPLATE =
            "# Pixtill store API base URL (leave the default unless self-hosting)\n"
                    + "apiUrl: \"https://api.pixtill.com/v1\"\n\n"
                    + "# API key - store panel -> Settings -> API keys (X-API-Key header)\n"
                    + "apiKey: \"\"\n\n"
                    + "# Server UUID - store panel -> Servers\n"
                    + "serverUuid: \"\"\n\n"
                    + "# How often to poll the API (e.g. 30s, 1m, 2m). Minimum 5s.\n"
                    + "pollInterval: \"30s\"\n\n"
                    + "# Debug logs - sensitive; enable only for diagnosis.\n"
                    + "debug: false\n";

    private final Path dataFolder;
    private final PixtillConfigurationMapper mapper;

    public YamlConfigProvider(final Path dataFolder) {
        this.dataFolder = dataFolder;
        this.mapper = new PixtillConfigurationMapper();
    }

    @Override
    public PixtillConfiguration load() {
        final Path configFile = ensureConfigFile();
        final Map<String, Object> values = readYaml(configFile);
        return mapper.map(
                string(values, "apiUrl", DEFAULT_API_URL),
                string(values, "apiKey", ""),
                string(values, "serverUuid", ""),
                string(values, "pollInterval", DEFAULT_POLL_INTERVAL),
                bool(values, "debug"));
    }

    private Path ensureConfigFile() {
        final Path configFile = dataFolder.resolve(CONFIG_FILE_NAME);
        if (Files.exists(configFile)) {
            return configFile;
        }
        try {
            Files.createDirectories(dataFolder);
            Files.write(configFile, DEFAULT_CONFIG_TEMPLATE.getBytes(StandardCharsets.UTF_8));
        } catch (final IOException exception) {
            throw new InvalidValueException("Could not create default config.yml: " + exception.getMessage());
        }
        return configFile;
    }

    private Map<String, Object> readYaml(final Path configFile) {
        try (InputStream input = Files.newInputStream(configFile)) {
            final Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
            final Object loaded = yaml.load(input);
            if (loaded instanceof Map) {
                return castToStringMap(loaded);
            }
            return Collections.emptyMap();
        } catch (final IOException exception) {
            throw new InvalidValueException("Could not read config.yml: " + exception.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castToStringMap(final Object loaded) {
        return (Map<String, Object>) loaded;
    }

    private static String string(final Map<String, Object> values, final String key, final String fallback) {
        final Object value = values.get(key);
        if (value == null) {
            return fallback;
        }
        final String text = String.valueOf(value);
        return text.isEmpty() ? fallback : text;
    }

    private static boolean bool(final Map<String, Object> values, final String key) {
        final Object value = values.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return value != null && Boolean.parseBoolean(String.valueOf(value));
    }
}
