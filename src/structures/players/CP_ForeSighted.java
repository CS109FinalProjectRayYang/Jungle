package structures.players;

import structures.Chessboard_NEW;
import structures.Game;
import structures.chesses.Chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CP_ForeSighted extends ComputerPlayer {
    Random ran = new Random();
    private int overCount;
    int[] lastChessPos;
    int countDeadLoop = 0;
    int countLoop = 0;
    @Override
    public void takeAction(Chessboard_NEW chessboard, int nowPlayer, Game game) {
        overCount = 0;
        countLoop++;
        double maxValue = - nowPlayer * 100000;
        int[] maxPos = new int[2];
        int[] maxNextPos = new int[2];
        for (int i = 1; i <= Chessboard_NEW.getSizeX(); i++) {
            for (int j = 1; j <= Chessboard_NEW.getSizeY(); j++) {
                int[] pos = new int[]{i, j};
                if (countDeadLoop < 7 || !Arrays.equals(pos, lastChessPos)) {
                    Chess chessOnPos = chessboard.getChess(pos);
                    if (chessOnPos != null && chessOnPos.getTeam() == nowPlayer) {
                        ArrayList<int[]> legalMoves = chessOnPos.getLegalMove(pos);
                        for (int[] nextPos : legalMoves) {
                            Chessboard_NEW chessboardNew = new Chessboard_NEW(chessboard);
                            chessboardNew.moveChess(pos, nextPos);
                            double value = search(chessboardNew, -nowPlayer, -100000, 100000, 1);
                            if (value * nowPlayer > maxValue * nowPlayer) {
                                maxValue = value;
                                maxPos = pos;
                                maxNextPos = nextPos;
                            }
                        }
                    }
                }
            }
        }
        if (Arrays.equals(maxPos, lastChessPos)) {
            countDeadLoop++;
        } else {
            countDeadLoop = 0;
        }
        lastChessPos = maxNextPos;
        game.input(maxPos, maxNextPos, "%s: (%d, %d) -> (%d, %d)".formatted(game.getChessboard().getChess(maxPos).getChessName(), maxPos[0], maxPos[1], maxNextPos[0], maxNextPos[1]));
    }
    private double search(Chessboard_NEW chessboard, int nowPlayer, double alpha, double beta, int countTurn) {
        overCount++;
        if (overCount % 100000 == 0) {
            System.out.printf("Searching for %d hundred thousand times\n", overCount / 100000);
        }
        if (chessboard.isEnd() != 0) {
            return chessboard.isEnd() * 10000;
        } else if (countTurn == 5) {
            return evaluateMap(chessboard, countLoop);
        } else {
            double ret = -nowPlayer * 100000;

            OUT:
            for (int i = 1; i <= Chessboard_NEW.getSizeX(); i++) {
                for (int j = 1; j <= Chessboard_NEW.getSizeY(); j++) {
                    int[] pos = new int[]{i, j};
                    Chess chessOnPos = chessboard.getChess(pos);
                    if (chessOnPos != null && chessOnPos.getTeam() == nowPlayer) {
                        ArrayList<int[]> legalMoves = chessOnPos.getLegalMove(pos);
                        for (int[] nextPos : legalMoves) {
                            Chessboard_NEW chessboardNew = new Chessboard_NEW(chessboard);
                            chessboardNew.moveChess(pos, nextPos);
                            double value = search(chessboardNew, -nowPlayer, alpha, beta, countTurn + 1);
                            double noise = ran.nextDouble(0.02) - 0.01;
                            value *= (1 + noise);

                            if (nowPlayer == 1) {
                                if (value > alpha) {
                                    alpha = value;
                                    ret = value;
                                }
                                if (value > beta) {
                                    break OUT;
                                }
                            } else {
                                if (value < beta) {
                                    beta = value;
                                    ret = value;
                                }
                                if (value < alpha) {
                                    break OUT;
                                }
                            }

                        }
                    }
                }
            }

            return ret;
        }
    }
}
// TODO: 进攻棋子缺少追击敌方棋子的动力，在棋局陷入僵局后，AI棋手可能会陷入防守棋子的无意义移动黑洞中