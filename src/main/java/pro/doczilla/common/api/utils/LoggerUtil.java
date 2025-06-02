package pro.doczilla.common.api.utils;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;

@Log
public final class LoggerUtil {
    public static void info(
            @NonNull String message,
            @Nullable final Object... args
    ) {
        info(null, message, args);
    }

    public static void info(
            @Nullable final Class<?> clazz,
            @NonNull String message,
            @Nullable final Object... args
    ) {
        log.info(String.format(classMessage(clazz, message), args));
    }

    public static void debug(
            @NonNull String message,
            @Nullable final Object... args
    ) {
        debug(null, message, args);
    }

    public static void debug(
            @Nullable final Class<?> clazz,
            @NonNull String message,
            @Nullable final Object... args
    ) {
        log.fine(String.format(classMessage(clazz, message), args));
    }

    public static void warn(
            @NonNull String message,
            @Nullable final Object... args
    ) {
        warn(null, message, args);
    }

    public static void warn(
            @Nullable final Class<?> clazz,
            @NonNull String message,
            @Nullable final Object... args
    ) {
        log.warning(String.format(classMessage(clazz, message), args));
    }

    private static String classMessage(
            @Nullable final Class<?> clazz,
            @NonNull String message
    ) {
        return clazz != null ? String.format("[%s]: %s", clazz.getSimpleName(), message) : message;
    }
}
