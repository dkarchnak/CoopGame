package cz.bcx.coopgame.application.game;

import org.joml.Vector3f;

/**
 * Created by bcx on 3/26/17.
 */
public class Camera {
    private Vector3f cameraPosition;

    private int width, height;

    public Camera(int width, int height) {
        this(width, height, new Vector3f());
    }

    public Camera(int width, int height, Vector3f cameraPosition) {
        this.width = width;
        this.height = height;
        this.cameraPosition = cameraPosition;
    }

    public void setCameraDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setCameraPosition(Vector3f cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public void setCameraPositionByCenter(Vector3f cameraCenterPosition) {
        this.cameraPosition.set(cameraCenterPosition.x - width/2f, cameraCenterPosition.y - height/2f, cameraCenterPosition.z);
    }

    public Vector3f getCameraPosition() {
        return cameraPosition;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}