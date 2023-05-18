package structures.chesses;

import structures.Chessboard;

import java.util.ArrayList;

public class ChessJump extends Chess {
    public ChessJump(int team, int chessID, String chessName, Chessboard chessboard) {
        super(team, chessID, chessName, chessboard);
    }
    @Override
    public ArrayList<int[]> getLegalMove(int[] pos) {
        ArrayList<int[]> ret = new ArrayList<>();
        ArrayList<int[]> moves = Chess.getMoves();

        // 通过modifyMoves使moves中的move动作能够跨越河流
        modifyMoves(pos, moves);

        // 判断每个move是否合法
        for (int[] move : moves) {
            if (super.isLegal(pos, move)) {
                ret.add(new int[]{pos[0] + move[0], pos[1] + move[1]});
            }
        }
        return ret;
    }
    public void modifyMoves(int[] pos, ArrayList<int[]> moves) {
        // 迭代每一个move动作
        for (int k = 0; k < moves.size(); k++) {

            // 获取位置在pos的棋子通过该move动作后的坐标nextPos
            int[] nextPos = new int[]{pos[0] + moves.get(k)[0], pos[1] + moves.get(k)[1]};

            // 在nextPos不越界的前提下
            if (!isOutOfBound(nextPos)) {

                // 如果遭遇河流
                if (isInRiver(nextPos)) {
                    int i;
                    // 最多只要跳三格就能跨越河流，因此从跳一格开始，每次尝试多跳一格，看看能否在不遇到老鼠挡路的情况下跳到对岸
                    for (i = 1; i <= 3; i++) {

                        // 获得跳i格后的新位置nextPos
                        nextPos = new int[]{pos[0] + i * moves.get(k)[0], pos[1] + i * moves.get(k)[1]};

                        // 无论跳到对岸还是被老鼠挡路都以break停下
                        // 如果被挡路，则当前的nextPos应该在河里，无法通过isLegal判断，因此达成被老鼠挡路时不能跳跃的效果
                        // 如果不被挡路，则nextPos应当在对岸，如果对岸有能吃的动物，则应当吃掉，如果对岸有吃不了的动物，亦无法通过isLegal判断，达成期望效果
                        if (Chessboard.getTerrain(nextPos).getId() != 10) {

                            // 如果当前坐标不再是河，则说明已经来到对岸
                            break;
                        } else if (getChessboard().getChess(nextPos) != null) {

                            // 如果当前坐标是河，那么判断chessMap上这个点是不是空的
                            // 因为除了老鼠别的动物都不能下水，所以当检测到这个点不是空的，就一定是老鼠，被老鼠挡路了
                            break;
                        }
                    }

                    // 更改当前move的正确步数
                    moves.set(k, new int[]{i * moves.get(k)[0], i * moves.get(k)[1]});
                }
            }
        }
    }
}
