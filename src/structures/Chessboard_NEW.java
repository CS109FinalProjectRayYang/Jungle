package structures;

import structures.chesses.Chess;
import structures.chesses.*;
import structures.terrains.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 描述 : 棋盘类，用于储存棋子与地形。
 * <p></p>
 * 坐标 : 左下角为原点建立横x纵y坐标平面，坐标从1开始计数
 * <p></p>
 * 状态 : initBoard方法未完成
 * <p></p>
 * {@code @Author}  : RayOvO
 */

public class Chessboard_NEW {
    /**
     * 棋盘的长宽以及地形为静态常量
     */
    private final static int sizeX = 9;
    private final static int sizeY = 7;
    private final static Terrain[][] terrains = new Terrain[sizeX+1][sizeY+1];
    private static boolean isTerrainCreated = false;
    /**
     * 棋盘上的棋子储存在chessMap中
     */
    private Chess[][] chessMap = new Chess[sizeX+1][sizeY+1];

    /**
     * 构造函数，创建棋盘地形
     */

    public Chessboard_NEW(Chessboard_NEW chessboardOld) {
        if (!isTerrainCreated) {
            createTerrains();
        }
        for (int i = 1; i <= sizeX; i++) {
            for (int j = 1; j <= sizeY; j++) {
                this.chessMap[i][j] = chessboardOld.chessMap[i][j];
            }
        }
    }
    public Chessboard_NEW() {
        if (!isTerrainCreated) {
            createTerrains();
        }
    }
    private void createTerrains() {
        //创建地形对象，陷阱和兽穴需要区分红蓝方
        Terrain denBlue = new Den(1);
        Terrain denRed = new Den(-1);
        Terrain trapBlue = new Trap(1);
        Terrain trapRed = new Trap(-1);
        Terrain river = new River();
        Terrain flat = new Flat();

        //放入平地
        for (int i = 1; i <= sizeX; i++) {
            for (int j = 1; j <= sizeY; j++) {
                terrains[i][j] = flat;
            }
        }

        //放入兽穴
        terrains[1][4] = denRed;
        terrains[9][4] = denBlue;

        //放入陷阱
        terrains[1][3] = trapRed;
        terrains[1][5] = trapRed;
        terrains[2][4] = trapRed;
        terrains[8][4] = trapBlue;
        terrains[9][3] = trapBlue;
        terrains[9][5] = trapBlue;

        //放入河流
        for (int i = 4; i <= 6; i++) {
            for (int j = 2; j <= 6; j++) {
                if (j != 4) {
                    terrains[i][j] = river;
                }
            }
        }
        isTerrainCreated = true;
    }

    /**
     * 初始化棋盘，把棋子放到对应位置上
     */
    public void initBoard() {

        chessMap[1][7] = new Lion(-1, this);
        chessMap[3][7] = new Rat(-1, this);
        chessMap[2][6] = new Dog(-1, this);
        chessMap[3][5] = new Leopard(-1, this);
        chessMap[3][3] = new Wolf(-1, this);
        chessMap[2][2] = new Cat(-1, this);
        chessMap[1][1] = new Tiger(-1, this);
        chessMap[3][1] = new Elephant(-1, this);

        chessMap[7][7] = new Elephant(1, this);
        chessMap[9][7] = new Tiger(1, this);
        chessMap[8][6] = new Cat(1, this);
        chessMap[7][5] = new Wolf(1, this);
        chessMap[7][3] = new Leopard(1, this);
        chessMap[8][2] = new Dog(1, this);
        chessMap[9][1] = new Lion(1, this);
        chessMap[7][1] = new Rat(1, this);
    }

    /**
     * 将棋盘上nowPos位置的棋子移动到nextPos上，对输入完全信任
     * @param nowPos
     * @param nextPos
     */
    public void moveChess(int[] nowPos, int[] nextPos) {
        chessMap[nextPos[0]][nextPos[1]] = chessMap[nowPos[0]][nowPos[1]];
        chessMap[nowPos[0]][nowPos[1]] = null;
    }

