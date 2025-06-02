package pro.doczilla.server.api.open_meteo.data;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiResponseData {
    private final @Nullable List<?> results = new ArrayList<>();
    private final double generationtime_ms;
}
