package structures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class History {
    int[][][] history = new int[3000][2][2];
    int size;
    public History() {
        size = 0;
    }
    public History(int[][][] history, int size) {
        this.history = history;
        this.size = size;
    }
    public History(File fHistory) {
        try {
            BufferedReader fHistoryReader = new BufferedReader(new FileReader(fHistory));
            int size = Integer.parseInt(fHistoryReader.readLine());
            this.size = size;
            for (int i = 1; i <= size; i++) {
                String line = fHistoryReader.readLine();
                String[] elements = line.split(" ");

                int[] testPos = new int[]{Integer.parseInt(elements[0]), Integer.parseInt(elements[1])};
                int[] testNextPos = new int[]{Integer.parseInt(elements[2]), Integer.parseInt(elements[3])};

                history[i][0] = testPos;
                history[i][1] = testNextPos;
            }
        } catch (Exception ignore) {

        }
    }
    public int[][] getHistory(int step) {
        return history[step];
    }
    public void addHistory(int[] pos, int[] nextPos) {
        history[++size] = new int[][]{pos, nextPos};
    }
    public void setHistory(int step, int[] pos, int[] nextPos) {
        history[step] = new int[][]{pos, nextPos};
    }
    public void setSize(int size) {
        this.size = size;
        if (this.size < 0) this.size = 0;
    }
    public void delHistory(int delSize) {
        size -= delSize;
        if (size < 0) size = 0;
    }
    public int getSize() {
        return size;
    }
}
