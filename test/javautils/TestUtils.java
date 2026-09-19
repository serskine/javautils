package javautils;

import java.util.Date;

public class TestUtils {
    public static long time(String title, Runnable runnable) {
        final Date start = new Date();
        Date end;
        try {
            runnable.run();
        } catch (Exception e) {
            Logger.err(e.getMessage(), e);
        } finally {
            end = new Date();
        }
        final long timeDiff = end.getTime() - start.getTime();
        Logger.info(String.format("%s: timeDiff = %d", title, timeDiff));
        return timeDiff;
    }
}
