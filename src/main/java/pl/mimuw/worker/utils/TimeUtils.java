package pl.mimuw.worker.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String currentDate() {
        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public static int currentTimeSecs() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static int currentTimeSecsPlus(final int secs) {
        return currentTimeSecs() + secs;
    }
}
