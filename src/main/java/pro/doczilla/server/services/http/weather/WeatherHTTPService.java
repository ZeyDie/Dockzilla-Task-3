package pro.doczilla.server.services.http.weather;

import com.google.common.cache.*;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.sun.net.httpserver.HttpExchange;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.doczilla.common.CommonLaunch;
import pro.doczilla.common.api.data.HourlyTemperatureData;
import pro.doczilla.common.api.utils.LoggerUtil;
import pro.doczilla.server.api.open_meteo.data.ApiResponseData;
import pro.doczilla.server.api.open_meteo.data.GeoLocationData;
import pro.doczilla.server.api.open_meteo.data.TemperatureGeoLocationData;
import pro.doczilla.server.api.open_meteo.configurations.GeoConfig;
import pro.doczilla.server.services.http.HTTPService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class WeatherHTTPService extends HTTPService {
    @Getter
    private static final @NotNull WeatherHTTPService instance = new WeatherHTTPService();

    @Getter
    private final @NotNull String pathContext = "/weather";

    private final @NotNull LoadingCache<String, HourlyTemperatureData> weatherCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(15, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, HourlyTemperatureData>() {
                        @Override
                        public @NotNull HourlyTemperatureData load(@NonNull final String city) throws Exception {
                            LoggerUtil.info("Updating temperature data for " + city);

                            return getHourlyTemperatureData(city);
                        }
                    }
            );

    @SneakyThrows
    @Override
    protected boolean success(@NonNull final HttpExchange exchange) {
        @NotNull val getBody = super.readGetBody(exchange);

        @Nullable val city = getBody.get("city");
        @Nullable val country = getBody.get("country");

        LoggerUtil.info("Weather GET: " + getBody);

        if (city == null) return false;

        @Nullable val presented = this.weatherCache.get(city);

        if (presented != null) {
            LoggerUtil.info("Weather presented " + this.getGson().toJson(presented));
            super.sendResponseJson(exchange, this.getGson().toJson(presented));
            return true;
        }

        return false;
    }

    private @NotNull HourlyTemperatureData getHourlyTemperatureData(@NotNull final String city) {
        try {
            @Nullable val responseGeolocation = CommonLaunch.getHttpClient()
                    .send(
                            CommonLaunch.getHttpRequestBuilder(new URL(GeoConfig.getApiContextGeolocation().replace("{city}", city)).toURI()).build(),
                            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                    ).body();

            if (responseGeolocation == null) throw new NullPointerException("Response geolocation is null");

            @NotNull val dataApiResponse = this.getGson().fromJson(responseGeolocation, ApiResponseData.class);

            @Nullable val results = dataApiResponse.getResults();

            if (results == null) throw new NullPointerException("Results of api resonse is null");

            @Nullable val dataGeoLocation = results
                    .stream()
                    .map(o -> this.getGson().fromJson(this.getGson().toJson(o), GeoLocationData.class))
                    .findFirst()
                    .orElse(null);

            if (dataGeoLocation == null) throw new NullPointerException("Data of Geolocation is null");

            @Nullable val responseGeoTemperature = CommonLaunch.getHttpClient()
                    .send(
                            CommonLaunch.getHttpRequestBuilder(
                                    new URL(
                                            GeoConfig.getApiContextGeoTemperature2MOfLatLong()
                                                    .replace("{lat}", String.valueOf(dataGeoLocation.getLatitude()))
                                                    .replace("{lon}", String.valueOf(dataGeoLocation.getLongitude()))
                                    ).toURI()
                            ).build(),
                            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                    ).body();

            if (responseGeoTemperature == null)
                throw new NullPointerException("Response temperature of Geolocation is null");

            @NotNull val dataTemperatureGeoLocation = this.getGson().fromJson(responseGeoTemperature, TemperatureGeoLocationData.class);
            @NotNull val dataHourly = dataTemperatureGeoLocation.getHourly();

            @NotNull val temperatures = Maps.<String, Double>newHashMap();

            for (int i = 0; i < dataHourly.getTime().length; i++) {
                @NotNull val date = dataHourly.getTime()[i];
                val temperature = dataHourly.getTemperature_2m()[i];

                temperatures.put(date, temperature);
            }

            return HourlyTemperatureData.builder()
                    .city(city)
                    .country(dataGeoLocation.getCountry())
                    .temperatures(temperatures)
                    .build();
        } catch (
                final URISyntaxException | IOException | InterruptedException | NullPointerException exception
        ) {
            exception.printStackTrace();

            return HourlyTemperatureData.builder().build();
        }
    }
}