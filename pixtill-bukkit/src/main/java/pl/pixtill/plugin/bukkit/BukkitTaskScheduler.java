package pl.pixtill.plugin.bukkit;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import pl.pixtill.plugin.domain.PollInterval;
import pl.pixtill.plugin.port.ScheduledHandle;
import pl.pixtill.plugin.port.TaskScheduler;

public final class BukkitTaskScheduler implements TaskScheduler {

    private static final long TICKS_PER_SECOND = 20L;

    private final Plugin plugin;

    public BukkitTaskScheduler(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ScheduledHandle scheduleRepeating(final Runnable task, final PollInterval interval) {
        final long ticks = interval.toSeconds() * TICKS_PER_SECOND;
        final BukkitTask scheduled = plugin.getServer().getScheduler()
                .runTaskTimerAsynchronously(plugin, task, ticks, ticks);
        return scheduled::cancel;
    }
}
