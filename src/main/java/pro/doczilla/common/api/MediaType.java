package pro.doczilla.common.api;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public enum MediaType {
    JSON("application/json");

    private @NotNull String type;

    MediaType(@NonNull final String type) {
        this.type = type;
    }
}
