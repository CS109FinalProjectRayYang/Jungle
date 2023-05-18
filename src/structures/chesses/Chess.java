package structures.chesses;

import structures.Chessboard;
import structures.terrains.Terrain;

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
    private Chessboard chessboard;
    public Chess(int team, int chessID, String chessName, Chessboard chessboard) {
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
            foodChain[i] = i <= chessID;
        }
        // 老鼠是例外，如果该棋子是老鼠，那么老鼠能吃capacity是8的动物，即大象
        if (chessID == 1) {
            foodChain[8] = true;
        }
        // 大象是例外，大象不能吃老鼠
        if (chessID == 8) {
            foodChain[1] = false;
        }
    }

    public int getID() {
        return chessID;
    }
    public int getTeam() {
        return team;
    }
    public int getCapacity(int[] pos) {
        int ret = capacity;
        Terrain terrain = Chessboard.getTerrain(pos);
        if (terrain.getId() == 20 && team * terrain.getTeam() == -1) {
            ret = 0;
        }
        return ret;
    }

    public boolean ableToEat(int capacity) {
        return foodChain[capacity];
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
    public Chessboard getChessboard() {
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


    public boolean isLegal(int[] pos, int[] move) {
        boolean ret = true;

        // 获得pos位置的棋子进行该move后的位置nextPos
        int[] nextPos = new int[]{pos[0] + move[0], pos[1] + move[1]};
        if (nextPos[1] == 4) {
            int a = 1;
        }
        // 判断nextPos位置是否越界或遭遇河流或是自己的兽穴，再判断该位置上有没有棋子，是否是对手的棋子，能不能吃
        if (isOutOfBound(nextPos) || isInRiver(nextPos) || isDen(nextPos, team)) {
            ret = false;
        } else if (chessboard.getChess(nextPos) != null) {
            Chess chess = chessboard.getChess(nextPos);

            // 只有该棋子是对手的且比自己弱，才合法
            ret = chess.getTeam() != this.getTeam() && foodChain[chess.getCapacity(nextPos)];
        }
        return ret;
    }
    protected boolean isLegalRat(int[] pos, int[] move) {
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
            ret = foodChain[chess.getCapacity(nextPos)];
        }
        return ret;
    }

    /**
     * 判断位置pos是否越界
     * @param pos
     * @return boolean 越界
     */
    public static boolean isOutOfBound(int[] pos) {
        boolean ret = false;
        if (pos[0] <= 0 || pos[0] > Chessboard.getSizeX() || pos[1] <= 0 || pos[1] > Chessboard.getSizeY()) {
            ret = true;
        }
        return ret;
    }

    /**
     * 判断位置pos是否遭遇河流
     * @param pos
     * @return boolean 遭遇河流
     */
    protected static boolean isInRiver(int[] pos) {
        return Chessboard.getTerrain(pos).getId() == 10;
    }
    protected static boolean isDen(int[] pos, int team) {
        return Chessboard.getTerrain(pos).getId() == 30 && Chessboard.getTerrain(pos).getTeam() == team;
    }

    /**
     * 使moves中的move动作跳过河流
     * @param pos
     * @param moves
     */


    /**
     * //加载并返回左右上下四个move向量
     * @return ArrayList moves
     */
    public static ArrayList<int[]> getMoves() {
        ArrayList<int[]> moves = new ArrayList<>();

        int[] moveLeft = new int[]{-1, 0};
        int[] moveRight = new int[]{1, 0};
        int[] moveUp = new int[]{0, -1};
        int[] moveDown = new int[]{0, 1};

        moves.add(moveLeft);
        moves.add(moveRight);
        moves.add(moveUp);
        moves.add(moveDown);

        return moves;
    }
}
