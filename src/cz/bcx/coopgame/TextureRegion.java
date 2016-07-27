package cz.bcx.coopgame;

/**
 * Created by bcx on 5/14/16.
 */
public class TextureRegion {
    private Texture texture;

    //TODO - Store textureRegion width and height or calculate it in getters ?
    private float u1, v1, u2, v2;

    public TextureRegion(Texture texture, float u1, float v1, float u2, float v2) {
        this.texture = texture;

        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    public TextureRegion(Texture texture, int x, int y, int width, int height) {
        this(
            texture,
            x / (float)(texture.getWidth()),
            y / (float)(texture.getWidth()),
            (x + width) / (float)(texture.getWidth()),
            (y + height) / (float)(texture.getHeight())
        );
    }

    public Texture getTexture() {
        return texture;
    }

    public float getU1() {
        return u1;
    }

    public float getV1() {
        return v1;
    }

    public float getU2() {
        return u2;
    }

    public float getV2() {
        return v2;
    }
}