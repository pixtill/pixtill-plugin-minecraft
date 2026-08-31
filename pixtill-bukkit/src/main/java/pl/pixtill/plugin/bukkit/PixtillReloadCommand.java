package pl.pixtill.plugin.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.pixtill.plugin.application.PixtillPlugin;
import pl.pixtill.plugin.application.ReloadCommand;

public final class PixtillReloadCommand implements CommandExecutor {

    private final ReloadCommand reloadCommand;

    public PixtillReloadCommand(final PixtillPlugin plugin) {
        this.reloadCommand = new ReloadCommand(plugin);
    }

    @Override
    public boolean onCommand(
            final CommandSender sender, final Command command, final String label, final String[] args) {
        final boolean hasPermission = sender.hasPermission(ReloadCommand.PERMISSION);
        sender.sendMessage(reloadCommand.execute(hasPermission, args));
        return true;
    }
}
