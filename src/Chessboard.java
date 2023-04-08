import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Chessboard {
    // grids用于存放棋盘上的棋子数据，以左下角为坐标零点，坐标通过(x, y)表示
    /*
    grids中存放的数据中，绝对值分别表示：
    8   :   象
    7   :   狮
    6   :   虎
    5   :   豹
    4   :   狼
    3   :   狗
    2   :   猫
    1   :   鼠
    +/-表示蓝红双方
     */

    private int[][] grids = new int[7][9];
    // map用于存放棋盘上的地形数据
    /*
    map存放的数据里，分别表示：
    30  :   兽穴
    20  :   陷阱
    10  :   河流
     */
    private int[][] map = new int[7][9];

    private HashMap<Integer, String> nameTable = new HashMap<>();
    private HashMap<Integer, int[]> actionTable = new HashMap<>();
    public void initialBoard() {
        // 初始化nameTable
        nameTable.put(8, "象");
        nameTable.put(7, "狮");
        nameTable.put(6, "虎");
        nameTable.put(5, "豹");
        nameTable.put(4, "狼");
        nameTable.put(3, "狗");
        nameTable.put(2, "猫");
        nameTable.put(1, "鼠");
        nameTable.put(0, " ");
        nameTable.put(10, "河流");
        nameTable.put(20, "陷阱");
        nameTable.put(30, "兽穴");
        // 初始化actionTable
        actionTable.put(1, new int[]{0, 1});
        actionTable.put(2, new int[]{0, -1});
        actionTable.put(3, new int[]{-1, 0});
        actionTable.put(4, new int[]{1, 0});
        // 放置蓝方棋子
        grids[0][0] = 6; grids[0][2] = 8;
        grids[1][1] = 2; grids[2][2] = 4;
        grids[4][2] = 5; grids[5][1] = 3;
        grids[6][0] = 7; grids[6][2] = 1;
        // 放置红方棋子
        grids[0][8] = -7; grids[0][6] = -1;
        grids[1][7] = -3; grids[2][6] = -5;
        grids[4][6] = -4; grids[5][7] = -2;
        grids[6][8] = -6; grids[6][6] = -8;
        // 标记河流
        for (int i = 1; i <= 5; i++) {
            if (i != 3) {
                for (int j = 3; j <= 5; j++) {
                    map[i][j] = 10;
                }
            }
        }
        // 标记蓝方兽穴及陷阱
        map[3][0] = 30;
        map[2][0] = 20; map[3][1] = 20; map[4][0] = 20;
        // 标记红方兽穴及陷阱
        map[3][8] = -30;
        map[2][8] = -20; map[3][7] = -20; map[4][8] = -20;
    }
    public void printBoard() {
        for (int j = 8; j >= 0; j--) {
            for (int i = 0; i <= 6; i++) {
                if (grids[i][j] != 0) {
                    System.out.print(nameTable.get(Math.abs(grids[i][j])));
                } else {
                    System.out.print(nameTable.get(Math.abs(map[i][j])));
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }
    public void takeAction(int player, Scanner in) {
        ArrayList<int[]> poses = new ArrayList<>();
        String playerName;
        if (player == 1) {
            playerName = "蓝方";
        } else {
            playerName = "红方";
        }
        System.out.printf("%s下棋：\n", playerName);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                if (grids[i][j] * player > 0) {
                    poses.add(new int[]{i, j});
                    System.out.printf("%d\t:\t%s\n", poses.size(), nameTable.get(Math.abs(grids[i][j])));
                }
            }
        }


        int[] pos;
        int[] vector;

        boolean isLegal = true;
        do {
            if (!isLegal) {
                System.out.print("请重新选择棋子：");
            } else {
                System.out.print("请选择棋子：");
            }
            // 选择移动的棋子编号
            int action = -1;
            int chessIndex = -2;
            isLegal = true;
            do {
                if (!isLegal) {
                    System.out.printf("请输入范围在[0, %d]的整数\n", poses.size());
                    System.out.print("请重新选择棋子：");
                }
                if (in.hasNextInt()) {
                    chessIndex = in.nextInt() - 1;
                    isLegal = chessIndex >= 0 && chessIndex < poses.size();
                } else {
                    isLegal = false;
                }
            } while (!isLegal);
            pos = poses.get(chessIndex);

            // 选择行动方向
            do {
                if (action == 0) {
                    System.out.println("请输入范围[1,4]的整数");
                }
                System.out.print("请选择动作（1.上/2.下/3.左/4.右）：");
                if (in.hasNextInt()) {
                    action = in.nextInt();
                } else {
                    action = 0;
                }
            } while (action <= 0 || action > 4);
            vector = actionTable.get(action);

            isLegal = (pos[0] + vector[0] >= 0) && (pos[0] + vector[0] < 7) && (pos[1] + vector[1] >= 0) && (pos[1] + vector[1] < 9);
            // 是否越界判断
            if (!isLegal) {
                System.out.println("撞墙啦！");
            }
            if (isLegal) {
                if (map[pos[0] + vector[0]][pos[1] + vector[1]] == 10) {
                    // 河流判断
                    if (Math.abs(grids[pos[0]][pos[1]]) == 6 || Math.abs(grids[pos[0]][pos[1]]) == 7) {
                        // 可跳跃
                        int[] vectorCopy = vector.clone();
                        vector = vectorCopy.clone();
                        do {
                            if (Math.abs(grids[pos[0]+vector[0]][pos[1]+vector[1]]) == 1) {
                                isLegal = false;
                                System.out.println("被洗澡的老鼠挡住啦！");
                                break;
                            }
                            vector[0] += vectorCopy[0];
                            vector[1] += vectorCopy[1];
                        } while (map[pos[0] + vector[0]][pos[1] + vector[1]] == 10);
                    } else if (Math.abs(grids[pos[0]][pos[1]]) != 1) {
                        // 不可跳跃且不是老鼠
                        isLegal = false;
                        System.out.println("被河流挡住啦！");
                    }
                }
            }
            if (isLegal) {
                // 目标是否可吃判断
                if (grids[pos[0] + vector[0]][pos[1] + vector[1]] * player <= 0) {
                    // 如果该点没有我方棋子，则进入下一步判断
                    if (map[pos[0] + vector[0]][pos[1] + vector[1]] != player * 20 && map[pos[0] + vector[0]][pos[1] + vector[1]] != player * 30 && Math.abs(grids[pos[0] + vector[0]][pos[1] + vector[1]]) != 0) {
                        // 如果该点不是我方陷阱，不是我方巢穴，且已被对手占领，则进入下一步判断
                        if (Math.abs(grids[pos[0]][pos[1]]) != 1 && Math.abs(grids[pos[0] + vector[0]][pos[1] + vector[1]]) >= Math.abs(grids[pos[0]][pos[1]])) {
                            // 如果棋子不是老鼠且目标比它大
                            isLegal = false;
                            System.out.println("这你也敢碰？");
                        } else if (Math.abs(grids[pos[0]][pos[1]]) == 1 && Math.abs(grids[pos[0] + vector[0]][pos[1] + vector[1]]) != 8) {
                            // 如果棋子是老鼠且目标不是象
                            isLegal = false;
                            System.out.println("这你也敢碰？");
                        } else if (Math.abs(grids[pos[0]][pos[1]]) == 8 && Math.abs(grids[pos[0] + vector[0]][pos[1] + vector[1]]) == 1) {
                            // 如果棋子是象且目标是老鼠
                            isLegal = false;
                            System.out.println("这你也敢碰？");
                        } else if (map[pos[0]][pos[1]] == 10) {
                            isLegal = false;
                            System.out.println("刚洗完澡的老鼠不可以吃大象哦！");
                        }
                    } else if (map[pos[0] + vector[0]][pos[1] + vector[1]] == player * 30) {
                        isLegal = false;
                        System.out.println("偷自己家？6！");
                    }
                } else {
                    // 我方棋子挡路
                    isLegal = false;
                    System.out.println("自己人不能吃哦！");
                }
            }
        } while (!isLegal);
        grids[pos[0]+vector[0]][pos[1]+vector[1]] = grids[pos[0]][pos[1]];
        grids[pos[0]][pos[1]] = 0;
    }
    public int isEnd() {
        int ret = 0;
        if (map[3][0] * grids[3][0] < 0) {
            ret = -1;
        } else if (map[3][8] * grids[3][8] < 0) {
            ret = 1;
        }
        return ret;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Chessboard testBoard = new Chessboard();
        testBoard.initialBoard();
        int k = 1;
        while (testBoard.isEnd() == 0) {
            testBoard.printBoard();
            testBoard.takeAction(k, in);
            k = -k;
        }
        System.out.println("游戏结束！");
    }
}
