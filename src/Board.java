package chessgame;

import online.client.Client;
import online.client.Receiver;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import util.Coord2d;
import util.MpihainoMouse;
import util.Scene;
import util.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Board extends Scene implements Serializable {
    chessLogic logic;

    private boolean online;
    private boolean shouldInit = false;
    private Coord2d selectedPiece = null;
    //Texture background = new Texture("assets/img/menu/chessimg.png");

    private Player player1 = new Player("Player1", PieceColor.WHITE); //By default white
    private Player player2 = new Player("Player2", PieceColor.BLACK); //By default black

    Client client;

    private Player turn = player1;
    private int caseWidth;
    private int caseHeight;

    private Receiver receiver;

    public boolean flag = false;


    public Board(int caseWidth, int caseHeight, boolean online, String hostIp) throws Exception {

        //index 0 -> black && index 1 -> white
        setLogic(new chessLogic(new Piece[8][8]));
        setOnline(online);
        if(isOnline()){
            try{
                setClient(new Client(hostIp));
                this.receiver = new Receiver(this);
                receiver.start();

            }catch (Exception exc){

                setOnline(false);
            }
        }

        initPieces(true);
        initPiecesTexture();


        setCaseWidth(caseWidth);
        setCaseHeight(caseHeight);

        resetAndInitPiecesTexture();
    }


    public Board(){
        logic = new chessLogic(new Piece[8][8]);
        initPieces(false);
        //move(getPieces()[1][0], new Coord2d(0, 2));

        setSelectedPiece(null);
    }

    public void move(Piece piece, Coord2d coord) throws  Exception{
        if(!getLogic().couldAttack(piece, coord)){
            throw new Exception("can't attack");
        }
        getPieces()[coord.getX()][coord.getY()] = piece;
        //piece.setXY(coord.getX(), coord.getY());
        System.out.println(piece.getX() + "&& " + piece.getY());
        getPieces()[piece.getX()][piece.getY()] = new Piece(PieceColor.UNSET, PieceType.NONE, "");


    }
    public void move(Player player, Piece piece, Coord2d coord) throws  Exception{
        if(!getLogic().couldAttack(piece, coord)){
            throw new Exception("can't attack");
        }

        player.setScore(player.getScore() + (getPieces()[coord.getX()][coord.getY()].getType().ordinal()));
        getPieces()[coord.getX()][coord.getY()] = piece;

        System.out.println("player1 - score:  " + getPlayer1().getScore());
        System.out.println("player2 - score:  " + getPlayer2().getScore());
        getPieces()[piece.getX()][piece.getY()] = new Piece(PieceColor.UNSET, PieceType.NONE, "");

        setSelectedPiece(null);


        resetPiecesTexture();

        checkPromotion(piece, coord);
        swapTurn();


    }

    public void turnMove(Player player, Piece piece, Coord2d coord) throws Exception {
        if(player == getTurn()){
            move(player, piece, coord);
        }
    }

    public ByteBuffer drawText(String text){
        int s = 256; //Take whatever size suits you.
        BufferedImage b = new BufferedImage(s, s, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = b.createGraphics();
        g.drawString(text, 0, 0);

        int co = b.getColorModel().getNumComponents();

        byte[] data = new byte[co * s * s];
        b.getRaster().getDataElements(0, 0, s, s, data);

        ByteBuffer pixels = BufferUtils.createByteBuffer(data.length);
        pixels.put(data);
        pixels.rewind();

        return pixels;
    }

    public void drawSelectedMove(Player player){
        if(player.getSelectedPiece() == null){
            return ;
        }

        if(getLogic().getPiece(player.selectedPiece.getX(),player.selectedPiece.getY()).getColor() == player.getPieceColor()){
            int caseWith = getCaseWidth();
            int caseHeight = getCaseHeight();
            ArrayList<Coord2d> moves = getLogic().canAttack(player.getSelectedPiece().getX(), player.getSelectedPiece().getY());
            //System.out.println(getSelectedPiece().getX() + " && " + getSelectedPiece().getY());
            for (int i = 0; i < moves.size(); i++) {
                int x1 = caseWith * moves.get(i).getX();
                int x2 = caseWith * (moves.get(i).getX() + 1);
                int y1 = caseHeight * moves.get(i).getY();
                int y2 = caseWith * (moves.get(i).getY() + 1);
                GL46.glBegin(GL_QUADS);

                GL46.glColor4f(0.4f, 0.4f, 0.4f, 1f);
                GL46.glVertex2i(x1 + (caseWith/4),y1+(caseHeight/4));
                GL46.glVertex2i(x2 - (caseWith/4),y1+(caseHeight/4));
                GL46.glVertex2i(x2-(caseWith/4),y2-(caseHeight/4));
                GL46.glVertex2i(x1+(caseWith/4),y2-(caseHeight/4));

                GL46.glEnd();

            }
        }

    }
    public void drawSelectedMove(){
        if(getSelectedPiece() == null){
            return ;
        }
        int caseWith = getCaseWidth();
        int caseHeight = getCaseHeight();
        ArrayList<Coord2d> moves = getLogic().canAttack(getSelectedPiece().getX(), getSelectedPiece().getY());

        for (int i = 0; i < moves.size(); i++) {
            int x1 = caseWith * moves.get(i).getX();
            int x2 = caseWith * (moves.get(i).getX() + 1);
            int y1 = caseHeight * moves.get(i).getY();
            int y2 = caseWith * (moves.get(i).getY() + 1);
            GL46.glBegin(GL_QUADS);

            GL46.glColor4f(0.5f, 0.5f, 0.5f, 1f);
            GL46.glVertex2i(x1 + (caseWith/4),y1+(caseHeight/4));
            GL46.glVertex2i(x2 - (caseWith/4),y1+(caseHeight/4));
            GL46.glVertex2i(x2-(caseWith/4),y2-(caseHeight/4));
            GL46.glVertex2i(x1+(caseWith/4),y2-(caseHeight/4));

            GL46.glEnd();

            GL46.glFlush();

        }
    }
    
    public void drawSquares(){
        int caseWith = getCaseWidth();
        int height = getCaseHeight();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int x1=caseWith*i; int x2=caseWith*(i+1);
                int y1=caseHeight*j; int y2=caseHeight*(j+1);
                GL46.glBegin(GL_QUADS);
                if((i+j)%2==0){
                    GL46.glColor4f(0.7f, 0.7f, 0.7f, 1f);
                }else{
                    GL46.glColor4f(1.0f, 1.0f, 1.0f, 1f);
                }

                GL46.glVertex2i(x1,y1);
                GL46.glVertex2i(x2,y1);
                GL46.glVertex2i(x2,y2);
                GL46.glVertex2i(x1,y2);

                GL46.glEnd();
            }
        }
    }

    public void initPieceCoord(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                getPieces()[i][j].setXY(i,j);
            }
        }
    }

    public void initPieces(boolean wTexture){
        for (int i = 0; i < 8; i++) {
            for (int j = 2; j <= 5; j++) {
                getPieces()[i][j] = new Piece(PieceColor.UNSET, PieceType.NONE, "");
            }
        }
        initBlack(wTexture);
        initWhite(wTexture);
        initPieceCoord();
    }

    public void initBlack(boolean wTexture){
        initBlackPawns(wTexture);

            getPieces()[0][7] = new Piece(PieceColor.BLACK, PieceType.ROOK,"assets/img/pieces/bR.png");
            getPieces()[7][7] = new Piece(PieceColor.BLACK, PieceType.ROOK,"assets/img/pieces/bR.png");

            getPieces()[1][7] = new Piece(PieceColor.BLACK, PieceType.KNIGHT,"assets/img/pieces/bN.png");
            getPieces()[6][7] = new Piece(PieceColor.BLACK, PieceType.KNIGHT,"assets/img/pieces/bN.png");

            getPieces()[2][7] = new Piece(PieceColor.BLACK, PieceType.BISHOP,"assets/img/pieces/bB.png");
            getPieces()[5][7] = new Piece(PieceColor.BLACK, PieceType.BISHOP,"assets/img/pieces/bB.png");

            getPieces()[3][7] = new Piece(PieceColor.BLACK, PieceType.QUEEN,"assets/img/pieces/bQ.png");
            getPieces()[4][7] = new Piece(PieceColor.BLACK, PieceType.KING,"assets/img/pieces/bK.png");

    }

    public void initWhite(boolean wTexture){
        initWhitePawns(wTexture);

            getPieces()[0][0] = new Piece(PieceColor.WHITE, PieceType.ROOK,"assets/img/pieces/wR.png");
            getPieces()[7][0] = new Piece(PieceColor.WHITE, PieceType.ROOK,"assets/img/pieces/wR.png");

            getPieces()[1][0] = new Piece(PieceColor.WHITE, PieceType.KNIGHT,"assets/img/pieces/wN.png");
            getPieces()[6][0] = new Piece(PieceColor.WHITE, PieceType.KNIGHT,"assets/img/pieces/wN.png");

            getPieces()[2][0] = new Piece(PieceColor.WHITE, PieceType.BISHOP,"assets/img/pieces/wB.png");
            getPieces()[5][0] = new Piece(PieceColor.WHITE, PieceType.BISHOP,"assets/img/pieces/wB.png");

            getPieces()[3][0] = new Piece(PieceColor.WHITE, PieceType.QUEEN,"assets/img/pieces/wQ.png");
            getPieces()[4][0] = new Piece(PieceColor.WHITE, PieceType.KING,"assets/img/pieces/wK.png");


    }

    public void initBlackPawns(boolean wTexture){
        for (int i = 0; i < getPieces().length; i++) {
                getPieces()[i][6]=new Piece(PieceColor.BLACK, PieceType.PAWN, "assets/img/pieces/bP.png");

        }
    }

    public void initPiecesTexture(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                getPieces()[i][j].getTexture().initTexture();
            }
        }
    }

    public void initWhitePawns(boolean wTexture){
        for (int i = 0; i < getPieces().length; i++) {
                getPieces()[i][1]=new Piece(PieceColor.WHITE, PieceType.PAWN, "assets/img/pieces/wP.png");

        }
    }

    public void drawPieces(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(getPieces()[i][j].getType() != PieceType.NONE){
                    if(getPieces()[i][j] != getLogic().getPiece(getSelectedPiece())){
                        getPieces()[i][j].getTexture().draw(getCaseWidth(),getCaseHeight());

                    }
                }
            }
        }
    }

    public void resetPiecesTexture(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(getPieces()[i][j].getType() != PieceType.NONE){
                    getPieces()[i][j].getTexture().setX(i*getCaseWidth());
                    getPieces()[i][j].getTexture().setY(j*getCaseWidth());
                }
            }
        }
    }
    public void resetAndInitPiecesTexture(){
        resetPiecesTexture();
        initPiecesTexture();
    }

    public void initPT(){
        if(shouldInit){
            resetAndInitPiecesTexture();
        }
    }



    public void setCaseHeight(int caseHeight) {
        this.caseHeight = caseHeight;
    }
    public void setCaseWidth(int caseWidth) {
        this.caseWidth = caseWidth;
    }

    public int getCaseHeight() {
        return caseHeight;
    }
    public int getCaseWidth() {
        return caseWidth;
    }

    public Piece[][] getPieces() {
        return getLogic().getPieces();
    }

    public Coord2d getSelectedPiece() {
        return selectedPiece;
    }

    public chessLogic getLogic() {
        return logic;
    }

    public void setLogic(chessLogic logic) {
        this.logic = logic;
    }

    public void setSelectedPiece(Coord2d selectedPiece) {
        this.selectedPiece = selectedPiece;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setTurn(Player turn) {
        this.turn = turn;
    }

    public Player getTurn() {
        return turn;
    }


    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }



    public void listenMouse(long window, IntBuffer Xbuffer, IntBuffer Ybuffer){
        Coord2d mousePos;

        mousePos = MpihainoMouse.getMousePos(window);

        int X = (mousePos.getX())/(600/8);
        int Y = Math.abs((((mousePos.getY())/(600/8))-7));

        Xbuffer.put(0, X);
        Ybuffer.put(0, Y);
    }

    public void movePiece(long window){

        Coord2d mousePos;

        mousePos = MpihainoMouse.getMousePos(window);

        IntBuffer Xbuffer = BufferUtils.createIntBuffer(1);
        IntBuffer Ybuffer = BufferUtils.createIntBuffer(1);


        int newMouseState = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1);
        if(newMouseState == GLFW_PRESS){

            listenMouse(window, Xbuffer, Ybuffer);

            int X = Xbuffer.get(0);
            int Y = Ybuffer.get(0);

            if(!flag && this.getLogic().isPiece(X, Y) &&
                    this.getLogic().pieceColor(X, Y) == getTurn().getPieceColor()){

                if(!isOnline()){
                    this.setSelectedPiece(new Coord2d(X,Y));
                }else if(this.getLogic().pieceColor(X, Y) == getClient().getPieceColor()){
                    this.setSelectedPiece(new Coord2d(X,Y));
                }

            }

            Piece piece = getLogic().getPiece(getSelectedPiece());

            if(piece != null && piece.getColor() != PieceColor.UNSET){
                piece.getTexture().setX(mousePos.getX()-40);

                piece.getTexture().setY(Math.abs(mousePos.getY()-600)-25);
            }

            this.flag = true;


        }

            if(newMouseState == GLFW_RELEASE && flag){
                this.flag = false;

                listenMouse(window, Xbuffer, Ybuffer);

                int X = Xbuffer.get(0);
                int Y = Ybuffer.get(0);

                if(this.getSelectedPiece() != null){
                    try {
                        Piece piece = this.getLogic().getPiece(this.getSelectedPiece());

                        if(isOnline()){
                            String t = "SELECT//"+this.getTurn().getPieceColor().ordinal()+"//"+
                                    piece.getX()+"/"+piece.getY()+"//"+X+"/"+Y;

                            System.out.println("pokemon");

                            sendRequest(t);
                        }else{
                            this.turnMove(this.getTurn(),piece , new Coord2d(X, Y));
                        }
                        resetAndInitPiecesTexture();

                    } catch (Exception e) {

                    }
                }

                System.out.println("X:  " + X);
                System.out.println("Y:  " + Y);
                System.out.println("piece:  "+this.getLogic().getPiece(X, Y).getType());
                System.out.println("Color:  "+this.getLogic().getPiece(X, Y).getColor());
                resetAndInitPiecesTexture();
            }
    }

    public void sendRequest(String request) throws Exception {

        getClient().getOutput().writeUTF(request);

        getClient().getOutput().flush();

        //receiveChessPiece();
        //initPiecesTexture();

        setSelectedPiece(null);
    }

    public void receiveNewTurn() throws Exception{
        int color = getClient().getInput().readInt();

        setTurn(color);
    }

    public void receiveChessPiece() throws Exception{
        setLogic((chessLogic) getClient().getInput().readObject());
        setShouldInit(true);
        //setShouldInit(false);
        //resetAndInitPiecesTexture();
        //initPiecesTexture();
    }

    public void setTurn(int turnColor){
        Player p = getPlayerByColor(turnColor);
        setTurn(p);
    }

    public void checkPromotion(Piece piece, Coord2d newPos){
        int Y = newPos.getY();

        if(piece.getType() == PieceType.PAWN){
            if(piece.getColor() == PieceColor.BLACK && Y == 0){
                piece.setType(PieceType.QUEEN);
                piece.setTexture(new Texture("assets/img/pieces/bQ.png"));
            }else if(piece.getColor() == PieceColor.WHITE && Y == 7){
                piece.setType(PieceType.QUEEN);
                piece.setTexture(new Texture("assets/img/pieces/wQ.png"));
            }
        }

    }

    public void swapTurn(){
        if(this.getTurn() == this.getPlayer1()){
            this.setTurn(this.getPlayer2());
        }else{
            this.setTurn(this.getPlayer1());
        }
        getLogic().setTurn(getTurn().getPieceColor());
    }

    public Player getPlayerByColor(int color){
        if(getPlayer1().getPieceColor() == PieceColor.values()[color]){
            return getPlayer1();
        }
        return getPlayer2();
    }
@Override
    public void update(long window){
        initPT();
        movePiece(window);

        double now = glfwGetTime();
        GL46.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //GL46.glViewport(100, 100, 1000, 1000);

        //location and dimension of area to draw on within the display frame.
        GL46.glLoadIdentity();
        //rotate the following object about the z axis
        //GL11.glRotatef(rotation,0,0,1);


        //test.draw(300,300,this.getCaseWidth(),this.getCaseHeight());
        //this.drawPieces();

        this.drawSelectedMove();
        this.drawSquares();
        this.drawSelectedMove();
        this.drawPieces();
        drawSelectedPiece();
        setTurn(getLogic().getTurn().ordinal());


        glfwSwapBuffers(window); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.

        glfwPollEvents();

        this.initPieceCoord();
        //this.initPiecesTexture();
        //System.out.println(getLogic().getTurn());

    }

    public void drawSelectedPiece(){
        if(getSelectedPiece() != null){
            getLogic().getPiece(getSelectedPiece()).getTexture().draw(getCaseWidth(),getCaseHeight());
        }
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isShouldInit() {
        return shouldInit;
    }

    public void setShouldInit(boolean shouldInit) {
        this.shouldInit = shouldInit;
    }

}

