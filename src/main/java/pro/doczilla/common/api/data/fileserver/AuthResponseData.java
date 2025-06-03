package pro.doczilla.common.api.data.fileserver;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public final class AuthResponseData {
    private @NotNull String token;
}