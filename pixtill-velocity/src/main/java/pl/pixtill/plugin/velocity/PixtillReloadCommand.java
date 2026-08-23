package pl.pixtill.plugin.velocity;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import pl.pixtill.plugin.application.PixtillPlugin;
import pl.pixtill.plugin.application.ReloadCommand;

public final class PixtillReloadCommand implements SimpleCommand {

    private final ReloadCommand reloadCommand;

    public PixtillReloadCommand(final PixtillPlugin plugin) {
        this.reloadCommand = new ReloadCommand(plugin);
    }

    @Override
    public void execute(final Invocation invocation) {
        final boolean hasPermission = invocation.source().hasPermission(ReloadCommand.PERMISSION);
        final String message = reloadCommand.execute(hasPermission, invocation.arguments());
        invocation.source().sendMessage(Component.text(message));
    }
}
