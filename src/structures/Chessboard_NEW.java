package structures;

import structures.chesses.Chess;
import structures.chesses.*;
import structures.terrains.*;

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
    /**
     * 棋盘上的棋子储存在chessMap中
     */
    private Chess[][] chessMap = new Chess[sizeX+1][sizeY+1];

    /**
     * 构造函数，创建棋盘地形
     */
    public Chessboard_NEW() {
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
    }

    /**
     * 初始化棋盘，把棋子放到对应位置上
     */
    public void initBoard() {
        // TODO: 完成intiBoard函数
        chessMap[1][1] = new Lion(-1, this);
        chessMap[9][7] = new Lion(1, this);

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
}
