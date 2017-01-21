package cz.bcx.coopgame.application;

/**
 * Created by BCX on 7/26/2016.
 */
public class Log {
    //TODO - Proper logging!
    public static final void error(Class clazz, String message, Throwable throwable) {
        error(clazz, message);
        throwable.printStackTrace();
    }

    public static final void error(Class clazz, String message) {
        System.err.print("[" + clazz.getSimpleName() + "]: " + message);
    }

    public static final void error(Class clazz, Object message) {
        error(clazz, message.toString());
    }

    public static final void debug(Class clazz, String message) {
        System.out.println("[" + clazz.getSimpleName() + "]: " + message);
    }

    public static final void debug(Class clazz, Object message) {
        debug(clazz, message.toString());
    }
}