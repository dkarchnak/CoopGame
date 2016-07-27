package cz.bcx.coopgame.application;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    public static String WINDOW_TITLE = "Hello world!";
    public static int    WIDTH        = 1280;
    public static int    HEIGHT       = (int) (WIDTH / 16f * 9f);

    //Actual application
    private Application application;

    //We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;

    //The window handle
    private long window;

    public void run() {
        try {
            init();
            loop();
            destroy();

            //Release window and window callbacks
            glfwDestroyWindow(window);
        } finally {
            //Terminate GLFW and release callbacks
            glfwTerminate();
            keyCallback.release();
            errorCallback.release();
        }
    }
    private void init() {
        //Setup an error callback. The default implementation
        //will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        //Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GLFW_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        //Configure our window
        glfwDefaultWindowHints(); //Optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //The window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); //The window will be resizable

        //Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, WINDOW_TITLE, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        //Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GLFW_TRUE); // We will detect this in our rendering loop

                application.handleKeyboardEvent(key, action, mods);
            }
        });

        //Get the resolution of the primary monitor
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        //Center our window
        glfwSetWindowPos(
                window,
                (videoMode.width()  - WIDTH)  / 2,
                (videoMode.height() - HEIGHT) / 2
        );

        //Disables cursor
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        //Make the OpenGL context current
        glfwMakeContextCurrent(window);

        //Enable/Disable v-sync
        glfwSwapInterval(0);

        //Make the window visible
        glfwShowWindow(window);
    }
    private void loop() {
        GL.createCapabilities();

        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        this.application = new Application(WIDTH, HEIGHT);

        long startTime = System.currentTimeMillis();
        int fpsCounter = 0;

        while ( glfwWindowShouldClose(window) == GLFW_FALSE) {
            application.update(0.2f); //TODO
            application.draw();

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();

            if(System.currentTimeMillis() - startTime >= 1000) {
                startTime = System.currentTimeMillis();
                glfwSetWindowTitle(window, WINDOW_TITLE + " FPS:" + fpsCounter);
                fpsCounter = 0;
            }
            else fpsCounter++;
        }
    }

    private void destroy() {
        //Destroy all resources and rule the World!
    }

    public static void main(String[] args) {
        new Main().run();
    }
}