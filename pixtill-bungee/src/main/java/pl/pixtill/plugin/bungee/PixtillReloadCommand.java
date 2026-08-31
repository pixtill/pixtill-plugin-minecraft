package pl.pixtill.plugin.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import pl.pixtill.plugin.application.PixtillPlugin;
import pl.pixtill.plugin.application.ReloadCommand;

public final class PixtillReloadCommand extends Command {

    private final ReloadCommand reloadCommand;

    public PixtillReloadCommand(final PixtillPlugin plugin) {
        super("pixtill", ReloadCommand.PERMISSION);
        this.reloadCommand = new ReloadCommand(plugin);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final boolean hasPermission = sender.hasPermission(ReloadCommand.PERMISSION);
        sender.sendMessage(new TextComponent(reloadCommand.execute(hasPermission, args)));
    }
}
