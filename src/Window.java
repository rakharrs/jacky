package chessengine;


import chessgame.Board;
import chessgame.MainMenu;
import online.server.GameServer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;


import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;
import util.*;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long window;
    private Scene scene;

    private Scene menu;

    private Scene[] scenes;
    private final int width = 600;
    private final int height = 600;
    private int current = 0;

    private Window() throws Exception {
        init();
        setMenu(new MainMenu(new Texture("assets/img/menu/chessimg.png"), new UI()));
        setScene(new Board(width/8, height/8,false,null));
        scenes = new Scene[2];
        scenes[0] = getMenu();
        scenes[1] = getScene();
        loop();
    }

    private Window(boolean online, String hostip) throws Exception {
        init();
        MainMenu mainmenu = new MainMenu(new Texture("assets/img/menu/chessimg.png"), new UI());
        mainmenu.getBackground().initTexture();
        setMenu(mainmenu);
        setScene(new Board(width/8, height/8,online,hostip));
        scenes = new Scene[2];
        scenes[0] = getMenu();
        scenes[1] = getScene();
        loop();
    }

    public static Window createWindow() throws Exception {
        return new Window();
    }
    public static Window createWindow(boolean online, String hostip) throws Exception {
        return new Window(online, hostip);
    }
    public static Window createWindow(boolean online, boolean host, String hostip) throws Exception {
        if(host){
            GameServer serv = new GameServer();
            Thread srv= new Thread(serv);
            srv.start();
            return new Window(online, "localhost");
        }
        return new Window(online, hostip);
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //ss the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

        // Create the window
        setWindow(glfwCreateWindow(width, height, "Chess", NULL, NULL));
        if ( getWindow() == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(getWindow(), (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ENTER && action == GLFW_RELEASE ){
                if(current == 1){
                    current = 0;
                }

                if(current == 0){
                    current = 1;
                }

            }
                //glfwSetWindowShouldClose(getWindow(), true); // We will detect this in the rendering loop

        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        GLCapabilities capabilities = GL.createCapabilities();

        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.


        // Set the clear color
        GL46.glClearColor(0.3f, 0.3f, 0.3f,1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        IntBuffer width = null;
        IntBuffer height = null;
        try ( MemoryStack stack = stackPush() ) {
            width = stack.mallocInt(1); // int*
            height = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, width, height);
            //glfwGetFramebufferSize(getWindow(),width,height);
        }

        //setup the size of the display to be drawn into.
        //GL46.glViewport(0, 0, 600, 600);
        //following statements define the camera model (camera lens ) to be used.
        GL46.glMatrixMode(GL46.GL_PROJECTION);
        GL46.glLoadIdentity();
        GL46.glOrtho(0, width.get(0), 0, height.get(0), -1, 1);

        //following statements define the location and rotation of object in the world.
        GL46.glMatrixMode(GL46.GL_MODELVIEW);
        GL46.glLoadIdentity();
        GL46.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        //Texture test = new Texture("assets/img/pieces/bB.png");

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(!glfwWindowShouldClose(getWindow())) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {

                scenes[current].update(getWindow());
                delta--;
            }
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
    }

    public void setWindow(long window) {
        this.window = window;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public long getWindow() {
        return window;
    }

    public Scene getScene() {
        return scene;
    }
    public Scene getMenu() {
        return menu;
    }

    public void setMenu(Scene menu) {
        this.menu = menu;
    }

}
