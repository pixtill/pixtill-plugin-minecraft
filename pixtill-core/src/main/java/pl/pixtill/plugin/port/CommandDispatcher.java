package pl.pixtill.plugin.port;

import pl.pixtill.plugin.domain.ConsoleCommand;

public interface CommandDispatcher {

    void dispatch(ConsoleCommand command);
}
