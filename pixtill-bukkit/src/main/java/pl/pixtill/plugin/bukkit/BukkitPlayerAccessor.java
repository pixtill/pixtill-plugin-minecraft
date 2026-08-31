package pl.pixtill.plugin.bukkit;

import org.bukkit.Server;
import pl.pixtill.plugin.domain.PlayerName;
import pl.pixtill.plugin.port.PlayerAccessor;

public final class BukkitPlayerAccessor implements PlayerAccessor {

    private final Server server;

    public BukkitPlayerAccessor(final Server server) {
        this.server = server;
    }

    @Override
    public boolean isOnline(final PlayerName player) {
        return server.getPlayerExact(player.value()) != null;
    }
}
