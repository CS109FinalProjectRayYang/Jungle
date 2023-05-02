package structures;

import network.Client;
import structures.players.ComputerPlayer;
import structures.players.HumanPlayer;
import structures.players.Player;

import java.util.HashMap;

public class Game {
    boolean withClient = true;
    int step = 0;
    Player playerBlue;
    Player playerRed;
    int gameCondition;
    Chessboard_NEW chessboard;
    boolean inputted = true;
    int nowPlayer = 1;
    String receiveCommand;
    int[][][] history = new int[1000][2][2];
    int historySize = 0;
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
    public void withOutClient() {
        withClient = false;
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
        if (withClient) {
            System.out.println("Game Started!");
        }
        // 在未分出胜负之前循环执行
        while ((gameCondition = chessboard.isEnd()) == 0 && step < 1000) {
            step++;
            // 打印棋盘
            if (withClient) {
                chessboard.printBoard();
                System.out.printf("final value: %.2f\n", ComputerPlayer.evaluateMap(chessboard, step / 2));
                System.out.printf("Waiting for %s...\n", nameMap.get(nowPlayer));
            }

            if (withClient) {
                Client.setNowPlayer(nowPlayer);
            }
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

            if (withClient) {
                // 打印输入状态，转换攻方
                System.out.printf("%s: %s\n", nameMap.get(nowPlayer), receiveCommand);
            }
            nowPlayer = -nowPlayer;
        }


        // 如果游戏结束
        chessboard.printBoard();
        nowPlayer = 0;
        if (withClient) {
            Client.setNowPlayer(nowPlayer);
            Client.winPaint(gameCondition);
        }
        if (gameCondition != 0) {
            System.out.printf("%s Wins!\n", nameMap.get(gameCondition));
        } else {
            System.out.println("Game Draw!");
        }
    }

    public int getGameCondition() {
        return gameCondition;
    }
    public boolean getInputted() {
        return inputted;
    }

    public void input(int[] pos, int[] nextPos, String command) {
        chessboard.moveChess(pos, nextPos);
        addHistory(pos, nextPos);
        inputted = true;
        receiveCommand = command;
    }
    private void addHistory(int[] pos, int[] nextPos) {
        history[++historySize] = new int[][]{pos, nextPos};
    }
    public void buildFromHistory(int size) {
        setHistorySize(size);
        buildFromHistory();
    }
    public void buildFromHistory() {
        chessboard.initBoard();
        for (int i = 1; i <= historySize; i++) {
            chessboard.moveChess(history[i][0], history[i][1]);
        }
        chessboard.printBoard();
    }

    public void setHistorySize(int historySize) {
        this.historySize = Math.max(historySize, 0);
    }

    public int getHistorySize() {
        return historySize;
    }

    public Chessboard_NEW getChessboard() {
        return chessboard;
    }
}