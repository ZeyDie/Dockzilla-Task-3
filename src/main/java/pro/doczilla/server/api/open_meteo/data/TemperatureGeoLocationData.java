package pro.doczilla.server.api.open_meteo.data;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class TemperatureGeoLocationData {
    private final @NotNull HourlyGeoLocationData hourly;
}