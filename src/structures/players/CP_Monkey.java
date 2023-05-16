package structures.players;

import structures.Chessboard_NEW;
import structures.Game;
import structures.chesses.Chess;

public class CP_Monkey extends ComputerPlayer{
    @Override
    public void takeAction(Chessboard_NEW chessboard, int nowPlayer, Game game) {
        int[] nowPos = new int[2];
        int[] nextPos = new int[2];
        Chess chessNow = chessboard.getChess(nowPos);
        boolean isMove = false;
        for(int i = 0; i <= chessNow.getLegalMove(nowPos).size()-1; i++){
            if(chessboard.getChess(chessNow.getLegalMove(nowPos).get(i)) != null){
                isMove = true;
                nextPos[0] = chessNow.getLegalMove(nowPos).get(i)[0];
                nextPos[1] = chessNow.getLegalMove(nowPos).get(i)[1];
            }
        }
        if(!isMove){
            nextPos[0] = chessNow.getLegalMove(nowPos).get(0)[0];
            nextPos[1] = chessNow.getLegalMove(nowPos).get(0)[1];
        }

        /*

        代码写在这里，需要移动的棋子位置是nowPos，移动前往的位置是nextPos
        你只需要把这两个位置填上就好

         */


        game.input(nowPos, nextPos, "%s: (%d, %d) -> (%d, %d)\n".formatted(game.getChessboard().getChess(nowPos).getChessName(), nowPos[0], nowPos[1], nextPos[0], nextPos[1]));
    }
}
