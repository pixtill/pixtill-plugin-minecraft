package pl.pixtill.plugin.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
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

class HttpPixtillApiClientTest {

    private static final ServerUuid SERVER = ServerUuid.of("11111111-1111-1111-1111-111111111111");

    private MockWebServer server;
    private PixtillApiClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        final PixtillConfiguration config = new PixtillConfiguration(
                ApiUrl.of(server.url("/v1").toString()),
                ApiKey.of("pk_test"),
                SERVER,
                PollInterval.parse("30s"),
                false);
        client = new HttpPixtillApiClientFactory(mock(PixtillLogger.class)).create(config, new StubPlatform());
    }

    @AfterEach
    void tearDown() {
        try {
            server.shutdown();
        } catch (final Exception ignored) {
        }
    }

    @Test
    void claimsCommandsFromBatch() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"@type\":\"CommandBatch\",\"serverUuid\":\"" + SERVER.value() + "\",\"commands\":["
                        + "{\"uuid\":\"22222222-2222-2222-2222-222222222222\",\"command\":\"say hi\",\"playerNickname\":\"Notch\"},"
                        + "{\"uuid\":\"33333333-3333-3333-3333-333333333333\",\"command\":\"broadcast hi\",\"playerNickname\":null}"
                        + "],\"leaseExpiresAt\":\"2026-08-23T19:00:00Z\"}"));

        final List<QueuedCommand> commands = client.claim(SERVER, 100);

        assertThat(commands).hasSize(2);
        assertThat(commands.get(0).player()).isPresent();
        assertThat(commands.get(0).requiresPlayerOnline()).isTrue();
        assertThat(commands.get(1).player()).isEmpty();
        assertThat(commands.get(1).requiresPlayerOnline()).isFalse();

        final RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/v1/servers/" + SERVER.value() + "/command-executions/claims");
        assertThat(request.getHeader("X-API-Key")).isEqualTo("pk_test");
        assertThat(request.getBody().readUtf8()).contains("\"max\":100");
    }

    @Test
    void reportsResultsToTheResultsEndpoint() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"accepted\":2,\"unknown\":0,\"ignored\":0}"));

        client.report(SERVER, Arrays.asList(
                CommandResult.completed(CommandId.of("22222222-2222-2222-2222-222222222222")),
                CommandResult.deferred(CommandId.of("33333333-3333-3333-3333-333333333333"))));

        final RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/v1/servers/" + SERVER.value() + "/command-executions/results");
        final String body = request.getBody().readUtf8();
        assertThat(body).contains("\"status\":\"completed\"");
        assertThat(body).contains("\"status\":\"deferred\"");
    }

    @Test
    void reportFailedIncludesErrorDetail() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));

        client.report(SERVER, java.util.Collections.singletonList(
                CommandResult.failed(CommandId.of("22222222-2222-2222-2222-222222222222"), "boom")));

        final String body = server.takeRequest().getBody().readUtf8();
        assertThat(body).contains("\"status\":\"failed\"");
        assertThat(body).contains("\"error\":\"boom\"");
    }

    @Test
    void emptyReportMakesNoRequest() {
        client.report(SERVER, java.util.Collections.emptyList());

        assertThat(server.getRequestCount()).isZero();
    }

    @Test
    void throwsOnServerError() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.claim(SERVER, 100)).isInstanceOf(ApiClientException.class);
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
