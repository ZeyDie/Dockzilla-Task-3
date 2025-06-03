package pro.doczilla.server.services.http;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.doczilla.common.api.MediaType;
import pro.doczilla.common.api.data.StatusResponseData;
import pro.doczilla.common.api.interfaces.IInitialize;
import pro.doczilla.common.api.utils.LoggerUtil;
import pro.doczilla.server.ServerLaunch;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public abstract class HTTPService implements IInitialize {
    @Getter
    private final @NotNull Path data = Path.of("data");

    @Getter
    private final @NotNull Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final @NotNull List<HttpExchange> exchanges = new ArrayList<>();

    private @NotNull Service service;

    public HTTPService() {
        this(Duration.ZERO, Duration.ofMillis(100));
    }

    public HTTPService(@NonNull final Duration period) {
        this(Duration.ZERO, period);
    }

    public HTTPService(
            @NonNull final Duration delay,
            @NonNull final Duration period
    ) {
        this.service = new AbstractScheduledService() {
            @Override
            protected void runOneIteration() {
                exchanges.removeIf(exchange -> success(exchange) || exchange.getResponseCode() == HTTP_OK);
            }

            @Override
            protected @NotNull Scheduler scheduler() {
                return Scheduler.newFixedDelaySchedule(delay, period);
            }
        }.startAsync();
    }

    @Override
    public void init() {
        @NonNull val context = this.getPathContext();

        ServerLaunch.getInstance()
                .getHttpServer()
                .createContext(
                        context,
                        this.exchanges::add
                );

        LoggerUtil.info(context);
    }

    protected abstract @NotNull String getPathContext();

    protected abstract boolean success(@NonNull final HttpExchange exchange);

    protected @NotNull String readPostBody(@NonNull final HttpExchange exchange) {
        @NonNull val stringBuilder = new StringBuilder();
        @NonNull val inputStream = exchange.getRequestBody();

        try {
            int i;

            while ((i = inputStream.read()) != -1)
                stringBuilder.append((char) i);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        @NonNull val string = stringBuilder.toString();

        LoggerUtil.debug(this.getClass(), string);

        return string;
    }

    protected @NotNull Map<String, String> readGetBody(@NonNull final HttpExchange exchange) {
        return this.parseQuery(exchange.getRequestURI().getQuery());
    }

    private @NotNull Map<String, String> parseQuery(@Nullable final String query) {
        @NotNull val params = new HashMap<String, String>();

        if (query == null) return params;

        @NotNull val pairs = query.split("&");

        for (@NotNull val pair : pairs) {
            val idx = pair.indexOf("=");

            if (idx > 0) {
                @NotNull val key = pair.substring(0, idx);
                @NotNull val value = pair.substring(idx + 1);

                params.put(key, value);
            }
        }

        return params;
    }

    protected void sendResponseEmpty(@NonNull final HttpExchange exchange) {
        this.sendResponseJson(
                exchange,
                this.gson.toJson(StatusResponseData
                        .builder()
                        .success(false)
                        .message("Empty Request")
                        .build()
                )
        );
    }

    protected void sendResponseBad(@NonNull final HttpExchange exchange, @NonNull final String message) {
        this.sendResponseJson(
                exchange,
                this.gson.toJson(StatusResponseData
                        .builder()
                        .success(false)
                        .message(message)
                        .build()
                )
        );
    }

    protected void sendResponseJson(
            @NonNull final HttpExchange exchange,
            @NonNull final String response
    ) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", MediaType.JSON.name());

        LoggerUtil.debug(this.getClass(), "Send Response: %s", response);

        this.sendResponse(exchange, response.getBytes());
    }

    protected void sendResponse(
            @NonNull final HttpExchange exchange,
            final byte[] bytes
    ) {
        try {
            exchange.sendResponseHeaders(HTTP_OK, bytes.length);

            try (@NotNull val output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}