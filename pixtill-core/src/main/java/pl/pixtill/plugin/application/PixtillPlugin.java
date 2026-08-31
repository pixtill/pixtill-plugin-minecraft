package pl.pixtill.plugin.application;

import pl.pixtill.plugin.domain.PixtillConfiguration;
import pl.pixtill.plugin.port.CommandDispatcher;
import pl.pixtill.plugin.port.ConfigProvider;
import pl.pixtill.plugin.port.PixtillApiClientFactory;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;
import pl.pixtill.plugin.port.PlayerAccessor;
import pl.pixtill.plugin.port.TaskScheduler;

public interface PixtillPlugin {

    static PixtillPlugin create(
            final PlatformDataProvider platform,
            final PixtillLogger logger,
            final ConfigProvider configProvider,
            final TaskScheduler scheduler,
            final PlayerAccessor players,
            final CommandDispatcher dispatcher,
            final PixtillApiClientFactory apiClientFactory) {
        return new PixtillPluginImpl(
                platform, logger, configProvider, scheduler, players, dispatcher, apiClientFactory);
    }

    void onEnable();

    void onDisable();

    void reload();

    PixtillConfiguration configuration();
}
