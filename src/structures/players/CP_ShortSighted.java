package structures.players;

import structures.Chessboard_NEW;
import structures.Game;
import structures.chesses.Chess;

import java.util.ArrayList;
import java.util.Arrays;

public class CP_ShortSighted extends ComputerPlayer{
    int[] lastChessPos;
    boolean hasSomethingToEat;
    @Override
    public void takeAction(Chessboard_NEW chessboard, int nowPlayer, Game game) {
        hasSomethingToEat = false;
        double maxValue = - nowPlayer * 100000;
        int[] maxPos = new int[2];
        int[] maxNextPos = new int[2];
        OUT:
        for (int i = 1; i <= Chessboard_NEW.getSizeX(); i++) {
            for (int j = 1; j <= Chessboard_NEW.getSizeY(); j++) {
                int[] pos = new int[]{i, j};
                Chess chessOnPos = chessboard.getChess(pos);
                if (chessOnPos != null && chessOnPos.getTeam() == nowPlayer) {
                    ArrayList<int[]> legalMoves = chessOnPos.getLegalMove(pos);
                    for (int[] nextPos : legalMoves) {
                        if (chessboard.getChess(3, 5) != null && chessboard.getChess(3, 5).getChessName().equals("Dog")) {
                            System.out.println("break point");
                        }
                        Chessboard_NEW chessboardNew = new Chessboard_NEW(chessboard);
                        chessboardNew.moveChess(pos, nextPos);
                        double value = evaluateMap(chessboardNew, 0);
                        if (value * nowPlayer > maxValue * nowPlayer || chessboard.getChess(nextPos) != null) {
                            if (chessboard.getChess(nextPos) != null || !hasSomethingToEat || Chessboard_NEW.getTerrain(nextPos).getId() == 30) {
                                maxValue = value;
                                maxPos = pos;
                                maxNextPos = nextPos;
                            }
                            if (chessboard.getChess(nextPos) != null) {
                                hasSomethingToEat = true;
                            }
                        }
                    }
                }
            }
        }


        lastChessPos = maxNextPos;
//        System.out.println(maxPos[0] + " " + maxPos[1]);
//        System.out.println(maxNextPos[0] + " " + maxNextPos[1]);
        System.out.println(maxPos[0] + " " + maxPos[1]);
        game.input(maxPos, maxNextPos, "%s: (%d, %d) -> (%d, %d)".formatted(game.getChessboard().getChess(maxPos).getChessName(), maxPos[0], maxPos[1], maxNextPos[0], maxNextPos[1]));

    }//哪个位置上的棋子  移到哪个位置上
}
