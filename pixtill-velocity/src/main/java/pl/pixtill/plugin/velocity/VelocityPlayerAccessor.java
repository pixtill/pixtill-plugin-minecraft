package pl.pixtill.plugin.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import pl.pixtill.plugin.domain.PlayerName;
import pl.pixtill.plugin.port.PlayerAccessor;

public final class VelocityPlayerAccessor implements PlayerAccessor {

    private final ProxyServer server;

    public VelocityPlayerAccessor(final ProxyServer server) {
        this.server = server;
    }

    @Override
    public boolean isOnline(final PlayerName player) {
        return server.getPlayer(player.value()).isPresent();
    }
}
