package structures.players;

import structures.ChessWithPos;
import structures.Chessboard_NEW;
import structures.Game;
import structures.chesses.Chess;

import java.util.ArrayList;

public class ComputerPlayer extends Player{
    static int[] blueDenPos = new int[]{9, 4};
    static int[] redDenPos = new int[]{1, 4};
    public ComputerPlayer() {
        super(2);
    }

    private static double pow(double x, double n) {
        if (x > 0) {
            return Math.pow(x, n);
        } else if (x < 0) {
            return -Math.pow(-x, n);
        } else {
            return 0;
        }
    }
    /**
     * 评估棋盘局面
     * @param chessboard
     * @return
     */
    public static double evaluateMap(Chessboard_NEW chessboard, int countLoop) {
        double ret = 0;

        int countBlueChess = 0;
        int countRedChess = 0;


        if (chessboard.getChess(blueDenPos) != null) {
            return -10000;
        }
        if (chessboard.getChess(redDenPos) != null) {
            return  10000;
        }

        ArrayList<ChessWithPos> blueAtkChess = new ArrayList<>();
        ArrayList<ChessWithPos> blueEnemyChess = new ArrayList<>();
        ArrayList<ChessWithPos> redAtkChess = new ArrayList<>();
        ArrayList<ChessWithPos> redEnemyChess = new ArrayList<>();

        for (int i = 1; i <= Chessboard_NEW.getSizeX(); i++) {
            for (int j = 1; j <= Chessboard_NEW.getSizeY(); j++) {
                int[] pos = new int[]{i, j};
                Chess chess = chessboard.getChess(pos);
                if (chess != null) {
                    // 获得进攻棋子
                    if (chess.getTeam() == 1) {
                        countBlueChess++;
                        putAtkChess(blueAtkChess, chess, pos);
                    } else {
                        countRedChess++;
                        putAtkChess(redAtkChess, chess, pos);
                    }
                    // 获得防守棋子
                    if (i < 5 && chess.getTeam() == 1) {
                        redEnemyChess.add(new ChessWithPos(chess, pos));
                    } else if (i > 5 && chess.getTeam() == -1) {
                        blueEnemyChess.add(new ChessWithPos(chess, pos));
                    }
                    // 老鼠河流价值
                    if (chess.getID() == 1 && Chessboard_NEW.getTerrain(pos).getId() == 10) {
                        ret += 3 * chess.getTeam();
                    }

                    // 存在价值
                    ret += chess.getTeam() * evaluateChess(chessboard, chess, pos);
                }
            }
        }

        // 进攻价值
        ret += evaluateAtkChess(countBlueChess, blueAtkChess, 1, countLoop);
        ret += evaluateAtkChess(countRedChess, redAtkChess, -1, countLoop);

        // 防守价值

        ret -= evaluateDefChess(chessboard, blueEnemyChess, 1);
        ret += evaluateDefChess(chessboard, redEnemyChess, -1);

        return ret;
    }

    /**
     * 有序放置进攻棋子
     * @param atkChess
     * @param chess
     * @param pos
     */
    private static void putAtkChess(ArrayList<ChessWithPos> atkChess, Chess chess, int[] pos) {
        if (atkChess.size() < 2) {
            if (atkChess.size() == 0) {
                atkChess.add(new ChessWithPos(chess, pos));
            } else if (atk(chess.getID()) < atk(atkChess.get(0).getChess().getID())){
                atkChess.add(new ChessWithPos(chess, pos));
            } else {
                atkChess.add(atkChess.get(0));
                atkChess.set(0, new ChessWithPos(chess, pos));
            }
        } else if (atk(chess.getID()) > atk(atkChess.get(0).getChess().getID())) {
            atkChess.set(1, atkChess.get(0));
            atkChess.set(0, new ChessWithPos(chess, pos));
        } else if (atk(chess.getID()) > atk(atkChess.get(1).getChess().getID())) {
            atkChess.set(1, new ChessWithPos(chess, pos));
        }
    }

