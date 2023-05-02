package structures;

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
