package pl.pixtill.plugin.infrastructure.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.pixtill.plugin.domain.ApiKey;
import pl.pixtill.plugin.domain.ApiUrl;
import pl.pixtill.plugin.domain.CommandResult;
import pl.pixtill.plugin.domain.QueuedCommand;
import pl.pixtill.plugin.domain.ServerUuid;
import pl.pixtill.plugin.infrastructure.http.dto.ClaimedCommandDto;
import pl.pixtill.plugin.infrastructure.http.dto.CommandBatchDto;
import pl.pixtill.plugin.port.ApiClientException;
import pl.pixtill.plugin.port.PixtillApiClient;
import pl.pixtill.plugin.port.PixtillLogger;

public final class HttpPixtillApiClient implements PixtillApiClient {

    private static final MediaType JSON_LD = MediaType.parse("application/ld+json");

    private final OkHttpClient http;
    private final ApiUrl apiUrl;
    private final ApiKey apiKey;
    private final Map<String, String> telemetryHeaders;
    private final PixtillLogger logger;
    private final boolean debug;
    private final Gson gson;
    private final ClaimedCommandMapper mapper;

    public HttpPixtillApiClient(
            final OkHttpClient http,
            final ApiUrl apiUrl,
            final ApiKey apiKey,
            final Map<String, String> telemetryHeaders,
            final PixtillLogger logger,
            final boolean debug) {
        this.http = http;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.telemetryHeaders = telemetryHeaders;
        this.logger = logger;
        this.debug = debug;
        this.gson = new Gson();
        this.mapper = new ClaimedCommandMapper();
    }

    @Override
    public List<QueuedCommand> claim(final ServerUuid server, final int max) {
        final HttpUrl url = endpoint(server, "claims");
        final String requestBody = gson.toJson(Collections.singletonMap("max", max));
        final Request request = baseRequest(url).post(RequestBody.create(requestBody, JSON_LD)).build();

        if (debug) {
            logger.debug("POST " + url + " " + requestBody);
        }

        try (Response response = http.newCall(request).execute()) {
            final String body = response.body() != null ? response.body().string() : null;
            if (debug) {
                logger.debug("Response " + response.code() + ": " + body);
            }
            if (!response.isSuccessful()) {
                throw new ApiClientException("Unexpected HTTP status " + response.code() + " while claiming commands.");
            }
            if (body == null || body.isEmpty()) {
                return new ArrayList<>();
            }
            return parseBatch(body);
        } catch (final IOException exception) {
            throw new ApiClientException("Network error while claiming commands: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void report(final ServerUuid server, final List<CommandResult> results) {
        if (results.isEmpty()) {
            return;
        }
        final HttpUrl url = endpoint(server, "results");
        final String requestBody = buildResultsBody(results);
        final Request request = baseRequest(url).post(RequestBody.create(requestBody, JSON_LD)).build();

        if (debug) {
            logger.debug("POST " + url + " " + requestBody);
        }

        try (Response response = http.newCall(request).execute()) {
            if (debug) {
                logger.debug("Response " + response.code());
            }
            if (!response.isSuccessful()) {
                throw new ApiClientException("Unexpected HTTP status " + response.code() + " while reporting results.");
            }
        } catch (final IOException exception) {
            throw new ApiClientException("Network error while reporting results: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        http.dispatcher().executorService().shutdown();
        http.connectionPool().evictAll();
    }

    private List<QueuedCommand> parseBatch(final String body) {
        final CommandBatchDto batch;
        try {
            batch = gson.fromJson(body, CommandBatchDto.class);
        } catch (final JsonSyntaxException exception) {
            throw new ApiClientException("Malformed API response: " + exception.getMessage(), exception);
        }

        final List<QueuedCommand> commands = new ArrayList<>();
        for (final ClaimedCommandDto dto : batch.getCommands()) {
            try {
                commands.add(mapper.toDomain(dto));
            } catch (final RuntimeException exception) {
                logger.error("Skipping malformed command from API: " + exception.getMessage());
            }
        }
        return commands;
    }

    private String buildResultsBody(final List<CommandResult> results) {
        final List<Map<String, Object>> items = new ArrayList<>();
        for (final CommandResult result : results) {
            final Map<String, Object> item = new LinkedHashMap<>();
            item.put("uuid", result.id().value());
            item.put("status", result.status().wireValue());
            result.error().ifPresent(error -> item.put("error", error));
            items.add(item);
        }
        return gson.toJson(Collections.singletonMap("results", items));
    }

    private HttpUrl endpoint(final ServerUuid server, final String action) {
        final HttpUrl base = HttpUrl.parse(apiUrl.value());
        if (base == null) {
            throw new ApiClientException("Invalid API URL: " + apiUrl.value());
        }
        return base.newBuilder()
                .addPathSegment("servers")
                .addPathSegment(server.value())
                .addPathSegment("command-executions")
                .addPathSegment(action)
                .build();
    }

    private Request.Builder baseRequest(final HttpUrl url) {
        final Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Accept", "application/ld+json")
                .header("X-API-Key", apiKey.value());
        for (final Map.Entry<String, String> header : telemetryHeaders.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }
        return builder;
    }
}
