package structures.chesses;

import structures.Chessboard_NEW;

import java.util.ArrayList;

public class Rat extends Chess {
    public Rat(int team, Chessboard_NEW chessboard) {
        super(team, 1, "Rat", chessboard);
    }
    @Override
    public ArrayList<int[]> getLegalMove(int[] pos) {
        return getLegalMove_Swim(pos);
    }

    public static void main(String[] args) {
        Chessboard_NEW chessboardNew = new Chessboard_NEW();
        chessboardNew.putChess(new int[]{3, 3}, new Elephant(1, chessboardNew));
        Rat rat = new Rat(-1, chessboardNew);
        ArrayList<int[]> legalMoves = rat.getLegalMove(new int[]{3, 4});
        for (int[] move : legalMoves) {
            System.out.printf("%d, %d\n", move[0], move[1]);
        }
    }
}
