package structures.chesses;

import structures.Chessboard;

import java.util.ArrayList;

public class FictionChess extends Chess{
    public FictionChess(Chessboard chessboard) {
        super(0, 0, "", chessboard);
    }

    @Override
    public ArrayList<int[]> getLegalMove(int[] pos) {
        ArrayList<int[]> ret = new ArrayList<>();
        ret.add(new int[]{0, 0});
        return ret;
    }
}
