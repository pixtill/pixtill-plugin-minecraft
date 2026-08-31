package pl.pixtill.plugin.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import pl.pixtill.plugin.domain.PollInterval;
import pl.pixtill.plugin.port.ScheduledHandle;
import pl.pixtill.plugin.port.TaskScheduler;

public final class VelocityTaskScheduler implements TaskScheduler {

    private final Object plugin;
    private final ProxyServer server;

    public VelocityTaskScheduler(final Object plugin, final ProxyServer server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public ScheduledHandle scheduleRepeating(final Runnable task, final PollInterval interval) {
        final ScheduledTask scheduled = server.getScheduler()
                .buildTask(plugin, task)
                .repeat(interval.value())
                .schedule();
        return scheduled::cancel;
    }
}
