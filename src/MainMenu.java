package chessgame;

import org.lwjgl.opengl.GL46;

import util.Scene;
import util.Texture;
import util.UI;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

import util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class MainMenu extends Scene{
    Texture Background;
    private UI UiMenu;

    public MainMenu(Texture background, UI uimenu){
        setBackground(background);
        setUiMenu(uimenu);
    }

    public Texture getBackground() {
        return Background;
    }

    public void setBackground(Texture background) {
        Background = background;
    }

    public UI getUiMenu() {
        return UiMenu;
    }

    public void setUiMenu(UI uiMenu) {
        UiMenu = uiMenu;
    }

    @Override
    public void update(long window) {
        GL46.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GL46.glLoadIdentity();
        getBackground().draw(0,0,600,600);

        glfwSwapBuffers(window);
        glfwPollEvents();

        GL46.glFlush();
    }

}
