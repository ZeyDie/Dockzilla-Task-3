package pro.doczilla.server.api.open_meteo.data;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class GeoLocationData {
    private final int id;
    private final @NotNull String name;
    private final double latitude;
    private final double longitude;
    private final @NotNull String country;
}