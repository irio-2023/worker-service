package pl.mimuw.worker.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtils {

    public static String currentDate() {
        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public static Date getExpiresAt() {
        return Date.from(Instant.now().plus(8, java.time.temporal.ChronoUnit.HOURS));
    }

    public static long currentTimeSecs() {
        return System.currentTimeMillis() / 1000L;
    }

    public static long currentTimeSecsPlus(final long secs) {
        return currentTimeSecs() + secs;
    }
}
