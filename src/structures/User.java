package structures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class User {
    private String username;
    private String password;
    private int gameNum;
    private int winNum;
    private double winningRate;
    public User(String username, String password, int gameNum, int winNum) {
        this.username = username;
        this.password = password;
        this.gameNum = gameNum;
        this.winNum = winNum;
        winningRate = (double) winNum / gameNum;
    }
    public boolean judgePassword(String password) {
        return this.password.equals(password);
    }
    public void save() {
        String path = "user/%s/%s.txt".formatted(username, username);
        File fUser = new File(path);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fUser));
            writer.write(username);
            writer.newLine();
            writer.write(password);
            writer.newLine();
            writer.write(gameNum);
            writer.newLine();
            writer.write(winNum);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.printf("%s's fUser don't exist!", username);
        }
    }
    public void addWinNum() {
        winNum++;
    }
}
