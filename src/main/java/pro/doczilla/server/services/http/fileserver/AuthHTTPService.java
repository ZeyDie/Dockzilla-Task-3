package pro.doczilla.server.services.http.fileserver;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.doczilla.common.api.data.fileserver.AuthRequestData;
import pro.doczilla.common.api.data.fileserver.AuthResponseData;
import pro.doczilla.server.api.fileserver.auth.UserData;
import pro.doczilla.server.services.http.HTTPService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;

public final class AuthHTTPService extends HTTPService {
    @Getter
    private static final @NotNull AuthHTTPService instance = new AuthHTTPService();

    @Getter
    private final @NotNull String pathContext = "/auth";

    private final @NotNull Path users = this.getData().resolve("users");

    private final @NotNull Map<String, UUID> usernameToUUID = Maps.newHashMap();
    private final @NotNull Cache<UUID, UserData> uuidUserDataCache = CacheBuilder.newBuilder().build();

    @Override
    public void preInit() {
        @NonNull val usersFile = this.users.toFile();

        if (!usersFile.exists())
            usersFile.mkdirs();
    }

    @SneakyThrows
    @Override
    public void init() {
        @NonNull val uuid = UUID.fromString("7c46a53d-a09e-4d5b-8ca9-4f0f6637dca2");
        @NonNull val testUser = UserData.builder()
                .name("Test")
                .password("qwerty")
                .build();

        @NonNull val userData = this.users.resolve(uuid + ".json").toFile();

        if (!userData.exists()) {
            userData.getParentFile().mkdirs();
            userData.createNewFile();
        }

        Files.write(
                userData.toPath(),
                this.getGson().toJson(testUser).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.WRITE
        );
    }

    @SneakyThrows
    @Override
    public void postInit() {
        Files.walk(this.users)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().endsWith(".json"))
                .forEach(path -> {
                            @NotNull val uuid = UUID.fromString(path.getFileName().toString().replace(".json", ""));
                            @NotNull val userData = getGson().fromJson(path.toFile().getPath(), UserData.class);

                            this.usernameToUUID.put(
                                    userData.getName(),
                                    uuid
                            );
                            this.uuidUserDataCache.put(
                                    uuid,
                                    userData
                            );
                        }
                );
    }

    @Override
    protected boolean success(@NonNull final HttpExchange exchange) {
        @NonNull val body = super.readPostBody(exchange);

        if (body.isEmpty()) {
            super.sendResponseEmpty(exchange);
            return true;
        }

        @NonNull val authResponse = this.getGson().fromJson(body, AuthRequestData.class);

        @NonNull val username = authResponse.username();
        @NonNull val password = authResponse.password();

        @Nullable val uuid = this.usernameToUUID.get(username);

        if (uuid == null) {
            super.sendResponseBad(exchange, username + " no exist!");
            return true;
        }

        @Nullable val userData = this.uuidUserDataCache.getIfPresent(uuid);

        if (userData == null) {
            super.sendResponseBad(exchange, uuid + " no exist!");
            return true;
        }

        userData.setToken(UUID.randomUUID().toString());

        super.sendResponseJson(
                exchange,
                this.getGson().toJson(
                        AuthResponseData.builder()
                                .token(userData.getToken())
                                .build()
                )
        );

        return true;
    }
}
