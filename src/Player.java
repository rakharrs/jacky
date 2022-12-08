package chessgame;

import util.Coord2d;

import java.io.Serializable;
import java.net.Socket;

public class Player extends Thread implements Serializable {
    Socket client;
    String username;
    int score = 0;

    PieceColor pieceColor;

    Coord2d selectedPiece = null;

    public Player(String username, PieceColor piececolor){
        setName(username);
        setPieceColor(piececolor);
    }

    public String getUserame() {
        return username;
    }

    public void setUserame(String username) {
        this.username = username;
    }

    public Coord2d getSelectedPiece() {
        return selectedPiece;
    }

    public void setSelectedPiece(Coord2d selectedPiece) {
        this.selectedPiece = selectedPiece;
    }
    public PieceColor getPieceColor() {
        return pieceColor;
    }

    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