    /**
     * 获得棋子进攻优先级
     * @param id
     * @return
     */
    private static int atk(int id) {
        int ret = id;
        if (id == 8) {
            ret = 1;
        }
        return ret;
    }
    private static int def(int id) {
        int ret = 9 - id;
        if (id == 1) {
            ret = 1;
        }
        return ret;
    }

    /**
     * 评估存在价值
     * @param chessboard
     * @param chess
     * @param pos
     * @return
     */
    private static double evaluateChess(Chessboard_NEW chessboard, Chess chess, int[] pos) {
        double ret;
        int initialValue = chess.getID();
        if (initialValue == 1) initialValue = 8;
        int distFromEnemy = getDistFromEnemy(chessboard, chess, pos);
        if (distFromEnemy != 1) {
            ret = (2 + initialValue) * (1 + Math.log(distFromEnemy));
        } else {
            ret = 0;
        }
//        System.out.printf("[%d] %s : %.2f\n", chess.getTeam(), chess.getChessName(), ret);
        return ret;
    }
    private static double evaluateAtkChess(int countChess, ArrayList<ChessWithPos> atkChesses, int team, int countLoop) {
        double ret = 0;
        if (countChess <= 3 && atkChesses.size() == 2) {
            atkChesses.remove(1);
        }
        for (ChessWithPos atkChessWithPos : atkChesses) {
            int[] atkPos = atkChessWithPos.getPos();
            int[] denPos;
            if (team == 1) {
                denPos = redDenPos;
            } else {
                denPos = blueDenPos;
            }
            double value = team * (-30 + pow(getDist(atkPos, denPos), 0.5) * 8);
            if (countLoop > 100) {
                value *= 3;
            }
            ret -= value;
//            System.out.printf("(atk) [%d] %s : %.2f\n", team, atkChessWithPos.getChess().getChessName(), value);
        }
        if (countChess <= 3) {
            ret *= 2;
        }
        return ret;
    }

    private static double evaluateDefChess(Chessboard_NEW chessboard, ArrayList<ChessWithPos> defChesses, int team) {
        double value = 0;
        for (ChessWithPos defChessWithPos : defChesses) {
            Chess enemyChess = defChessWithPos.getChess();
            int[] enemyPos = defChessWithPos.getPos();
            int distFromEnemy = getDistFromEnemy(chessboard, enemyChess, enemyPos);
            double defChessValue = pow(distFromEnemy, 0.5) * 4;
            ArrayList<int[]> defTrapPoses = new ArrayList<>();
            if (enemyPos[1] == 4) {
                defTrapPoses.add(new int[]{5 + team * 4, 3});
                defTrapPoses.add(new int[]{5 + team * 4, 5});
                defTrapPoses.add(new int[]{5 + team * 3, 4});
            } else if (enemyPos[1] < 4) {
                defTrapPoses.add(new int[]{5 + team * 4, 3});
                defTrapPoses.add(new int[]{5 + team * 3, 4});
            } else {
                defTrapPoses.add(new int[]{5 + team * 4, 5});
                defTrapPoses.add(new int[]{5 + team * 3, 4});
            }
            double defTrapValue = evaluateDefTrap(chessboard, defTrapPoses, team);
            if (enemyPos[1] == 4) {
                defTrapValue *= (double)2/3;
            }
            value += defChessValue + defTrapValue;
//            System.out.printf("[%d] counter%s : %.2f", team, enemyChess.getChessName(), value);
        }
        double ret = 0;
        if (value != 0) {
            ret = pow(value, 0.5);
        }
        return ret;
    }
    private static double evaluateDefTrap(Chessboard_NEW chessboard, ArrayList<int[]> trapPoses, int team) {
        double ret = 0;
        for (int[] trapPos : trapPoses) {
            int dist = getDistFromDefAnimal(chessboard, trapPos, team);
            ret -= pow(dist, 1.2) * 1.5;
        }
        return ret;
    }
    private static int getDistFromDefAnimal(Chessboard_NEW chessboard, int[] trapPos, int team) {
        int dist;
        OUT:
        for (dist = 1; dist < Chessboard_NEW.getSizeY() + Chessboard_NEW.getSizeX(); dist++) {
            for (int i = -dist; i <= dist; i++) {
                int j = dist - Math.abs(i);
                int[] nextPos = new int[]{trapPos[0] + i, trapPos[1] + j};
                if (!Chess.isOutOfBound(nextPos)) {
                    Chess nextPosChess = chessboard.getChess(nextPos);
                    if (nextPosChess != null && nextPosChess.getTeam() == team) {
                        break OUT;
                    }
                }
                j = -j;
                nextPos = new int[]{trapPos[0] + i, trapPos[1] + j};
                if (!Chess.isOutOfBound(nextPos)) {
                    Chess nextPosChess = chessboard.getChess(nextPos);
                    if (nextPosChess != null && nextPosChess.getTeam() == team) {
                        break OUT;
                    }
                }
            }
        }
        return dist;
    }
    /**
     * 获得最近天敌距离
     * @param chessboard
     * @param chess
     * @param pos
     * @return
     */
    private static int getDistFromEnemy(Chessboard_NEW chessboard, Chess chess, int[] pos) {
        int dist;
        OUT:
        for (dist = 1; dist < Chessboard_NEW.getSizeY() + Chessboard_NEW.getSizeX(); dist++) {
            for (int i = -dist; i <= dist; i++) {
                int j = dist - Math.abs(i);
                int[] nextPos = new int[]{pos[0] + i, pos[1] + j};
                if (whetherEnemy(chessboard, chess, nextPos)) break OUT;
                j = -j;
                nextPos = new int[]{pos[0] + i, pos[1] + j};
                if (whetherEnemy(chessboard, chess, nextPos)) break OUT;
            }
        }
        return dist;
    }

