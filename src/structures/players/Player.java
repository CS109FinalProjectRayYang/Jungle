package structures.players;

import structures.Chessboard_NEW;
import structures.Chessboard_OLD;

public class Player {
    // 身份
    // 1 : 普通玩家
    // 2 : 电脑玩家
    // 3 : 网络玩家
    int identity;
    public Player(int identity) {
        this.identity = identity;
    }
    public void takeAction(Chessboard_NEW chessboard, int nowPlayer) {

    }
    public int getIdentity() {
        return identity;
    }
}
