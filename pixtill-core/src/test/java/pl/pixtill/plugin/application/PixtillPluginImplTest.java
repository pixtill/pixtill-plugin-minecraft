package pl.pixtill.plugin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pixtill.plugin.domain.ApiKey;
import pl.pixtill.plugin.domain.ApiUrl;
import pl.pixtill.plugin.domain.InvalidValueException;
import pl.pixtill.plugin.domain.PixtillConfiguration;
import pl.pixtill.plugin.domain.PollInterval;
import pl.pixtill.plugin.domain.ServerUuid;
import pl.pixtill.plugin.port.CommandDispatcher;
import pl.pixtill.plugin.port.ConfigProvider;
import pl.pixtill.plugin.port.PixtillApiClient;
import pl.pixtill.plugin.port.PixtillApiClientFactory;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;
import pl.pixtill.plugin.port.PlayerAccessor;
import pl.pixtill.plugin.port.ScheduledHandle;
import pl.pixtill.plugin.port.TaskScheduler;

@ExtendWith(MockitoExtension.class)
class PixtillPluginImplTest {

    private static final ServerUuid SERVER = ServerUuid.of(UUID.randomUUID().toString());
    private static final PollInterval INTERVAL = PollInterval.parse("30s");

    @Mock private PlatformDataProvider platform;
    @Mock private PixtillLogger logger;
    @Mock private ConfigProvider configProvider;
    @Mock private TaskScheduler scheduler;
    @Mock private PlayerAccessor players;
    @Mock private CommandDispatcher dispatcher;
    @Mock private PixtillApiClientFactory apiClientFactory;
    @Mock private PixtillApiClient apiClient;
    @Mock private ScheduledHandle handle;

    private PixtillPlugin plugin;

    @BeforeEach
    void setUp() {
        plugin = PixtillPlugin.create(
                platform, logger, configProvider, scheduler, players, dispatcher, apiClientFactory);
    }

    private PixtillConfiguration configured(final String url) {
        return new PixtillConfiguration(
                ApiUrl.of(url), ApiKey.of("pk_test"), SERVER, INTERVAL, false);
    }

    private PixtillConfiguration notConfigured() {
        return new PixtillConfiguration(
                ApiUrl.of("https://api.pixtill.com/v1"), null, null, INTERVAL, false);
    }

    @Test
    void doesNotScheduleWhenNotConfigured() {
        when(configProvider.load()).thenReturn(notConfigured());

        plugin.onEnable();

        verify(logger).error(contains("requires configuration"));
        verify(scheduler, never()).scheduleRepeating(any(), any());
    }

    @Test
    void doesNotScheduleWhenConfigInvalid() {
        when(configProvider.load()).thenThrow(new InvalidValueException("bad serverUuid"));

        plugin.onEnable();

        verify(logger).error(contains("configuration error"));
        verify(scheduler, never()).scheduleRepeating(any(), any());
    }

    @Test
    void schedulesPollingWhenConfiguredOverHttps() {
        when(configProvider.load()).thenReturn(configured("https://api.pixtill.com/v1"));
        when(apiClientFactory.create(any(), any())).thenReturn(apiClient);
        when(scheduler.scheduleRepeating(any(), any())).thenReturn(handle);

        plugin.onEnable();

        verify(scheduler).scheduleRepeating(any(Runnable.class), eq(INTERVAL));
        verify(logger, never()).error(contains("SECURITY WARNING"));
        assertThat(plugin.configuration().isConfigured()).isTrue();
    }

    @Test
    void warnsWhenApiUrlIsPlaintextAndStillSchedules() {
        when(configProvider.load()).thenReturn(configured("http://api.pixtill.com/v1"));
        when(apiClientFactory.create(any(), any())).thenReturn(apiClient);
        when(scheduler.scheduleRepeating(any(), any())).thenReturn(handle);

        plugin.onEnable();

        verify(logger).error(contains("SECURITY WARNING"));
        verify(scheduler).scheduleRepeating(any(), eq(INTERVAL));
    }

    @Test
    void reloadCancelsTaskAndClosesClientBeforeRestarting() {
        when(configProvider.load()).thenReturn(configured("https://api.pixtill.com/v1"));
        when(apiClientFactory.create(any(), any())).thenReturn(apiClient);
        when(scheduler.scheduleRepeating(any(), any())).thenReturn(handle);

        plugin.onEnable();
        plugin.reload();

        verify(handle).cancel();
        verify(apiClient).close();
        verify(scheduler, times(2)).scheduleRepeating(any(), any());
    }

    @Test
    void disableCancelsTaskAndClosesClient() {
        when(configProvider.load()).thenReturn(configured("https://api.pixtill.com/v1"));
        when(apiClientFactory.create(any(), any())).thenReturn(apiClient);
        when(scheduler.scheduleRepeating(any(), any())).thenReturn(handle);

        plugin.onEnable();
        plugin.onDisable();

        verify(handle).cancel();
        verify(apiClient).close();
    }
}
