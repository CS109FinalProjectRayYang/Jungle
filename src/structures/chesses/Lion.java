package structures.chesses;

import structures.Chessboard_NEW;

import java.util.ArrayList;

public class Lion extends ChessJump{
    public Lion(int team, Chessboard_NEW chessboard) {
        super(team, 7, "Lion", chessboard);
    }

    public static void main(String[] args) {
        // 仅做测试用，不用管下面这几行代码
        Chessboard_NEW chessboard = new Chessboard_NEW();
        chessboard.putChess(new int[]{4, 3}, new Rat(1, chessboard));
        chessboard.putChess(new int[]{3, 4}, new Elephant(1, chessboard));
        Lion chessLion = new Lion(1, chessboard);
        ArrayList<int[]> legalMoves = chessLion.getLegalMove(new int[]{3, 3});
        for (int[] move : legalMoves) {
            System.out.printf("%d, %d\n", move[0], move[1]);
        }
    }
}
