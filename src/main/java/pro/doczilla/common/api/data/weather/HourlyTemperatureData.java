package pro.doczilla.common.api.data.weather;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@Builder
public class HourlyTemperatureData {
    @Builder.Default
    private @NotNull String city = "UNKNOWN";
    @Builder.Default
    private @Nullable String country = "UNKNOWN";
    @Builder.Default
    private @NotNull Map<String, Double> temperatures = Maps.newHashMap();
}