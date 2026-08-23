package pl.pixtill.plugin.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pixtill.plugin.domain.ApiKey;
import pl.pixtill.plugin.domain.ApiUrl;
import pl.pixtill.plugin.domain.CommandId;
import pl.pixtill.plugin.domain.CommandResult;
import pl.pixtill.plugin.domain.PixtillConfiguration;
import pl.pixtill.plugin.domain.PollInterval;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.domain.ServerUuid;
import pl.pixtill.plugin.domain.ServerVersion;
import pl.pixtill.plugin.port.ApiClientException;
import pl.pixtill.plugin.port.PixtillApiClient;
import pl.pixtill.plugin.port.PixtillLogger;
import pl.pixtill.plugin.port.PlatformDataProvider;

class HttpPixtillApiClientErrorTest {

    private static final ServerUuid SERVER = ServerUuid.of("11111111-1111-1111-1111-111111111111");
    private static final CommandId ID = CommandId.of("22222222-2222-2222-2222-222222222222");

    private MockWebServer server;
    private PixtillLogger logger;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        logger = mock(PixtillLogger.class);
    }

    @AfterEach
    void tearDown() {
        try {
            server.shutdown();
        } catch (final Exception ignored) {
        }
    }

    private PixtillApiClient client(final boolean debug) {
        final PixtillConfiguration config = new PixtillConfiguration(
                ApiUrl.of(server.url("/v1").toString()),
                ApiKey.of("pk_test"),
                SERVER,
                PollInterval.parse("30s"),
                debug);
        return new HttpPixtillApiClientFactory(logger).create(config, new StubPlatform());
    }

    @Test
    void throwsOnMalformedJson() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{ this is not json"));

        assertThatThrownBy(() -> client(false).claim(SERVER, 100)).isInstanceOf(ApiClientException.class);
    }

    @Test
    void skipsMalformedCommandButKeepsValidOnes() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"commands\":["
                        + "{\"uuid\":\"not-a-uuid\",\"command\":\"say bad\"},"
                        + "{\"uuid\":\"33333333-3333-3333-3333-333333333333\",\"command\":\"say ok\"}"
                        + "]}"));

        final List<QueuedCommand> commands = client(false).claim(SERVER, 100);

        assertThat(commands).hasSize(1);
        assertThat(commands.get(0).command().value()).isEqualTo("say ok");
        verify(logger).error(contains("Skipping malformed command"));
    }

    @Test
    void handlesBatchWithoutCommandsKey() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        assertThat(client(false).claim(SERVER, 100)).isEmpty();
    }

    @Test
    void emptyBodyIsTreatedAsNoCommands() {
        server.enqueue(new MockResponse().setResponseCode(200));

        assertThat(client(false).claim(SERVER, 100)).isEmpty();
    }

    @Test
    void throwsOnNotFound() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client(false).claim(SERVER, 100)).isInstanceOf(ApiClientException.class);
    }

    @Test
    void claimInDebugModeLogs() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"commands\":[]}"));

        client(true).claim(SERVER, 100);

        verify(logger, atLeastOnce()).debug(anyString());
    }

    @Test
    void reportInDebugModeLogs() {
        server.enqueue(new MockResponse().setResponseCode(200));

        client(true).report(SERVER, Collections.singletonList(CommandResult.completed(ID)));

        verify(logger, atLeastOnce()).debug(anyString());
    }

    @Test
    void claimWrapsNetworkErrors() throws Exception {
        final PixtillApiClient client = client(false);
        server.shutdown();

        assertThatThrownBy(() -> client.claim(SERVER, 100)).isInstanceOf(ApiClientException.class);
    }

    @Test
    void reportWrapsNetworkErrors() throws Exception {
        final PixtillApiClient client = client(false);
        server.shutdown();

        assertThatThrownBy(() -> client.report(SERVER, Collections.singletonList(CommandResult.completed(ID))))
                .isInstanceOf(ApiClientException.class);
    }

    private static final class StubPlatform implements PlatformDataProvider {
        @Override
        public String pluginVersion() {
            return "0.1.0";
        }

        @Override
        public String engineName() {
            return "Test";
        }

        @Override
        public ServerVersion engineVersion() {
            return ServerVersion.parse("26.2");
        }
    }
}
