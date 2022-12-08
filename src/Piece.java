package chessgame;

import util.Texture;

import java.io.Serializable;

public class Piece implements Serializable {
    private PieceColor color;
    private PieceType type;
    private Texture texture;

    public int x;
    public int y;

    public Piece(){

    }
    public Piece(Piece piece){
        setColor(piece.getColor());
        setType(piece.getType());
        setTexture(piece.getTexture());
        setXY(piece.getX(), piece.getY());
    }

    public Piece(PieceColor color, PieceType type){
        setColor(color);
        setType(type);
    }
    public Piece(PieceColor color, PieceType type, String texturePath){
        setColor(color);
        setType(type);
        setTexture(new Texture(texturePath));
    }

    public boolean sameColor(Piece piece){
        if(piece != null && piece.getColor() == this.getColor())
            return true;
        return false;
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    public void setType(PieceType type) {
        this.type = type;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public void setXY(int x, int y){
        setX(x);
        setY(y);
    }
}
