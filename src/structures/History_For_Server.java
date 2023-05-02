package structures;

import java.io.*;

public class History_For_Server {
    String username;
    String counterUser;
    int size;
    int nowStep;
    int playerIndex;
    String status;
    int[][][] history;
    public History_For_Server(String username, int playerIndex, String counterUser, int[][][] history, String status) {
        this.username = username;
        this.playerIndex = playerIndex;
        this.counterUser = counterUser;
        this.history = history;
        this.size = history.length;
        this.status = status;
        nowStep = 0;
    }
    public static History_For_Server[] getHistories(String username, int historyNum) {
        History_For_Server[] histories = new History_For_Server[historyNum];
        for (int i = 0; i < historyNum; i++) {
            String path = "user/%s/history/%d.txt".formatted(username, i);
            histories[i] = getHistory(path, username);
        }
        return histories;
    }
    public static History_For_Server getHistory(String path, String username) {
        File fHistory = new File(path);
        History_For_Server ret = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fHistory));
            int playerIndex = Integer.parseInt(reader.readLine());
            String counterUser = reader.readLine();
            int stepNum = Integer.parseInt(reader.readLine());
            int[][][] history = new int[stepNum][2][2];
            for (int i = 0; i < stepNum; i++) {
                String line = reader.readLine();
                String[] elements = line.split(" ");
                history[i][0][0] = Integer.parseInt(elements[0]);
                history[i][0][1] = Integer.parseInt(elements[1]);
                history[i][1][0] = Integer.parseInt(elements[2]);
                history[i][1][1] = Integer.parseInt(elements[3]);
            }
            String status = reader.readLine();
            ret = new History_For_Server(username, playerIndex, counterUser, history, status);
        } catch (IOException ignore) {}
        return ret;
    }
    public boolean hasNextStep() {
        return nowStep < size;
    }
    public boolean hasPrevStep() {
        return nowStep > 0;
    }
    public int[][] getStep(int step) {
        this.nowStep = step;
        return history[step];
    }
    public int[][] nextStep() {
        return history[nowStep++];
    }
    public int[][] prevStep() {
        return history[--nowStep];
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
            writer.write(size);
            writer.newLine();
            for (int i = 0; i < size; i++) {
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
