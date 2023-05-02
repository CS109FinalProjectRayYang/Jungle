package structures.players;

import structures.Chessboard_NEW;
import structures.Game;
import structures.chesses.Chess;

import java.util.ArrayList;
import java.util.Arrays;

public class CP_ShortSighted extends ComputerPlayer{
    int[] lastChessPos;
    int countDeadLoop = 0;
    @Override
    public void takeAction(Chessboard_NEW chessboard, int nowPlayer, Game game) {
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
                            double value = evaluateMap(chessboardNew, 0);
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
}
