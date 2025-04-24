package org.ravry.utilities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";

    public enum LOG_STATE {
        DEFAULT_LOG,
        WARNING_LOG,
        ERROR_LOG
    }

    public static void LOG(LOG_STATE logState, String message) {
        LocalTime now = LocalTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2];
        String callClass = caller.getClassName();
        int callLine = caller.getLineNumber();

        switch(logState) {
            case DEFAULT_LOG -> {
                System.out.printf("[%s | %s:%d] %s\n", timeStr, callClass, callLine, message);
                System.out.flush();
            }
            case WARNING_LOG -> {
                System.out.printf(YELLOW + "[%s | %s:%d] %s\n" + RESET, timeStr, callClass, callLine, message);
                System.out.flush();
            }
            case ERROR_LOG -> {
                System.out.printf(RED + "[%s | %s:%d] %s\n" + RESET, timeStr, callClass, callLine, message);
                System.out.flush();
            }
        }
    }
}
