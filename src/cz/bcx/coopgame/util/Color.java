package cz.bcx.coopgame.util;

/**
 * Created by bcx on 9.5.16.
 */
public class Color {
    public static final Color WHITE   = new Color(1f, 1f, 1f, 1f);
    public static final Color BLACK   = new Color(0f, 0f, 0f, 1f);
    public static final Color RED     = new Color(1f, 0f, 0f, 1f);
    public static final Color GREEN   = new Color(0f, 1f, 0f, 1f);
    public static final Color BLUE    = new Color(0f, 0f, 1f, 1f);

    public float r, g, b, a;

    public Color() {
        this(1f, 1f, 1f, 1f);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1f);
    }

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}