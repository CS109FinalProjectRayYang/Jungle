package structures;

import structures.chesses.Chess;

/**
 * ChessWithPos类
 * <p>用于将Chess和Pos打包一起存储</p>
 */
public class ChessWithPos {
    private Chess chess;
    private int[] pos;
    public ChessWithPos(Chess chess, int[] pos) {
        this.chess = chess;
        this.pos = pos;
    }

    public Chess getChess() {
        return chess;
    }

    public int[] getPos() {
        return pos;
    }
}
