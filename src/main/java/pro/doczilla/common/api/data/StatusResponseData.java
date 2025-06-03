package pro.doczilla.common.api.data;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public final class StatusResponseData {
    private boolean success;
    private @NotNull String message;
}