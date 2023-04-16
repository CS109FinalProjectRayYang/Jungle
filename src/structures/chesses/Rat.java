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
}
