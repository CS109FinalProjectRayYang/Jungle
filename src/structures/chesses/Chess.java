package structures.chesses;

import structures.Chessboard_NEW;

import javax.swing.*;
import java.util.ArrayList;

public class Chess {
    private int chessID;
    private int team;
    private int capacity;
    private String chessName;
    private boolean[] foodChain;
    private ImageIcon imgBlue;
    private ImageIcon imgRed;
    private ImageIcon imgBlueGrey;
    private ImageIcon imgRedGrey;
    private Chessboard_NEW chessboard;
    public Chess(int team, int chessID, String chessName, Chessboard_NEW chessboard) {
        // 红方在左，team编号为-1；蓝方在右，team编号为1
        this.team = team;
        // chessID 和动物实力正相关，象是8，鼠是1
        this.chessID = chessID;
        // capacity默认 = chessID，只有在进入敌方陷阱的时候capacity会变成0
        capacity = chessID;
        this.chessName = chessName;
        // 每个棋子有四个图片，分别是：蓝方，红方，蓝方灰色，红方灰色
        // 蓝方和红方代表了不同朝向，如果当前是对方操作步骤的话，本方图片会以灰色显示，因此需要灰色图片
        imgBlue = new ImageIcon("data/img/animals/blue/%d.png".formatted(chessID));
        imgRed = new ImageIcon("data/img/animals/red/%d.png".formatted(chessID));
        imgBlueGrey = new ImageIcon("data/img/animals/blue/grey/%d.png".formatted(chessID));
        imgRedGrey = new ImageIcon("data/img/animals/red/grey/%d.png".formatted(chessID));
        this.chessboard = chessboard;
        foodChain = new boolean[9];
        // foodChain中存放该棋子能否吃在某个capacity状态下的棋子的布尔值
        // capacity默认 = chessID，只有在进入敌方陷阱的时候capacity会变成0
        // 大capacity能吃小capacity动物
        for (int i = 0; i < 9; i++) {
            foodChain[i] = i < chessID;
        }
        // 老鼠是例外，如果该棋子是老鼠，那么老鼠能吃capacity是8的动物，即大象
        if (chessID == 1) {
            foodChain[8] = true;
        }
    }

    public int getID() {
        return chessID;
    }
    public int getTeam() {
        return team;
    }
    public int getCapacity() {
        return capacity;
    }

    public String getChessName() {
        return chessName;
    }

    public ImageIcon getImg() {
        if (team == 1) {
            return imgBlue;
        } else {
            return imgRed;
        }
    }
    public ImageIcon getGreyImg() {
        if (team == 1) {
            return imgBlueGrey;
        } else {
            return imgRedGrey;
        }
    }
    public Chessboard_NEW getChessboard() {
        return chessboard;
    }

    /**
     * 获得该棋子在位置pos下的合法移动坐标位置列表
     * @param pos
     * @return ArrayList 下一步的合法坐标位置列表
     */
    public ArrayList<int[]> getLegalMove(int[] pos) {
        ArrayList<int[]> ret = new ArrayList<>();

        // 加载上下左右四种move方式
        ArrayList<int[]> moves = getMoves();

        // 判断每个move是否合法
        for (int[] move : moves) {
            if (isLegal(pos, move)) {
                // 如果某个move合法，则将棋子进行该move后的坐标位置加入ret列表
                ret.add(new int[]{pos[0] + move[0], pos[1] + move[1]});
            }
        }
        return ret;
    }

    /**
     * 获得该棋子在具有跳跃能力时，在位置pos下的合法移动坐标位置列表
     * @param pos
     * @return ArrayList 下一步的合法坐标位置列表
     */
    public ArrayList<int[]> getLegalMove_Jump(int[] pos) {
        ArrayList<int[]> ret = new ArrayList<>();
        ArrayList<int[]> moves = Chess.getMoves();

        // 通过modifyMoves使moves中的move动作能够跨越河流
        modifyMoves(pos, moves);

        // 判断每个move是否合法
        for (int[] move : moves) {
            if (isLegal(pos, move)) {
                ret.add(new int[]{pos[0] + move[0], pos[1] + move[1]});
            }
        }
        return ret;
    }
    public ArrayList<int[]> getLegalMove_Swim(int[] pos) {
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
    public boolean isLegal(int[] pos, int[] move) {
        boolean ret = true;

        // 获得pos位置的棋子进行该move后的位置nextPos
        int[] nextPos = new int[]{pos[0] + move[0], pos[1] + move[1]};

        // 判断nextPos位置是否越界和遭遇河流，再判断该位置上有没有棋子，是否是对手的棋子，能不能吃
        if (isOutOfBound(nextPos) || isInRiver(nextPos)) {
            ret = false;
        } else if (chessboard.getChess(nextPos) != null) {
            Chess chess = chessboard.getChess(nextPos);

            // 只有该棋子是对手的且比自己弱，才合法
            ret = chess.getTeam() != this.getTeam() && foodChain[chess.getCapacity()];
        }
        return ret;
    }
    private boolean isLegalRat(int[] pos, int[] move) {
        boolean ret = true;

        // 获得pos位置的棋子进行该move后的位置nextPos
        int[] nextPos = new int[]{pos[0] + move[0], pos[1] + move[1]};

        // 判断nextPos位置是否越界和遭遇河流，再判断该位置上有没有棋子，是否是对手的棋子，能不能吃
        if (isOutOfBound(nextPos) ) {
            ret = false;
        } else if (isInRiver(pos) && chessboard.getChess(nextPos) != null) {
            ret = false;
        } else if (chessboard.getChess(nextPos) != null) {
            Chess chess = chessboard.getChess(nextPos);
            ret = foodChain[chess.getCapacity()];
        }
        return ret;
    }

    /**
     * 判断位置pos是否越界
     * @param pos
     * @return boolean 越界
     */
    public boolean isOutOfBound(int[] pos) {
        boolean ret = false;
        if (pos[0] <= 0 || pos[0] > Chessboard_NEW.getSizeX() || pos[1] <= 0 || pos[1] > Chessboard_NEW.getSizeY()) {
            ret = true;
        }
        return ret;
    }

    /**
     * 判断位置pos是否遭遇河流
     * @param pos
     * @return boolean 遭遇河流
     */
    private boolean isInRiver(int[] pos) {
        return Chessboard_NEW.getTerrain(pos).getId() == 10;
    }

    /**
     * 使moves中的move动作跳过河流
     * @param pos
     * @param moves
     */
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
                        if (Chessboard_NEW.getTerrain(nextPos).getId() != 10) {

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

    /**
     * //加载并返回左右上下四个move向量
     * @return ArrayList moves
     */
    public static ArrayList<int[]> getMoves() {
        ArrayList<int[]> moves = new ArrayList<>();

        int[] moveLeft = new int[]{-1, 0};
        int[] moveRight = new int[]{1, 0};
        int[] moveUp = new int[]{0, 1};
        int[] moveDown = new int[]{0, -1};

        moves.add(moveLeft);
        moves.add(moveRight);
        moves.add(moveUp);
        moves.add(moveDown);

        return moves;
    }
}
