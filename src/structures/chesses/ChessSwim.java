package structures.chesses;

import structures.Chessboard_NEW;

import java.util.ArrayList;

public class ChessSwim extends Chess{
    public ChessSwim(int team, int chessID, String chessName, Chessboard_NEW chessboard) {
        super(team, chessID, chessName, chessboard);
    }
    @Override
    public ArrayList<int[]> getLegalMove(int[] pos) {
        ArrayList<int[]> ret = new ArrayList<>();
        ArrayList<int[]> moves = Chess.getMoves();
        for (int[] move : moves) {
            if (isLegalRat(pos, move)) {
                // 如果某个move合法，则将棋子进行该move后的坐标位置加入ret列表
                ret.add(new int[]{pos[0] + move[0], pos[1] + move[1]});
            }
        }
        // 老鼠可以下水，且在水中不能被吃，也不能吃岸上的大象
        // 因此对某个move动作只需要做 是否越界 以及 在水中不能从陆地上有动物的地方上岸 两个判断
        return ret;
    }
}