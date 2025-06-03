package pro.doczilla.common;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import pro.doczilla.common.api.MediaType;
import pro.doczilla.common.api.interfaces.ILaunch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class CommonLaunch implements ILaunch {
    @Getter
    private static final @NotNull CommonLaunch instance = new CommonLaunch();

    @Getter
    private static final int port = 8090;

    @Getter
    private static final @NotNull HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public static @NotNull HttpRequest.Builder getHttpRequestBuilder(@NonNull final URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .headers("Content-Type", MediaType.JSON.name())
                .headers("Accept", MediaType.JSON.name());
    }

    @Override
    public void stop() {

    }

    @Override
    public void init() {

    }
}