package pl.pixtill.plugin.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import pl.pixtill.plugin.domain.ConsoleCommand;
import pl.pixtill.plugin.port.CommandDispatcher;

public final class VelocityCommandDispatcher implements CommandDispatcher {

    private final ProxyServer server;

    public VelocityCommandDispatcher(final ProxyServer server) {
        this.server = server;
    }

    @Override
    public void dispatch(final ConsoleCommand command) {
        server.getCommandManager().executeAsync(server.getConsoleCommandSource(), command.value());
    }
}
