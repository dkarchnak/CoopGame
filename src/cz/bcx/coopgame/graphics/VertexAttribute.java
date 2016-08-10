package cz.bcx.coopgame.graphics;

import org.lwjgl.opengl.GL20;

/**
 * Created by BCX on 7/19/2016.
 */
public class VertexAttribute {
    private int location;
    private String name;

    public VertexAttribute(int location, String name) {
        this.location = location;
        this.name = name;
    }

    public void bindAttributeLocation(int shaderProgramId) {
        GL20.glBindAttribLocation(shaderProgramId, location, name);
    }

    public void enableVertexAttribute() {
        GL20.glEnableVertexAttribArray(location);
    }

    public void disableVertexAttribute() {
        GL20.glDisableVertexAttribArray(location);
    }

    public int getLocation() {
        return location;
    }

    public String getName() {
            return name;
        }
}