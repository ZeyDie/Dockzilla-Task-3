package pro.doczilla.server.api.fileserver.auth;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder
public final class UserData {
    private @NotNull String name;
    private @NotNull String password;
    private @Nullable String token;
}