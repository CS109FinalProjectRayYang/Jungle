package structures.chesses;

import structures.Chessboard_NEW;

import java.util.ArrayList;

public class Tiger extends Chess{
    public Tiger(int team, Chessboard_NEW chessboard) {
        super(team, 6, "Tiger", chessboard);
    }
    @Override
    public ArrayList<int[]> getLegalMove(int[] pos) {
        return getLegalMove_Jump(pos);
    }

}
