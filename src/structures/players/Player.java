package structures.players;

import structures.Chessboard;
import structures.Game;

public class Player {
    // 身份
    // 1 : 普通玩家
    // 2 : 电脑玩家
    // 3 : 网络玩家
    int identity;
    public Player(int identity) {
        this.identity = identity;
    }
    public void takeAction(Chessboard chessboard, int nowPlayer, Game game) {

    }
    public int getIdentity() {
        return identity;
    }
}
