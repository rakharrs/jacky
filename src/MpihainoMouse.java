package util;

import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;

public class MpihainoMouse {
    public static Coord2d getMousePos(long window) {
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);

        GLFW.glfwGetCursorPos(window, xBuffer, yBuffer);

        double x = xBuffer.get(0);
        double y = yBuffer.get(0);

        return new Coord2d(x, y);
    }
}
