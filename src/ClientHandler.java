package online.server;

import chessgame.Board;
import chessgame.Piece;
import chessgame.PieceColor;
import chessgame.Player;
import util.Coord2d;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread{
    Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    GameServer server;

    private Board board;
    PieceColor pieceColor = PieceColor.UNSET;

    public ClientHandler(GameServer serv, Socket socket, Board board, PieceColor color)throws Exception{

        setServer(serv);

        setClient(socket);

        setBoard(board);

        setPieceColor(color);



        output = new ObjectOutputStream(getClient().getOutputStream());
        output.flush();
        input = new ObjectInputStream(getClient().getInputStream());

        //receiveBoard = new Askboarding(this);

    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public PieceColor getPieceColor() {
        return pieceColor;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public void run() {
        try {
            while(true){
                /*output.writeObject(getBoard().getLogic());
                output.flush();*/

                System.out.println("BEGIN");
                String req = input.readUTF();
                System.out.println("CLIENT");
                System.out.println(req);

                translateRequest(req);
                System.out.println("END");
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void translateRequest(String request) throws Exception {
        /*   ohatra: SELECT//1//2/2//3/2   */
        boolean flag = false;

        String req = request;
        output.reset();
        if(req.startsWith("SELECT")){

            String[] splitted = req.split("//");

            Player player = getBoard().getPlayerByColor(Integer.parseInt(splitted[1]));

            int[] pieceCoord = parseInteger(splitted[2].split("/"));        /// Coordonnées anle piece
            int[] targetCoord = parseInteger(splitted[3].split("/"));       /// Coordonnées tianle piece hietsehana

            Piece movedPiece = board.getLogic().getPiece(pieceCoord[0], pieceCoord[1]);
            Coord2d target = new Coord2d(targetCoord[0], targetCoord[1]);

            try{
                board.turnMove(player, movedPiece, target);
                board.initPieceCoord();

            }catch (Exception exception){
                System.out.println(exception);
                flag = true;
            }

            output.reset();
                if(!flag){
                    writeUTFtoAll("CHESS-BOARD");
                    writeToAll(getBoard().getLogic());
                    output.reset();
                }
                flag = false;

        }
    }

    public void writeToAll(Object o) throws Exception {
        for (int i = 0; i < getServer().getClients().size(); i++) {
            getServer().getClients().get(i).getOutput().writeObject(o);
            System.out.println("for client: N"+i + " packet sent ! ");
            output.flush();
        }
    }
    public void writeUTFtoAll(String s) throws Exception {
        for (int i = 0; i < getServer().getClients().size(); i++) {
            getServer().getClients().get(i).getOutput().writeUTF(s);
        }
        output.flush();
    }

    public int[] parseInteger(String... strings){
        int[] val = new int[strings.length];

        for (int i = 0; i < strings.length; i++) {
            val[i] = Integer.parseInt(strings[i]);
        }

        return val;
    }

    public GameServer getServer() {
        return server;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }
}
