package chessgame;

public enum PieceColor {
    UNSET,

    WHITE,

    BLACK;

    public static PieceColor getOpposite(PieceColor color) {
        if (color == WHITE)
            return BLACK;

        if (color == BLACK)
            return WHITE;

        return UNSET;
    }
}
