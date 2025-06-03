package pro.doczilla.common.api.data.fileserver;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

public record AuthRequestData(
        @NotNull String username,
        @NotNull String password
) {
}