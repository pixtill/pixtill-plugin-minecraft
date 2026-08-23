package pl.pixtill.plugin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pixtill.plugin.domain.CommandId;
import pl.pixtill.plugin.domain.CommandResult;
import pl.pixtill.plugin.domain.CommandStatus;
import pl.pixtill.plugin.domain.ConsoleCommand;
import pl.pixtill.plugin.domain.PlayerName;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.domain.ServerUuid;
import pl.pixtill.plugin.port.ApiClientException;
import pl.pixtill.plugin.port.CommandDispatcher;
import pl.pixtill.plugin.port.PixtillApiClient;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlayerAccessor;

@ExtendWith(MockitoExtension.class)
class ProcessCommandQueueTaskTest {

    private static final ServerUuid SERVER = ServerUuid.of("11111111-1111-1111-1111-111111111111");

    @Mock private PixtillLogger logger;
    @Mock private PixtillApiClient apiClient;
    @Mock private PlayerAccessor players;
    @Mock private CommandDispatcher dispatcher;
    @Captor private ArgumentCaptor<List<CommandResult>> resultsCaptor;

    private ProcessCommandQueueTask task;

    @BeforeEach
    void setUp() {
        task = new ProcessCommandQueueTask(logger, apiClient, players, dispatcher, SERVER);
    }

    private QueuedCommand command(final String player, final boolean requiresOnline) {
        return new QueuedCommand(
                CommandId.of(UUID.randomUUID()),
                ConsoleCommand.of("give " + player + " diamond"),
                player == null ? null : PlayerName.of(player),
                requiresOnline);
    }

    private List<CommandStatus> reportedStatuses() {
        verify(apiClient).report(eq(SERVER), resultsCaptor.capture());
        return resultsCaptor.getValue().stream().map(CommandResult::status).collect(Collectors.toList());
    }

    @Test
    @DisplayName("player online -> dispatched and reported completed")
    void executesAndReportsCompleted_whenPlayerOnline() {
        final QueuedCommand cmd = command("Notch", true);
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Collections.singletonList(cmd));
        when(players.isOnline(any())).thenReturn(true);

        task.run();

        verify(dispatcher).dispatch(cmd.command());
        assertThat(reportedStatuses()).containsExactly(CommandStatus.COMPLETED);
    }

    @Test
    @DisplayName("player offline and required -> not dispatched, reported deferred")
    void deferred_whenRequiredPlayerOffline() {
        final QueuedCommand cmd = command("Notch", true);
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Collections.singletonList(cmd));
        when(players.isOnline(any())).thenReturn(false);

        task.run();

        verify(dispatcher, never()).dispatch(any());
        assertThat(reportedStatuses()).containsExactly(CommandStatus.DEFERRED);
    }

    @Test
    @DisplayName("command without player -> dispatched and reported completed")
    void executes_whenNoPlayerRequirement() {
        final QueuedCommand cmd = command(null, false);
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Collections.singletonList(cmd));

        task.run();

        verify(dispatcher).dispatch(cmd.command());
        assertThat(reportedStatuses()).containsExactly(CommandStatus.COMPLETED);
    }

    @Test
    @DisplayName("player appears later -> command executes on a subsequent cycle")
    void executes_whenPlayerAppearsLater() {
        final QueuedCommand cmd = command("Notch", true);
        when(apiClient.claim(eq(SERVER), anyInt()))
                .thenReturn(Collections.singletonList(cmd))
                .thenReturn(Collections.singletonList(cmd));
        when(players.isOnline(any())).thenReturn(false).thenReturn(true);

        task.run();
        task.run();

        verify(dispatcher, times(1)).dispatch(cmd.command());
    }

    @Test
    @DisplayName("deferral logged once at INFO, then DEBUG on later cycles (no spam)")
    void logsDeferralOnceOnInfoThenDebug() {
        final QueuedCommand cmd = command("Notch", true);
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Collections.singletonList(cmd));
        when(players.isOnline(any())).thenReturn(false);

        task.run();
        task.run();
        task.run();

        verify(logger, times(1)).info(contains("deferred until player Notch"));
        verify(logger, times(2)).debug(contains("Still waiting"));
    }

    @Test
    @DisplayName("empty claim -> nothing dispatched or reported")
    void doesNothing_whenNoCommandsClaimed() {
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Collections.emptyList());

        task.run();

        verify(dispatcher, never()).dispatch(any());
        verify(apiClient, never()).report(any(), any());
    }

    @Test
    @DisplayName("claim failure -> logs error, does not propagate, does not report")
    void logsError_andSwallows_whenClaimFails() {
        when(apiClient.claim(eq(SERVER), anyInt())).thenThrow(new ApiClientException("timeout"));

        task.run();

        verify(logger).error(contains("Failed to claim commands"));
        verify(dispatcher, never()).dispatch(any());
        verify(apiClient, never()).report(any(), any());
    }

    @Test
    @DisplayName("dispatch failure -> reported failed, error logged")
    void reportsFailed_whenDispatchThrows() {
        final QueuedCommand cmd = command(null, false);
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Collections.singletonList(cmd));
        doThrow(new RuntimeException("boom")).when(dispatcher).dispatch(any());

        task.run();

        assertThat(reportedStatuses()).containsExactly(CommandStatus.FAILED);
        verify(logger).error(contains("Failed to execute command"));
    }

    @Test
    @DisplayName("report failure -> logged, not propagated")
    void logsError_whenReportFails() {
        final QueuedCommand cmd = command(null, false);
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Collections.singletonList(cmd));
        doThrow(new ApiClientException("500")).when(apiClient).report(eq(SERVER), any());

        task.run();

        verify(dispatcher).dispatch(cmd.command());
        verify(logger).error(contains("Failed to report command results"));
    }

    @Test
    @DisplayName("mixed batch -> completed for available, deferred for blocked")
    void handlesMixedBatch() {
        final QueuedCommand ready = command(null, false);
        final QueuedCommand blocked = command("Notch", true);
        when(apiClient.claim(eq(SERVER), anyInt())).thenReturn(Arrays.asList(ready, blocked));
        when(players.isOnline(any())).thenReturn(false);

        task.run();

        verify(dispatcher, times(1)).dispatch(ready.command());
        assertThat(reportedStatuses()).containsExactly(CommandStatus.COMPLETED, CommandStatus.DEFERRED);
    }
}
