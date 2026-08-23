package pl.pixtill.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import org.slf4j.Logger;
import pl.pixtill.plugin.application.PixtillPlugin;
import pl.pixtill.plugin.domain.ServerVersion;
import pl.pixtill.plugin.infrastructure.config.YamlConfigProvider;
import pl.pixtill.plugin.infrastructure.http.HttpPixtillApiClientFactory;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;

public final class PixtillVelocityPlugin implements PlatformDataProvider {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private PixtillPlugin pixtill;

    @Inject
    public PixtillVelocityPlugin(
            final ProxyServer server, final Logger logger, final @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(final ProxyInitializeEvent event) {
        final PixtillLogger pixtillLogger = new VelocityPixtillLogger(logger);
        pixtill = PixtillPlugin.create(
                this,
                pixtillLogger,
                new YamlConfigProvider(dataDirectory),
                new VelocityTaskScheduler(this, server),
                new VelocityPlayerAccessor(server),
                new VelocityCommandDispatcher(server),
                new HttpPixtillApiClientFactory(pixtillLogger));
        pixtill.onEnable();

        final CommandManager commands = server.getCommandManager();
        final CommandMeta meta = commands.metaBuilder("pixtill").build();
        commands.register(meta, new PixtillReloadCommand(pixtill));
    }

    @Subscribe
    public void onProxyShutdown(final ProxyShutdownEvent event) {
        if (pixtill != null) {
            pixtill.onDisable();
        }
    }

    @Override
    public String pluginVersion() {
        return server.getPluginManager().fromInstance(this)
                .flatMap(container -> container.getDescription().getVersion())
                .orElse("Unknown");
    }

    @Override
    public String engineName() {
        return "Velocity";
    }

    @Override
    public ServerVersion engineVersion() {
        return ServerVersion.parse(server.getVersion().getVersion());
    }
}
