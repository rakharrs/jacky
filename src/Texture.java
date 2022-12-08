package util;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.stb.STBImage;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL46.*;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Texture implements Serializable {
    private int textID;
    private int width;
    private int height;
    private int channel;
    private String imgPath;
    boolean initiated = false;

    private int X;
    private int Y;

    public Texture(String imgPath){
        setImgPath(imgPath);

        //initTexture();
    }

    public void initTexture(){
        if(imgPath.equals("") || isInitiated()){
            //System.out.println(imgPath);
            return;
        }

        setTextID(glGenTextures());

        //Set Textures parameters
        /*glTextureParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTextureParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        //When stretching
        glTextureParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTextureParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);*/

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channel = stack.mallocInt(1);

            ByteBuffer image = stbi_load(getImgPath(), w, h, channel, 0);
            setWidth(w.get(0));
            setHeight(h.get(0));
            if(!stbi_info(getImgPath(),w,h,channel)){
                throw new RuntimeException("image info didn't load");
            }
            if(image == null){
                throw  new RuntimeException("Image didn't load");
            }
            makeTexture(image);
        }
        initiated = true;
    }

    public void makeTexture(ByteBuffer image){
        glBindTexture(GL_TEXTURE_2D, getTextID());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        int format;
        if(getChannel()==3){
            format = GL_RGB;
        }else{
            GL46.glEnable(GL_BLEND);
            GL46.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            format = GL_RGBA;

        }
        GL46.glTexImage2D(GL_TEXTURE_2D, 0, format, getWidth(), getHeight(), 0, format, GL_UNSIGNED_BYTE, image);
        stbi_image_free(image);
    }

    public void draw(int x, int y, int width, int height){
        glColor4f(1.0f, 1.0f, 1.0f, 1f);
        GL46.glBindTexture(GL_TEXTURE_2D, getTextID());

        GL46.glEnable(GL_TEXTURE_2D);
        GL46.glBegin(GL_QUADS);

            GL46.glTexCoord2f(1.0f, 1.0f);
            GL46.glVertex2f(x, y);

            GL46.glTexCoord2f(0.0f, 1.0f);
            GL46.glVertex2f(x + width, y);

            GL46.glTexCoord2f(0.0f, 0.0f);
            GL46.glVertex2f(x + width, y + height);

            GL46.glTexCoord2f(1.0f, 0.0f);
            GL46.glVertex2f(x, y + height);

        GL46.glEnd();
        GL46.glDisable(GL_TEXTURE_2D);

    }
    public void draw(int width, int height){
        draw(getX(), getY(), width, height);

    }

    public int getChannel() {
        return channel;
    }

    public int getHeight() {
        return height;
    }

    public int getTextID() {
        return textID;
    }

    public int getWidth() {
        return width;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setTextID(int textID) {
        this.textID = textID;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public boolean isInitiated() {
        return initiated;
    }

    public void setInitiated(boolean initiated) {
        this.initiated = initiated;
    }
}
