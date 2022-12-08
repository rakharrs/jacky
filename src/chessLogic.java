package chessgame;

import util.Coord2d;

import java.io.Serializable;
import java.util.ArrayList;

public class chessLogic implements Serializable {
    Piece[][] pieces;
    PieceColor turn = PieceColor.WHITE;

    public chessLogic(Piece[][] pieces){
        setPieces(pieces);
    }

    public boolean couldAttack(Piece piece, Coord2d coord){
        ArrayList<Coord2d> possibilities = canAttack(piece.getX(), piece.getY());
        System.out.println(piece.getX()+"//"+piece.getY());
        for (int i = 0; i < possibilities.size(); i++) {
            if(possibilities.get(i).getX() == coord.getX() && possibilities.get(i).getY() == coord.getY()){
                return true;
            }
        }
        return  false;
    }
    public ArrayList<Coord2d> canAttack(int x, int y){
        ArrayList<Coord2d> val = new ArrayList<>();
        Piece piece = getPiece(x, y);
        final boolean isWhite = piece.getColor() == PieceColor.WHITE;
        int pawnUpDirection = isWhite ? 1 : -1;

        switch (piece.getType()){
            case PAWN -> {
                if(!isPiece(x, y + pawnUpDirection)){
                    if(!isPiece(x, y + (pawnUpDirection*2)) && y == 1 || y == 6){
                        val.add(new Coord2d(x, y + (pawnUpDirection*2)));
                    }
                    val.add(new Coord2d(x, y + pawnUpDirection));
                }

                if(isPieceEnemy(x + 1, y + pawnUpDirection, piece))
                    val.add(new Coord2d(x + 1, y + pawnUpDirection));
                if(isPieceEnemy(x - 1, y + pawnUpDirection, piece))
                    val .add(new Coord2d(x - 1, y + pawnUpDirection));
                return val;

            }
            case KNIGHT -> {
                if(!piece.sameColor(getPiece(x + 1, y + 2)))
                    val.add(new Coord2d(x + 1, y + 2));
                if(!piece.sameColor(getPiece(x - 1, y + 2)))
                    val.add(new Coord2d(x - 1, y + 2));
                if(!piece.sameColor(getPiece(x + 2, y + 1)))
                    val.add(new Coord2d(x + 2, y + 1));
                if(!piece.sameColor(getPiece(x - 2, y + 1)))
                    val.add(new Coord2d(x - 2, y + 1));

                if(!piece.sameColor(getPiece(x + 1, y - 2)))
                    val.add(new Coord2d(x + 1, y - 2));
                if(!piece.sameColor(getPiece(x - 1, y - 2)))
                    val.add(new Coord2d(x - 1, y - 2));
                if(!piece.sameColor(getPiece(x + 2, y - 1)))
                    val.add(new Coord2d(x + 2, y - 1));
                if(!piece.sameColor(getPiece(x - 2, y - 1)))
                    val.add(new Coord2d(x - 2, y - 1));

                return val;

            }
            case BISHOP -> {
                return bishopPossibleMove(x, y);

            }
            case ROOK -> {
                return RookPossibleMove(x, y);
            }
            case QUEEN -> {
                val.addAll(bishopPossibleMove(x, y));
                val.addAll(RookPossibleMove(x, y));
                return val;
            }
            case KING -> {
                return kingPossibleMove(x, y);
            }


        }
        return val;
    }

    public Piece getPiece(int x, int y){
        if(x < 8 && x >= 0 && y < 8 && y >= 0){
            return getPieces()[x][y];
        }
        return null;
    }

    public Piece getPiece(Coord2d coo){
        if(coo != null && coo.getX() < 8 && coo.getX() >= 0 && coo.getY() < 8 && coo.getY() >= 0){
            return getPieces()[coo.getX()][coo.getY()];
        }
        return null;
    }


    public boolean isPieceEnemy(int x, int y, Piece piece){
        if(isPiece(x, y) && !getPiece(x, y).sameColor(piece)){
            return true;
        }
        return false;
    }

    public boolean isPiece(int x, int y){
        Piece piece = getPiece(x, y);

        if(piece != null && piece.getType() != PieceType.NONE){
            return true;
        }
        return false;
    }

    public PieceColor pieceColor(int x, int y){
        Piece piece = getPiece(x, y);
        if(piece != null){
            return piece.getColor();
        }
        return PieceColor.UNSET;

    }
    public Piece[][] getPieces() {
        return pieces;
    }

    public ArrayList<Coord2d> kingPossibleMove(int x, int y){
        ArrayList<Coord2d> val = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            if(isPiece(x + i, y + 1)) {
                if (pieceColor(x, y) != pieceColor(x + i, y + 1)){
                    val.add(new Coord2d(x + i, y + 1));
                }

            }else
                val.add(new Coord2d(x + i, y + 1));
        }

        for (int i = -1; i <= 1; i++) {
            if(isPiece(x + i, y - 1)) {
                if (pieceColor(x, y) != pieceColor(x + i, y - 1)){
                    val.add(new Coord2d(x + i, y - 1));
                }

            }else
                val.add(new Coord2d(x + i, y - 1));
        }

        if(isPiece(x + 1, y)){
            if(pieceColor(x, y) != pieceColor(x + 1, y))
                val.add(new Coord2d(x + 1, y));
        }else
            val.add(new Coord2d(x + 1, y));

        if(isPiece(x - 1, y)){
            if(pieceColor(x, y) != pieceColor(x - 1, y))
                val.add(new Coord2d(x - 1, y));
        }else
            val.add(new Coord2d(x - 1, y));

        return val;
    }
    public ArrayList<Coord2d> bishopPossibleMove(int x, int y){
        ArrayList<Coord2d> val = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            int pX = x + i; int pY = y + i;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        for (int i = 1; i < 8; i++) {
            int pX = x + i; int pY = y - i;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        for (int i = 1; i < 8; i++) {
            int pX = x - i; int pY = y - i;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        for (int i = 1; i < 8; i++) {
            int pX = x - i; int pY = y + i;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        return val;
    }

    public ArrayList<Coord2d> RookPossibleMove(int x, int y){
        ArrayList<Coord2d> val = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            int pX = x + i; int pY = y;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        for (int i = 1; i < 8; i++) {
            int pX = x - i; int pY = y;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        for (int i = 1; i < 8; i++) {
            int pX = x; int pY = y - i;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        for (int i = 1; i < 8; i++) {
            int pX = x; int pY = y + i;
            Piece piece = getPiece(pX, pY);
            if (piece == null) {
                break;
            }
            if(piece.getType() != PieceType.NONE){
                if(piece.getColor() != getPiece(x, y).getColor()){
                    val.add(new Coord2d(pX, pY));
                }
                break;
            }
            val.add(new Coord2d(pX, pY));
        }

        return val;
    }

    public PieceColor getTurn() {
        return turn;
    }

    public void setTurn(PieceColor turn) {
        this.turn = turn;
    }

    public void setPieces(Piece[][] pieces) {
        this.pieces = pieces;
    }
}
