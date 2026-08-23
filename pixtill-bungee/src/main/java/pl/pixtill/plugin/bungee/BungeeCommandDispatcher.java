package pl.pixtill.plugin.bungee;

import net.md_5.bungee.api.ProxyServer;
import pl.pixtill.plugin.domain.ConsoleCommand;
import pl.pixtill.plugin.port.CommandDispatcher;

public final class BungeeCommandDispatcher implements CommandDispatcher {

    private final ProxyServer proxy;

    public BungeeCommandDispatcher(final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void dispatch(final ConsoleCommand command) {
        proxy.getPluginManager().dispatchCommand(proxy.getConsole(), command.value());
    }
}
