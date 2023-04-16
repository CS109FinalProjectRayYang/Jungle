package structures.chesses;

import structures.Chessboard_OLD;
import structures.Chessboard_NEW;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Chess {
    private int chessID;
    private int team;
    private int capacity;
    private String chessName;
    private boolean[] foodChain;
    private ImageIcon imgBlue;
    private ImageIcon imgRed;
    private ImageIcon imgBlueGrey;
    private ImageIcon imgRedGrey;
    private Chessboard_NEW chessboard;
    public Chess(int team, int chessID, String chessName, Chessboard_NEW chessboard) {
        this.team = team;
        this.chessID = chessID;
        capacity = chessID;
        this.chessName = chessName;
        imgBlue = new ImageIcon("data/img/animals/blue/%d.png".formatted(chessID));
        imgRed = new ImageIcon("data/img/animals/red/%d.png".formatted(chessID));
        imgBlueGrey = new ImageIcon("data/img/animals/blue/grey/%d.png".formatted(chessID));
        imgRedGrey = new ImageIcon("data/img/animals/red/grey/%d.png".formatted(chessID));
        this.chessboard = chessboard;
        foodChain = new boolean[9];
        for (int i = 0; i < 9; i++) {
            foodChain[i] = i < chessID;
        }
        if (chessID == 1) {
            foodChain[8] = true;
        }
    }

    public int getID() {
        return chessID;
    }
    public int getTeam() {
        return team;
    }
    public int getCapacity() {
        return capacity;
    }

    public String getChessName() {
        return chessName;
    }

    public ImageIcon getImg() {
        if (team == 1) {
            return imgBlue;
        } else {
            return imgRed;
        }
    }
    public ImageIcon getGreyImg() {
        if (team == 1) {
            return imgBlueGrey;
        } else {
            return imgRedGrey;
        }
    }
    public Chessboard_NEW getChessboard() {
        return chessboard;
    }

    public ArrayList<int[]> getLegalMove(int[] pos) {
        ArrayList<int[]> ret = new ArrayList<>();
        ArrayList<int[]> moves = getMoves();

        for (int[] move : moves) {
            if (isLegal(pos, move)) {
                ret.add(new int[]{pos[0] + move[0], pos[1] + move[1]});
            }
        }
        return ret;
    }
    public ArrayList<int[]> getLegalMove_Jump(int[] pos) {
        ArrayList<int[]> ret = new ArrayList<>();
        ArrayList<int[]> moves = Chess.getMoves();

        modifyMoves(pos, moves);
        for (int[] move : moves) {
            if (isLegal(pos, move)) {
                ret.add(new int[]{pos[0] + move[0], pos[1] + move[1]});
            }
        }
        return ret;
    }
    public boolean isLegal(int[] pos, int[] move) {
        boolean ret = true;
        int[] nextPos = new int[]{pos[0] + move[0], pos[1] + move[1]};
        if (isOutOfBound(nextPos) || isInRiver(nextPos)) {
            ret = false;
        } else if (chessboard.getChess(nextPos) != null) {
            ret = foodChain[chessboard.getChess(nextPos).getCapacity()];
        }
        return ret;
    }
    public boolean isOutOfBound(int[] pos) {
        boolean ret = false;
        if (pos[0] <= 0 || pos[0] > Chessboard_NEW.getSizeX() || pos[1] <= 0 || pos[1] > Chessboard_NEW.getSizeY()) {
            ret = true;
        }
        return ret;
    }
    private boolean isInRiver(int[] pos) {
        boolean ret = false;
        if (Chessboard_NEW.getTerrain(pos).getId() == 10) {
            ret = true;
        }
        return ret;
    }
    public void modifyMoves(int[] pos, ArrayList<int[]> moves) {
        for (int k = 0; k < moves.size(); k++) {
            int[] nextPos = new int[]{pos[0] + moves.get(k)[0], pos[1] + moves.get(k)[1]};
            if (!isOutOfBound(nextPos)) {
                if (Chessboard_NEW.getTerrain(nextPos).getId() == 10) {
                    int i;
                    for (i = 1; i <= 3; i++) {
                        nextPos = new int[]{pos[0] + i * moves.get(k)[0], pos[1] + i * moves.get(k)[1]};
                        if (Chessboard_NEW.getTerrain(nextPos).getId() != 10) {
                            break;
                        } else if (getChessboard().getChess(nextPos) != null) {
                            break;
                        }
                    }
                    moves.set(k, new int[]{i * moves.get(k)[0], i * moves.get(k)[1]});
                }
            }
        }
    }
    public static ArrayList<int[]> getMoves() {
        ArrayList<int[]> moves = new ArrayList<>();

        int[] moveLeft = new int[]{-1, 0};
        int[] moveRight = new int[]{1, 0};
        int[] moveUp = new int[]{0, 1};
        int[] moveDown = new int[]{0, -1};

        moves.add(moveLeft);
        moves.add(moveRight);
        moves.add(moveUp);
        moves.add(moveDown);

        return moves;
    }
}
