package structures;

import structures.players.HumanPlayer;
import structures.players.Player;

public class Game {
    Player playerBlue;
    Player playerRed;
    Chessboard_NEW chessboard;
    public Game() {

    }
    public Game(Player playerBlue, Player playerRed) {
        setPlayer(playerBlue, playerRed);
    }
    public void setPlayer(Player playerBlue, Player playerRed) {
        this.playerBlue = playerBlue;
        this.playerRed = playerRed;
    }
    public Player getPlayerBlue() {
        return playerBlue;
    }
    public Player getPlayerRed() {
        return playerRed;
    }
    public void start() {
        chessboard = new Chessboard_NEW();
        chessboard.initBoard();
        while (chessboard.isEnd() == 0) {

        }
    }
    public Chessboard_NEW getChessboard() {
        return chessboard;
    }
    public static void main(String[] args) {
        Game game = new Game(new HumanPlayer(), new HumanPlayer());
        game.start();
    }
}
