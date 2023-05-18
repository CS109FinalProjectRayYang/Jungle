package structures;

public class Observer {
    History history;
    Chessboard chessboard;
    public Observer(History history) {
        this.history = history;
    }
    public void setChessboard(int step) throws Exception{
        chessboard = new Chessboard();
        chessboard.initBoard();

        if (step > history.size || step < 1) {
            throw new Exception();
        }

        for (int i = 1; i <= step; i++) {
            chessboard.moveChess(history.getHistory(i)[0], history.getHistory(i)[1]);
        }
    }
    public Chessboard getChessboard() {
        return chessboard;
    }
    public int getSize() {
        return history.size;
    }
}
