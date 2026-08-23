package pl.pixtill.plugin.bungee;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import pl.pixtill.plugin.domain.PollInterval;
import pl.pixtill.plugin.port.ScheduledHandle;
import pl.pixtill.plugin.port.TaskScheduler;

public final class BungeeTaskScheduler implements TaskScheduler {

    private final Plugin plugin;

    public BungeeTaskScheduler(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ScheduledHandle scheduleRepeating(final Runnable task, final PollInterval interval) {
        final long seconds = interval.toSeconds();
        final ScheduledTask scheduled = plugin.getProxy().getScheduler()
                .schedule(plugin, task, seconds, seconds, TimeUnit.SECONDS);
        return scheduled::cancel;
    }
}