    private static boolean whetherEnemy(Chessboard_NEW chessboard, Chess chess, int[] nextPos) {
        if (!Chess.isOutOfBound(nextPos)) {
            Chess nextPosChess = chessboard.getChess(nextPos);
            if (nextPosChess != null && nextPosChess.getTeam() * chess.getTeam() == -1) {
                if (nextPosChess.ableToEat(chess.getCapacity(nextPos))) {
                    return nextPosChess.getID() != 1 || Chessboard_NEW.getTerrain(nextPos).getId() != 10;
                }
            }
        }
        return false;
    }

    public static int getDist(int[] posA, int[] posB) {
        return Math.abs(posA[0] - posB[0]) + Math.abs(posA[1] - posB[1]);
    }
    private static double removeLittleNumber(double input, int rank) {
        double littleNumber = pow(10, rank);
        return input - input % littleNumber;
    }
    public static void main(String[] args) {
        Chessboard_NEW chessboard = new Chessboard_NEW();
        chessboard.initBoard();
        chessboard.moveChess(new int[]{1, 1}, new int[]{2, 1});
        ComputerPlayer ai = new ComputerPlayer();
        double value = removeLittleNumber(ai.evaluateMap(chessboard, 0), -5);
//        System.out.println(value);

    }
}
// TODO: value值表存在缺陷，棋子不能阻挡进攻
/*
value值表：
棋子存在 : chessIndex (Rat : 8)
获得胜利 : MaxInteger / 2
棋子位置 {
    存在价值:
    // 老鼠的 initialValue = 8, 其余与编号相等
    +- (2 + initialValue) * (1 + ln(最近天敌距离))
    若与天敌距离为1, 则为0

    进攻价值:
    // 场上进攻型棋子为除大象外编号最大的两个棋子，若棋子总数小于等于3，则为最大的一个棋子
    -+ 距离敌方巢穴的距离 ^ 0.9 * 5.2

    防守价值:
    // 敌方进攻型棋子: 跨过半场的敌方棋子
    // 防守由两部分构成: 1.最近克制敌方的棋子的拦截 2.守住陷阱
    -+ 最近克制棋子与敌方棋子距离 ^ 0.5 * 4
    -+ 最近两个陷阱与最近子距离 ^ 1.2 * 1.5

}
 */