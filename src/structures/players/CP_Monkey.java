package structures.players;

import structures.ChessWithPos;
import structures.Chessboard_NEW;
import structures.Game;
import structures.chesses.Chess;


import java.util.ArrayList;

public class CP_Monkey extends ComputerPlayer{
    @Override
    public void takeAction(Chessboard_NEW chessboard, int nowPlayer, Game game) {
        int[] nowPos;
        int[] nextPos = new int[2];
        ArrayList<ChessWithPos> myChessOK = new ArrayList<>();//可以走的所有棋子
        myChessOK = chessboard.getChessesOfTeam(nowPlayer);
        int randmom = 1 + (int)(Math.random() * myChessOK.size());//随机选棋子
        nowPos = myChessOK.get(randmom - 1).getPos();
        Chess chessNow = chessboard.getChess(nowPos);
        boolean isMove = false;
//        for(int i = 0; i <= chessNow.getLegalMove(nowPos).size()-1; i++){
//            if(chessboard.getChess(chessNow.getLegalMove(nowPos).get(i)) != null){
//                isMove = true;
//                nextPos[0] = chessNow.getLegalMove(nowPos).get(i)[0];
//                nextPos[1] = chessNow.getLegalMove(nowPos).get(i)[1];
//            }
//        }
        int count = 0;
        while(!isMove && count <= 10){//给10次机会选随机
            int randmom2 = (int)(Math.random() * (chessNow.getLegalMove(nowPos).size()));//随机选一步走
            count++;
            if(chessboard.getChess(chessNow.getLegalMove(nowPos).get(randmom2)) != null){
                isMove = true;
                nextPos[0] = chessNow.getLegalMove(nowPos).get(randmom2)[0];
                nextPos[1] = chessNow.getLegalMove(nowPos).get(randmom2)[1];
            }
        }

        if(!isMove){//实在选不出就走第一个
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
