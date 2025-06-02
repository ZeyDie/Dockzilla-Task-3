package pro.doczilla.server.api.open_meteo.configurations;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class GeoConfig {
    @Getter
    private static final @NotNull String apiContextGeolocation = "https://geocoding-api.open-meteo.com/v1/search?name={city}";
    @Getter
    private static final @NotNull String apiContextGeoTemperature2MOfLatLong = "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&hourly=temperature_2m";
}