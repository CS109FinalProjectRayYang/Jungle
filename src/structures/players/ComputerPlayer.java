package structures.players;

import structures.ChessWithPos;
import structures.Chessboard_NEW;
import structures.chesses.Chess;

import java.util.ArrayList;

public class ComputerPlayer extends Player{
    public ComputerPlayer() {
        super(2);
    }
    @Override
    public void takeAction(Chessboard_NEW chessboard, int nowPlayer) {

    }
    private double evaluateMap(Chessboard_NEW chessboard, int nowPlayer) {
        double ret = 0;
        int[] myDenPos = new int[]{5 + 4 * nowPlayer, 4};
        int[] EnemyDenPos = new int[]{5 - 4 * nowPlayer, 4};

        if (chessboard.getChess(myDenPos) != null) {
            return -10000;
        }
        if (chessboard.getChess(EnemyDenPos) != null) {
            return  10000;
        }

        ArrayList<ChessWithPos> myAtkChess = new ArrayList<>();
        ArrayList<ChessWithPos> myEnemyChess = new ArrayList<>();
        ArrayList<ChessWithPos> enemyAtkChess = new ArrayList<>();
        ArrayList<ChessWithPos> enemyMyChess = new ArrayList<>();

        for (int i = 0; i < Chessboard_NEW.getSizeX(); i++) {
            for (int j = 0; j < Chessboard_NEW.getSizeY(); j++) {
                int[] pos = new int[]{i, j};
                Chess chess = chessboard.getChess(pos);
                if (chess != null) {
                    if (chess.getTeam() * nowPlayer == 1) {
                        putAtkChess(myAtkChess, chess, pos);
                    } else {
                        putAtkChess(enemyAtkChess, chess, pos);
                    }
                    
                    ret += evaluateChess(chessboard, nowPlayer, chess, pos);
                }
            }
        }
        return ret;
    }
    private void putAtkChess(ArrayList<ChessWithPos> atkChess, Chess chess, int[] pos) {
        if (atkChess.size() < 2) {
            if (atkChess.size() == 0) {
                atkChess.add(new ChessWithPos(chess, pos));
            } else if (atk(chess.getID()) < atk(atkChess.get(0).getChess().getID())){
                atkChess.add(new ChessWithPos(chess, pos));
            } else {
                atkChess.add(atkChess.get(0));
                atkChess.set(0, new ChessWithPos(chess, pos));
            }
        } else if (atk(chess.getID()) > atk(atkChess.get(1).getChess().getID())) {
            atkChess.set(1, atkChess.get(1));
            atkChess.set(0, new ChessWithPos(chess, pos));
        }
    }
    private int atk(int id) {
        int ret = id;
        if (id == 8 || id == 1) {
            ret = 0;
        }
        return ret;
    }
    private double evaluateChess(Chessboard_NEW chessboard, int nowPlayer, Chess chess, int[] pos) {
        double ret;
        int initialValue = chess.getID();
        if (initialValue == 1) initialValue = 8;
        int distFromEnemy = getDistFromEnemy(chessboard, chess, pos);
        ret = nowPlayer * chess.getTeam() * initialValue * (1 + Math.log(distFromEnemy));
        return ret;
    }
    private int getDistFromEnemy(Chessboard_NEW chessboard, Chess chess, int[] pos) {
        int dist;
        OUT:
        for (dist = 1; dist < Chessboard_NEW.getSizeY() + Chessboard_NEW.getSizeX(); dist++) {
            for (int i = -dist; i <= dist; i++) {
                int j = dist - Math.abs(i);
                int[] nextPos = new int[]{pos[0] + i, pos[1] + j};
                Chess nextPosChess = chessboard.getChess(nextPos);
                if (nextPosChess != null && nextPosChess.getTeam() * chess.getTeam() == -1) {
                    break OUT;
                }
                j = -j;
                nextPos = new int[]{pos[0] + i, pos[1] + j};
                nextPosChess = chessboard.getChess(nextPos);
                if (nextPosChess != null && nextPosChess.getTeam() * chess.getTeam() == -1) {
                    break OUT;
                }
            }
        }
        return dist;
    }
}
/*
value值表：
棋子存在 : chessIndex (Rat : 8)
获得胜利 : MaxInteger / 2
棋子位置 {
    存在价值:
    // 老鼠的 initialValue = 8, 其余与编号相等
    +- initialValue * (1 + ln(最近天敌距离))
    若与天敌距离为1, 则为0

    进攻价值:
    // 场上进攻型棋子为除大象外编号最大的两个棋子，若棋子总数小于等于3，则为最大的一个棋子
    -+ 距离敌方巢穴的距离 ^ 0.9 * 3.2

    防守价值:
    // 敌方进攻型棋子: 跨过半场的敌方棋子
    // 防守由两部分构成: 1.最近克制敌方的棋子的拦截 2.弱子守住陷阱
    -+ 最近克制棋子与敌方棋子距离 ^ 0.5 * 4
    -+ 最近两个陷阱与最近弱子距离 ^ 1.2 * 1.5

}
 */