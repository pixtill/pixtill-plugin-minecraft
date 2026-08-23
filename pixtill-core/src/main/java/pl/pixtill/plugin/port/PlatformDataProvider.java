package pl.pixtill.plugin.port;

import pl.pixtill.plugin.domain.ServerVersion;

public interface PlatformDataProvider {

    String pluginVersion();

    String engineName();

    ServerVersion engineVersion();
}
