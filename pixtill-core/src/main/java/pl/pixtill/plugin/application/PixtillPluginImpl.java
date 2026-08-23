package pl.pixtill.plugin.application;

import pl.pixtill.plugin.domain.InvalidValueException;
import pl.pixtill.plugin.domain.PixtillConfiguration;
import pl.pixtill.plugin.port.CommandDispatcher;
import pl.pixtill.plugin.port.ConfigProvider;
import pl.pixtill.plugin.port.PixtillApiClient;
import pl.pixtill.plugin.port.PixtillApiClientFactory;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;
import pl.pixtill.plugin.port.PlayerAccessor;
import pl.pixtill.plugin.port.ScheduledHandle;
import pl.pixtill.plugin.port.TaskScheduler;

final class PixtillPluginImpl implements PixtillPlugin {

    private final PlatformDataProvider platform;
    private final PixtillLogger logger;
    private final ConfigProvider configProvider;
    private final TaskScheduler scheduler;
    private final PlayerAccessor players;
    private final CommandDispatcher dispatcher;
    private final PixtillApiClientFactory apiClientFactory;

    private ScheduledHandle handle;
    private PixtillApiClient client;
    private PixtillConfiguration configuration;

    PixtillPluginImpl(
            final PlatformDataProvider platform,
            final PixtillLogger logger,
            final ConfigProvider configProvider,
            final TaskScheduler scheduler,
            final PlayerAccessor players,
            final CommandDispatcher dispatcher,
            final PixtillApiClientFactory apiClientFactory) {
        this.platform = platform;
        this.logger = logger;
        this.configProvider = configProvider;
        this.scheduler = scheduler;
        this.players = players;
        this.dispatcher = dispatcher;
        this.apiClientFactory = apiClientFactory;
    }

    @Override
    public void onEnable() {
        start();
    }

    @Override
    public void onDisable() {
        stop();
    }

    @Override
    public void reload() {
        logger.info("Reloading Pixtill configuration...");
        stop();
        start();
    }

    @Override
    public PixtillConfiguration configuration() {
        return configuration;
    }

    private void start() {
        try {
            configuration = configProvider.load();
        } catch (final InvalidValueException exception) {
            logger.error("Pixtill configuration error: " + exception.getMessage() + " Polling not started.");
            return;
        }

        logger.setDebugEnabled(configuration.debug());

        logger.info("Configuration loaded: apiUrl=" + configuration.apiUrl().value()
                + ", serverUuid=" + configuration.serverUuid().map(uuid -> uuid.value()).orElse("<none>")
                + ", pollInterval=" + configuration.pollInterval().toSeconds() + "s"
                + ", debug=" + configuration.debug());

        if (!configuration.isConfigured()) {
            logger.error("Pixtill requires configuration - set apiKey and serverUuid in config.yml. "
                    + "Polling not started.");
            return;
        }

        if (!configuration.apiUrl().isSecure() && !configuration.apiUrl().isLoopback()) {
            logger.error("SECURITY WARNING: apiUrl is not HTTPS. The API key and console commands "
                    + "are sent in plaintext and can be intercepted or forged. Use https:// in production.");
        }

        client = apiClientFactory.create(configuration, platform);
        final ProcessCommandQueueTask task = new ProcessCommandQueueTask(
                logger, client, players, dispatcher, configuration.requireServerUuid());

        handle = scheduler.scheduleRepeating(task, configuration.pollInterval());
        logger.info("Pixtill started - polling API every " + configuration.pollInterval().toSeconds()
                + "s for server " + configuration.requireServerUuid() + ".");
    }

    private void stop() {
        if (handle != null) {
            handle.cancel();
            handle = null;
            logger.debug("Polling task stopped.");
        }
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
