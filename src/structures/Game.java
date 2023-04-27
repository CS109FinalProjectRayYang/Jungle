package structures;

import network.Client;
import structures.players.HumanPlayer;
import structures.players.Player;

import java.util.HashMap;

public class Game {
    Player playerBlue;
    Player playerRed;
    Chessboard_NEW chessboard;
    boolean inputted = true;
    int nowPlayer = 1;
    String receiveCommand;
    HashMap<Integer, String> nameMap = new HashMap<>();
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
        nameMap.put(1, "playerBlue");
        nameMap.put(-1, "playerRed");
        chessboard = new Chessboard_NEW();
        chessboard.initBoard();
        System.out.println("Game Started!");
        while (chessboard.isEnd() == 0) {
            chessboard.printBoard();
            System.out.printf("Waiting for %s...\n", nameMap.get(nowPlayer));
            inputted = false;
            Client.setNowPlayer(nowPlayer);
            while (!inputted) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            System.out.printf("%s: %s\n", nameMap.get(nowPlayer), receiveCommand);
            nowPlayer = -nowPlayer;
        }
    }

    public boolean getInputted() {
        return inputted;
    }

    public void input(String command) {
        inputted = true;
        receiveCommand = command;
    }
    public Chessboard_NEW getChessboard() {
        return chessboard;
    }
}
