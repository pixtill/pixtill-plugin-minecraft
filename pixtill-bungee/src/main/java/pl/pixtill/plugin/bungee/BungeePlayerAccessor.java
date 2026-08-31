package pl.pixtill.plugin.bungee;

import net.md_5.bungee.api.ProxyServer;
import pl.pixtill.plugin.domain.PlayerIdentifier;
import pl.pixtill.plugin.port.PlayerAccessor;

public final class BungeePlayerAccessor implements PlayerAccessor {

    private final ProxyServer proxy;

    public BungeePlayerAccessor(final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean isOnline(final PlayerIdentifier player) {
        return proxy.getPlayer(player.value()) != null;
    }
}
