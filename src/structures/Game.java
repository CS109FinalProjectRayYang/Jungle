package structures;

import network.Client;
import structures.players.ComputerPlayer;
import structures.players.HumanPlayer;
import structures.players.Player;

import java.util.ArrayList;
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
    int countWaitingTime = 0;
    String receiveCommand;
    History history = new History();
    HashMap<Integer, String> nameMap = new HashMap<>();
    HashMap<Integer, Player> playerMap = new HashMap<>();
    ArrayList<String> messages = new ArrayList<>();
    public Game() {

    }
    public Game(Player playerBlue, Player playerRed) {
        setPlayer(playerBlue, playerRed);
    }
    public void setPlayer(Player playerBlue, Player playerRed) {
        this.playerBlue = playerBlue;
        this.playerRed = playerRed;
    }
    public void setHistory(History history) {
        this.history = history;
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
    public int getNowPlayer() {
        return nowPlayer;
    }
    public Player getPlayer() {
        if (nowPlayer == 1) {
            return playerBlue;
        } else {
            return playerRed;
        }
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
        while ((gameCondition = chessboard.isEnd()) == 0 && step < 1000) {

            Client.updateGamePaint();

            step++;
            // 打印棋盘
            chessboard.printBoard();
//          System.out.printf("final value: %.2f\n", ComputerPlayer.evaluateMap(chessboard, step / 2));
            System.out.printf("Waiting for %s...\n", nameMap.get(nowPlayer));

            Client.setNowPlayer(nowPlayer);
            Client.setPlayer(playerMap.get(nowPlayer));
            Client.updateGamePaint();

            int identity = playerMap.get(nowPlayer).getIdentity();

            if (identity == 1) {
                // 本地客户端回合，开放输入端口，等待Client输入
                inputted = false;
                while (!inputted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    countWaitingTime++;

                    // 计时
                    if (countWaitingTime % 10 == 1) {
                        int timeCountDown = 15 - countWaitingTime / 10;
//                        Client.setCountTime(nowPlayer, timeCountDown);
                    }
                    if (countWaitingTime == 150) {

                    }
                }
            } else if (identity == 2) {
                // 电脑回合，等待电脑计算
                playerMap.get(nowPlayer).takeAction(chessboard, nowPlayer, this);
            } else if (identity == 3) {
                // 对手回合，开放输入端口，等待网络输入
                inputted = false;
                while (!inputted) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            // 打印输入状态，转换攻方
            System.out.printf("%s: %s\n", nameMap.get(nowPlayer), receiveCommand);


            nowPlayer = -nowPlayer;
        }


        // 如果游戏结束
        chessboard.printBoard();
        nowPlayer = 0;

        Client.setNowPlayer(nowPlayer);
        Client.winPaint(gameCondition);

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
        messages.add("[%s] %s".formatted(nameMap.get(nowPlayer), command));
    }
    public void messageInput(String line) {
        messages.add("[local] %s".formatted(line));
    }
    public void messageInput(String line, int nowPlayer) {
        messages.add("[%s] %s".formatted(nameMap.get(nowPlayer), line));
    }
    public String[] getMessages() {
        String[] ret = new String[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
            ret[i] = messages.get(i);
        }
        return ret;
    }
    private void addHistory(int[] pos, int[] nextPos) {
        history.addHistory(pos, nextPos);
    }
    public History getHistory() {
        return history;
    }
    public int[][] getHistory(int step) {
        return history.getHistory(step);
    }
    public void buildFromHistory() {
        buildFromHistory(history);
    }
    public void buildFromHistory(int delSize) {
        history.delHistory(delSize);
        buildFromHistory(history);
    }
    public void buildFromHistory(History history) {
        this.history = history;
        chessboard.initBoard();
        for (int i = 1; i <= history.getSize(); i++) {
            int[][] nowStep = history.getHistory(i);
            chessboard.moveChess(nowStep[0], nowStep[1]);
        }
        chessboard.printBoard();
        updateNowPlayer();
        System.out.printf("Waiting for %s...\n", nameMap.get(nowPlayer));
    }

    public void setHistorySize(int historySize) {
        history.setSize(historySize);
        buildFromHistory();
    }

    public int getHistorySize() {
        return history.getSize();
    }

    public Chessboard_NEW getChessboard() {
        return chessboard;
    }
    private void updateNowPlayer() {
        int flag = getHistorySize() % 2;
        if (flag == 0) {
            nowPlayer = 1;
        } else {
            nowPlayer = -1;
        }
        Client.setNowPlayer(nowPlayer);
    }
}