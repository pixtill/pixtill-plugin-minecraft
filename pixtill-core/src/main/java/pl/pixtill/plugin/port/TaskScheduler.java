package pl.pixtill.plugin.port;

import pl.pixtill.plugin.domain.PollInterval;

public interface TaskScheduler {

    ScheduledHandle scheduleRepeating(Runnable task, PollInterval interval);
}
