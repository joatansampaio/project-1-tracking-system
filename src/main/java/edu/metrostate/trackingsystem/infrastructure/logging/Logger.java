package edu.metrostate.trackingsystem.infrastructure.logging;

/**
 * We could have a LoggerFactory, but this is way too much for such small need.
 */
public class Logger {

    private final String className;

    private Logger() {
        this.className = this.getClass().getName();
    }

    public static Logger getLogger() {
        return new Logger();
    }

    public void info(String msg) {
        System.out.println("[" + className + "] : " + msg);
    }

    public void error(String msg) {
        System.err.println("[" + className + "] : " + msg);
    }
}