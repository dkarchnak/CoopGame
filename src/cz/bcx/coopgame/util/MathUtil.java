package cz.bcx.coopgame.util;

/**
 * Created by BCX on 7/26/2016.
 */
public class MathUtil {
    public static final float clamp(float value) {
        return clamp(value, 0f, 1f);
    }

    public static final float clamp(float value, float min, float max) {
        return value > max ? max : value < min ? min : value;
    }
}
