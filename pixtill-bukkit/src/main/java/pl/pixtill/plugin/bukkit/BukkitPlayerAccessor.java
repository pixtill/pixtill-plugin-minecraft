package pl.pixtill.plugin.bukkit;

import org.bukkit.Server;
import pl.pixtill.plugin.domain.PlayerIdentifier;
import pl.pixtill.plugin.port.PlayerAccessor;

public final class BukkitPlayerAccessor implements PlayerAccessor {

    private final Server server;

    public BukkitPlayerAccessor(final Server server) {
        this.server = server;
    }

    @Override
    public boolean isOnline(final PlayerIdentifier player) {
        return server.getPlayerExact(player.value()) != null;
    }
}
