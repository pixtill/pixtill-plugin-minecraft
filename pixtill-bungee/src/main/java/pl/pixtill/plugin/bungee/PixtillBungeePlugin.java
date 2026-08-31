package pl.pixtill.plugin.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import pl.pixtill.plugin.application.PixtillPlugin;
import pl.pixtill.plugin.domain.ServerVersion;
import pl.pixtill.plugin.infrastructure.config.YamlConfigProvider;
import pl.pixtill.plugin.infrastructure.http.HttpPixtillApiClientFactory;
import pl.pixtill.plugin.infrastructure.logging.JavaUtilLoggingLogger;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;

public final class PixtillBungeePlugin extends Plugin implements PlatformDataProvider {

    private PixtillPlugin pixtill;

    @Override
    public void onEnable() {
        final PixtillLogger logger = new JavaUtilLoggingLogger(getLogger());
        pixtill = PixtillPlugin.create(
                this,
                logger,
                new YamlConfigProvider(getDataFolder().toPath()),
                new BungeeTaskScheduler(this),
                new BungeePlayerAccessor(getProxy()),
                new BungeeCommandDispatcher(getProxy()),
                new HttpPixtillApiClientFactory(logger));
        pixtill.onEnable();

        getProxy().getPluginManager().registerCommand(this, new PixtillReloadCommand(pixtill));
    }

    @Override
    public void onDisable() {
        if (pixtill != null) {
            pixtill.onDisable();
        }
    }

    @Override
    public String pluginVersion() {
        return getDescription().getVersion();
    }

    @Override
    public String engineName() {
        return "BungeeCord";
    }

    @Override
    public ServerVersion engineVersion() {
        return ServerVersion.parse(getProxy().getVersion());
    }
}
