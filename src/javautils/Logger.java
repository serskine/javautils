package javautils;

import java.io.PrintStream;

public class Logger {
    private static PrintStream INFO = System.out;
    private static PrintStream WARN = System.err;
    private static PrintStream ERROR = System.err;


    public static final void info(final String message) {
        INFO.println(message);
    }

    public static final void warn(final String message) {
        WARN.println(message);
    }

    public static final void warn(final String message, final Throwable t) {
        WARN.println(message);
        if (t != null) {
            t.printStackTrace(WARN);
        }
    }

    public static final void err(final String message, final Throwable t) {
        ERROR.println(message);
        if (t != null) {
            t.printStackTrace(ERROR);
        }
    }

}
