package structures;

import java.io.*;

public class History {
    String username;
    String counterUser;
    int stepNum;
    int step;
    int playerIndex;
    String status;
    int[][] history;
    public History(String username, int playerIndex, String counterUser, int[][] history, String status) {
        this.username = username;
        this.playerIndex = playerIndex;
        this.counterUser = counterUser;
        this.history = history;
        this.stepNum = history.length;
        step = 0;
    }
    public static History[] getHistories(String username, int historyNum) {
        History[] histories = new History[historyNum];
        for (int i = 0; i < historyNum; i++) {
            String path = "user/%s/history/%d.txt".formatted(username, i);
            histories[i] = getHistory(path, username);
        }
        return histories;
    }
    public static History getHistory(String path, String username) {
        File fHistory = new File(path);
        History ret = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fHistory));
            int playerIndex = Integer.parseInt(reader.readLine());
            String counterUser = reader.readLine();
            int stepNum = Integer.parseInt(reader.readLine());
            int[][] history = new int[stepNum][2];
            for (int i = 0; i < stepNum; i++) {
                String line = reader.readLine();
                String[] elements = line.split(" ");
                history[i][0] = Integer.parseInt(elements[0]);
                history[i][1] = Integer.parseInt(elements[1]);
            }
            String status = reader.readLine();
            ret = new History(username, playerIndex, counterUser, history, status);
        } catch (IOException ignore) {}
        return ret;
    }
    public boolean hasNextStep() {
        return step < stepNum;
    }
    public boolean hasPrevStep() {
        return step > 0;
    }
    public int[] getStep(int step) {
        this.step = step;
        return history[step];
    }
    public int[] nextStep() {
        return history[step++];
    }
    public int[] prevStep() {
        return history[--step];
    }
    public void saveHistory(int historyIndex) {
        String path = "user/%s/history/%d.txt".formatted(username, historyIndex);
        File fHistory = new File(path);
        try {
            fHistory.createNewFile();
        } catch (IOException ignored) {}
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fHistory));
            writer.write(playerIndex);
            writer.newLine();
            writer.write(counterUser);
            writer.newLine();
            writer.write(stepNum);
            writer.newLine();
            for (int i = 0; i < stepNum; i++) {
                writer.write("%d %d".formatted(history[i][0], history[i][1]));
                writer.newLine();
            }
            writer.write(status);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException ignore) {}

    }
}
