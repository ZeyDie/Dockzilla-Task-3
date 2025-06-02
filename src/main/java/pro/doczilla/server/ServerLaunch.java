package pro.doczilla.server;

import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.doczilla.common.CommonLaunch;
import pro.doczilla.common.api.interfaces.IInitialize;
import pro.doczilla.common.api.utils.LoggerUtil;
import pro.doczilla.server.services.http.HTTPService;
import pro.doczilla.server.services.http.weather.WeatherHTTPService;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ServerLaunch extends CommonLaunch {
    @Getter
    private static final @NotNull ServerLaunch instance = new ServerLaunch();

    private final @NotNull List<HTTPService> httpServices = new ArrayList<>(
            Collections.singleton(new WeatherHTTPService())
    );

    @Getter
    private HttpServer httpServer;

    public static void main(@Nullable final String[] args) {
        instance.launch(args);
    }

    @Override
    public void launch(@Nullable final String[] args) {
        super.launch(args);
    }

    @Override
    public void preInit() {
        try {
            val port = CommonLaunch.getPort();

            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            LoggerUtil.info("Server started on port: %d", port);
        } catch (final Exception exception) {
            exception.printStackTrace();
            System.exit(-1);
        }

        this.httpServices.forEach(IInitialize::preInit);
    }

    @Override
    public void init() {
        this.httpServices.forEach(HTTPService::init);
    }

    @Override
    public void postInit() {
        this.httpServices.forEach(IInitialize::postInit);

        this.httpServer.start();
    }

    @Override
    public void stop() {
        LoggerUtil.info("Server was stopped!");
    }
}