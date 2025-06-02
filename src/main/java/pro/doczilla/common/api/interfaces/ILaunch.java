package pro.doczilla.common.api.interfaces;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.doczilla.common.api.utils.LoggerUtil;

public interface ILaunch extends IInitialize {
    @NotNull
    default String getName() {
        return this.getClass().getSimpleName();
    }

    default void launch(@Nullable final String[] args) {
        val startTime = System.currentTimeMillis();

        this.preInit();
        this.init();
        this.postInit();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        LoggerUtil.info("Launched! (%f sec.)", ((System.currentTimeMillis() - startTime) / 1000.0));
    }

    void stop();
}