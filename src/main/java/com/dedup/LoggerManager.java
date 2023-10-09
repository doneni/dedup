package com.dedup;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerManager {
    private static final Logger logger = Logger.getLogger(LoggerManager.class.getName());
    private static boolean loggingEnabled = true;

    static {
        try
        {
            logger.setUseParentHandlers(false);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("application.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error initializing logger", e);
        }
    }

    public static void logInfo(String message)
    {
        if (loggingEnabled)
            logger.info(message);
    }

    public static void logWarning(String message)
    {
        if (loggingEnabled)
            logger.warning(message);
    }

    public static void logError(String message, Throwable throwable)
    {
        if (loggingEnabled)
            logger.log(Level.SEVERE, message, throwable);
    }

    public static void setLoggingEnabled(boolean enabled)
    {
        loggingEnabled = enabled;
    }
}