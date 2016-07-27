package cz.bcx.coopgame.application;

/**
 * Created by BCX on 7/26/2016.
 */
public class Log {
    public static final void debug(Class clazz, String message) {
        System.out.println("[" + clazz.getSimpleName() + "]: " + message);
    }

    public static final void debug(Class clazz, Object message) {
        debug(clazz, message.toString());
    }
}