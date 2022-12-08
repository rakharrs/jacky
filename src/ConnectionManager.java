package online;

import chessgame.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager extends Thread{
    boolean host;
    Socket client;
    String packet = new String();
    ServerSocket listener = null;
    Player player;
    DataInputStream din;
    DataOutputStream dout;

    public ConnectionManager(boolean host){
        setHost(host);
        if(isHost()){
            ServerSocket listener = null;
            try {
                listener = new ServerSocket(7243);
                System.out.println("ok");
                setListener(listener);
                //this.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            Socket client = null;
            try {

                client = new Socket("localhost",7243);
                setClient(client);
                setDataIO();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void run(){
        if(isHost()){
            while(true){
                try {
                    Socket client = getListener().accept();
                    setClient(client);
                    setDataIO();

                    getDin().readUTF();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            try {
                client = new Socket("localhost",7243);
                setClient(client);
                setDataIO();


                getDout().writeUTF("jean");
                getDout().flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public void setDataIO() throws IOException{
            DataInputStream input = new DataInputStream(getClient().getInputStream());
            DataOutputStream output = new DataOutputStream(getClient().getOutputStream());

            setDin(input);
            setDout(output);

    }

    public ServerSocket getListener() {
        return listener;
    }

    public void setListener(ServerSocket listener) {
        this.listener = listener;
    }

    public boolean isHost() {
        return host;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public DataInputStream getDin() {
        return din;
    }

    public void setDin(DataInputStream din) {
        this.din = din;
    }

    public DataOutputStream getDout() {
        return dout;
    }

    public void setDout(DataOutputStream dout) {
        this.dout = dout;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
