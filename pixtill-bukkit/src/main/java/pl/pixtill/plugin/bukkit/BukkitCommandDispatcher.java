package pl.pixtill.plugin.bukkit;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import pl.pixtill.plugin.domain.ConsoleCommand;
import pl.pixtill.plugin.port.CommandDispatcher;

public final class BukkitCommandDispatcher implements CommandDispatcher {

    private final Plugin plugin;
    private final Server server;

    public BukkitCommandDispatcher(final Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override
    public void dispatch(final ConsoleCommand command) {
        server.getScheduler().runTask(
                plugin,
                () -> server.dispatchCommand(server.getConsoleSender(), command.value()));
    }
}
