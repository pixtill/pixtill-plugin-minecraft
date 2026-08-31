package pl.pixtill.plugin.port;

import java.util.List;
import pl.pixtill.plugin.domain.CommandResult;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.domain.ServerUuid;

public interface PixtillApiClient {

    List<QueuedCommand> claim(ServerUuid server, int max);

    void report(ServerUuid server, List<CommandResult> results);

    default void close() {
    }
}