    /**
     * 将棋盘状态在命令行中打印出来，仅用于完成GUI前的调试测试
     */
    public void printBoard() {
        for (int i = 1; i <= sizeY; i++) {
            for (int j = 1; j <= sizeX; j++) {
                if (chessMap[j][i] != null) {
                    char c = chessMap[j][i].getChessName().charAt(0);
                    System.out.printf(" %c ", chessMap[j][i].getChessName().charAt(0));
                } else {
                    char c = terrains[j][i].getName().charAt(0);
                    switch (c) {
                        case 'F' -> System.out.print(" . ");
                        case 'R' -> System.out.print(" ~ ");
                        case 'D' -> System.out.print(" o ");
                        case 'T' -> System.out.print(" x ");
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * 获得某个team的所有棋子
     * <p>定义了一个新类型ChessWithPos</p>
     *
     * @param team
     * @return ArrayList 该team在棋盘上存在的所有棋子
     */
    public ArrayList<ChessWithPos> getChessesOfTeam(int team) {
        ArrayList<ChessWithPos> ret = new ArrayList<>();
        for (int i = 1; i <= sizeX; i++) {
            for (int j = 1; j <= sizeY; j++) {
                if (chessMap[i][j] != null && chessMap[i][j].getTeam() == team) {
                    ret.add(new ChessWithPos(chessMap[i][j], new int[]{i, j}));
                }
            }
        }
        return ret;
    }

    /**
     * 获得棋盘中pos位置上的棋子
     * @param pos
     * @return 一个Chess对象
     */
    public Chess getChess(int[] pos) {
        return chessMap[pos[0]][pos[1]];
    }

    /**
     * 在棋盘中pos位置上放置棋子chess
     * @param pos
     * @param chess
     */
    public void putChess(int[] pos, Chess chess) {
        chessMap[pos[0]][pos[1]] = chess;
    }

    /**
     * 获得棋盘的宽度
     * @return int 宽度
     */
    public static int getSizeX() {
        return sizeX;
    }

    /**
     * 获得棋盘的长度
     * @return int 长度
     */
    public static int getSizeY() {
        return sizeY;
    }

    /**
     * 获得棋盘中pos位置上的地形
     * @param pos
     * @return 一个Terrain对象
     */
    public static Terrain getTerrain(int[] pos) {
        return terrains[pos[0]][pos[1]];
    }
    public int isEnd() {
        int ret = 0;
        if (isLose(1)) {
            ret = -1;
        } else if (isLose(-1)) {
            ret = 1;
        }
        return ret;
    }
    private boolean isLose(int team) {
        boolean ableToMove = false;
        boolean obtained = false;
        OUT:
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (chessMap[j][i] != null) {
                    // 存在自己team可移动的棋子
                    if (chessMap[j][i].getTeam() == team && chessMap[j][i].getLegalMove(new int[]{j, i}).size() != 0) {
                        ableToMove = true;
                        break OUT;
                    }
                }
            }
        }
        if (team == 1) {
            obtained = chessMap[9][4] != null;
        } else {
            obtained = chessMap[1][4] != null;
        }
        return !(ableToMove && !obtained);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Chessboard_NEW chessboardNew = new Chessboard_NEW();
        chessboardNew.initBoard();
        while (chessboardNew.isEnd() == 0) {
            chessboardNew.printBoard();
            System.out.print("Choose chess: ");
            int[] nowPos = new int[2];
            nowPos[0] = in.nextInt();
            nowPos[1] = in.nextInt();
            Chess chess = chessboardNew.getChess(nowPos);
            if (chess != null) {
                ArrayList<int[]> legalMoves = chess.getLegalMove(nowPos);
                for (int[] move : legalMoves) {
                    System.out.printf("(%d, %d)\n", move[0], move[1]);
                }
                System.out.print("Move it to: ");
                int[] nextPos = new int[2];
                nextPos[0] = in.nextInt();
                nextPos[1] = in.nextInt();
                chessboardNew.moveChess(nowPos, nextPos);
            } else {
                break;
            }
        }
    }
}
