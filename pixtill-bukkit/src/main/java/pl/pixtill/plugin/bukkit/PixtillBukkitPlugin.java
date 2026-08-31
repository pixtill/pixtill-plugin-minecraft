package pl.pixtill.plugin.bukkit;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;
import pl.pixtill.plugin.application.PixtillPlugin;
import pl.pixtill.plugin.domain.ServerVersion;
import pl.pixtill.plugin.infrastructure.config.YamlConfigProvider;
import pl.pixtill.plugin.infrastructure.http.HttpPixtillApiClientFactory;
import pl.pixtill.plugin.infrastructure.logging.JavaUtilLoggingLogger;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;

public final class PixtillBukkitPlugin extends JavaPlugin implements PlatformDataProvider {

    private PixtillPlugin pixtill;

    @Override
    public void onEnable() {
        final PixtillLogger logger = new JavaUtilLoggingLogger(getLogger());
        pixtill = PixtillPlugin.create(
                this,
                logger,
                new YamlConfigProvider(getDataFolder().toPath()),
                new BukkitTaskScheduler(this),
                new BukkitPlayerAccessor(getServer()),
                new BukkitCommandDispatcher(this),
                new HttpPixtillApiClientFactory(logger));
        pixtill.onEnable();

        Objects.requireNonNull(getCommand("pixtill")).setExecutor(new PixtillReloadCommand(pixtill));
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
        return "Bukkit";
    }

    @Override
    public ServerVersion engineVersion() {
        return ServerVersion.parse(getServer().getBukkitVersion());
    }
}
