package pl.pixtill.plugin.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import pl.pixtill.plugin.domain.CommandResult;
import pl.pixtill.plugin.domain.PlayerName;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.domain.ServerUuid;
import pl.pixtill.plugin.port.ApiClientException;
import pl.pixtill.plugin.port.CommandDispatcher;
import pl.pixtill.plugin.port.PixtillApiClient;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlayerAccessor;

public final class ProcessCommandQueueTask implements Runnable {

    private static final int CLAIM_BATCH_SIZE = 100;

    private final PixtillLogger logger;
    private final PixtillApiClient apiClient;
    private final PlayerAccessor players;
    private final CommandDispatcher dispatcher;
    private final ServerUuid server;

    private final Set<String> announcedWaiting = ConcurrentHashMap.newKeySet();

    public ProcessCommandQueueTask(
            final PixtillLogger logger,
            final PixtillApiClient apiClient,
            final PlayerAccessor players,
            final CommandDispatcher dispatcher,
            final ServerUuid server) {
        this.logger = logger;
        this.apiClient = apiClient;
        this.players = players;
        this.dispatcher = dispatcher;
        this.server = server;
    }

    @Override
    public void run() {
        final List<QueuedCommand> claimed;
        try {
            claimed = apiClient.claim(server, CLAIM_BATCH_SIZE);
        } catch (final ApiClientException exception) {
            logger.error("Failed to claim commands from API: " + exception.getMessage());
            return;
        }

        if (claimed == null || claimed.isEmpty()) {
            logger.debug("No commands claimed.");
            announcedWaiting.clear();
            return;
        }

        final List<CommandResult> results = new ArrayList<>();
        final Set<String> waitingThisCycle = new HashSet<>();
        for (final QueuedCommand command : claimed) {
            if (command.isBlockedBy(this::isOnline)) {
                results.add(CommandResult.deferred(command.id()));
                announceDeferral(command, waitingThisCycle);
            } else {
                results.add(execute(command));
            }
        }

        announcedWaiting.clear();
        announcedWaiting.addAll(waitingThisCycle);

        try {
            apiClient.report(server, results);
        } catch (final ApiClientException exception) {
            logger.error("Failed to report command results to API: " + exception.getMessage()
                    + " (leased commands will be retried after the lease expires).");
        }
    }

    private CommandResult execute(final QueuedCommand command) {
        final String id = command.id().value();
        final String who = playerLabel(command);

        try {
            dispatcher.dispatch(command.command());
        } catch (final RuntimeException exception) {
            logger.error("Failed to execute command " + id + " (player " + who + "): " + exception.getMessage());
            return CommandResult.failed(command.id(), exception.getMessage());
        }

        logger.info("Executed command for player " + who + " (id " + id + "): " + command.command().value());
        return CommandResult.completed(command.id());
    }

    private void announceDeferral(final QueuedCommand command, final Set<String> waitingThisCycle) {
        final String id = command.id().value();
        final String who = playerLabel(command);
        waitingThisCycle.add(id);

        if (announcedWaiting.contains(id)) {
            logger.debug("Still waiting for player " + who + " to come online (command " + id + ").");
        } else {
            logger.info("Command " + id + " deferred until player " + who + " comes online.");
        }
    }

    private boolean isOnline(final PlayerName player) {
        return players.isOnline(player);
    }

    private static String playerLabel(final QueuedCommand command) {
        return command.player().map(PlayerName::value).orElse("-");
    }
}
