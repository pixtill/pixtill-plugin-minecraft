package pl.pixtill.plugin.application;

public final class ReloadCommand {

    public static final String PERMISSION = "pixtill.reload";

    static final String NO_PERMISSION = "You do not have permission to use this command.";
    static final String USAGE = "Usage: /pixtill reload";
    static final String RELOADED = "Pixtill configuration reloaded.";

    private final PixtillPlugin plugin;

    public ReloadCommand(final PixtillPlugin plugin) {
        this.plugin = plugin;
    }

    public String execute(final boolean hasPermission, final String[] arguments) {
        if (!hasPermission) {
            return NO_PERMISSION;
        }
        if (arguments.length != 1 || !"reload".equalsIgnoreCase(arguments[0])) {
            return USAGE;
        }
        plugin.reload();
        return RELOADED;
    }
}
