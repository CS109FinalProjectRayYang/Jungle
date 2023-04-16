package structures.chesses;

import structures.Chessboard_NEW;

public class Elephant extends Chess {
    public Elephant(int team, Chessboard_NEW chessboard) {
        super(team, 8, "Elephant", chessboard);
    }
    // 不重写getLegalMove方法，则在调用elephant.getLegalMove时会自动去调用Chess类里的getLegalMove方法
}
