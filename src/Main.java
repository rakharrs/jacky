import chessengine.Window;
import chessgame.PieceType;
import online.server.GameServer;

import java.awt.*;

public class Main extends Thread{
    public  boolean online;

    public Main(boolean Online){
        online = Online;
    }

    public static void main(String[] args) throws Exception {

        System.setProperty("sun.java2d.opengl", "true");

        Window wind = Window.createWindow(true,true,"localhost");

    }

    public void run(){

    }
}
