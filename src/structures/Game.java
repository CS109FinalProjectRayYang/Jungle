package structures;

import network.Client;
import structures.players.ComputerPlayer;
import structures.players.HumanPlayer;
import structures.players.Player;

import java.util.HashMap;

public class Game {
    Player playerBlue;
    Player playerRed;
    int gameCondition;
    Chessboard_NEW chessboard;
    boolean inputted = true;
    int nowPlayer = 1;
    String receiveCommand;
    HashMap<Integer, String> nameMap = new HashMap<>();
    HashMap<Integer, Player> playerMap = new HashMap<>();
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

        playerMap.put(1, playerBlue);
        playerMap.put(-1, playerRed);

        // 新建并初始化棋盘
        chessboard = new Chessboard_NEW();
        chessboard.initBoard();

        // 游戏开始
        nowPlayer = 1;
        System.out.println("Game Started!");

        // 在未分出胜负之前循环执行
        while ((gameCondition = chessboard.isEnd()) == 0) {

            // 打印棋盘
            chessboard.printBoard();

            System.out.printf("final value: %.2f\n", ComputerPlayer.evaluateMap(chessboard));

            System.out.printf("Waiting for %s...\n", nameMap.get(nowPlayer));
            Client.setNowPlayer(nowPlayer);
            int identity = playerMap.get(nowPlayer).getIdentity();

            if (identity == 1) {
                // 开放输入端口，等待输入
                inputted = false;
                while (!inputted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
            } else if (identity == 2) {
                playerMap.get(nowPlayer).takeAction(chessboard, nowPlayer, this);
            } else if (identity == 3) {

            }

            // 打印输入状态，转换攻方
            System.out.printf("%s: %s\n", nameMap.get(nowPlayer), receiveCommand);
            nowPlayer = -nowPlayer;
        }


        // 如果游戏结束
        nowPlayer = 0;
        Client.setNowPlayer(nowPlayer);
        Client.winPaint(gameCondition);
        System.out.printf("%s Wins!", nameMap.get(gameCondition));
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
