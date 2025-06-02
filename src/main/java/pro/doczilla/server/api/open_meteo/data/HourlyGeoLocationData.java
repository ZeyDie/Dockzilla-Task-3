package pro.doczilla.server.api.open_meteo.data;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class HourlyGeoLocationData {
    private final @NotNull String[] time = new String[0];
    private final double[] temperature_2m = new double[0];
}
