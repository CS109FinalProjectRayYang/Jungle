package structures;

import java.io.*;
import java.net.Socket;

public class Room {
    int roomID;
    int userNum = 0;
    String roomName;
    String userBlue = null;
    String userRed = null;
    Socket socketBlue, socketRed;
    public Room(int roomID, String roomName, String username, Socket socket) {
        this.roomID = roomID;
        this.roomName = roomName;
        if (Math.random() * 2 > 1) {
            userBlue = username;
            socketBlue = socket;
        } else {
            userRed = username;
            socketRed = socket;
        }
        userNum = 1;
    }
    public void joinRoom(String username, Socket socket) {
        if (!isFull()) {
            if (userRed == null) {
                userBlue = username;
                socketBlue = socket;
            } else {
                userRed = username;
                socketRed = socket;
            }
            userNum = 2;
        }
    }
    public void exitRoom(String username) {
        if (userRed.equals(username)) {
            userRed = null;
            socketRed = null;
        } else {
            userBlue = null;
            socketBlue = null;
        }
        userNum -= 1;
    }
    public boolean isFull() {
        return userNum == 2;
    }
    public boolean isWaiting() {
        return userNum == 1;
    }
    public boolean isEmpty() {
        return userNum == 0;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }
    public String getRoomMate() {
        if (userBlue == null) {
            return userRed;
        } else {
            return userBlue;
        }
    }

    private class Game extends Thread {
        public void run() {
            InputStream input;
            OutputStream output;
            BufferedReader readerBlue, readerRed;
            BufferedWriter writerBlue, writerRed;
            try {
                input = socketBlue.getInputStream();
                output = socketBlue.getOutputStream();
                readerBlue = new BufferedReader(new InputStreamReader(input));
                writerBlue = new BufferedWriter(new OutputStreamWriter(output));
                writerBlue.write(1);
                writerBlue.newLine();
                writerBlue.flush();

                input = socketRed.getInputStream();
                output = socketRed.getOutputStream();
                readerRed = new BufferedReader(new InputStreamReader(input));
                writerRed = new BufferedWriter(new OutputStreamWriter(output));
                writerRed.write(-1);
                writerRed.newLine();
                writerRed.flush();

                int nowPlayer = 1;
                Chessboard chessboard = new Chessboard();
                while (chessboard.isEnd() == 0) {
                    if (nowPlayer == 1) {
                        String command = readerBlue.readLine();
                        chessboard.takeAction(nowPlayer, command);

                        writerRed.write(command);
                        writerRed.newLine();
                        writerRed.flush();

                        nowPlayer = -1;
                    } else {
                        String command = readerRed.readLine();
                        chessboard.takeAction(nowPlayer, command);

                        writerBlue.write(command);
                        writerBlue.newLine();
                        writerBlue.flush();

                        nowPlayer = 1;
                    }
                }
                chessboard.save();
            } catch (IOException e) {
                System.out.println("Game is interrupted by illegal exit.");
                interrupt();
            }
        }
    }
}
