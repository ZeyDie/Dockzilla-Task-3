package pro.doczilla.common.api.data;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@Builder
public class HourlyTemperatureData {
    private @NotNull String city;
    private @Nullable String country;
    @Builder.Default
    private @NotNull Map<String, Double> temperatures = Maps.newHashMap();
}