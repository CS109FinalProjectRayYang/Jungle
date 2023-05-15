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
    boolean isInputted = false;
    String command;
    public Room(int roomID, String roomName, String username, Socket socket) {
        this.roomID = roomID;
        this.roomName = roomName;
        userBlue = username;
        socketBlue = socket;
        userNum = 1;
    }
    public void joinRoom(String username, Socket socket) {
        if (!isFull()) {
            userRed = username;
            socketRed = socket;
            userNum = 2;
        }
    }
    public void exitRoom(String username) {
        userNum = 0;
    }
    public void checkRoom() {
        boolean flag = true;
        if (socketBlue != null) {
            try{
                socketBlue.sendUrgentData(0xFF);
            }catch(Exception ex){
                flag = false;
            }
        } else if (socketRed != null) {
            try{
                socketRed.sendUrgentData(0xFF);
            }catch(Exception ex){
                flag = false;
            }
        }
        if (!flag) {
            userNum = 0;
        }
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
    public void inform() throws Exception{
        BufferedWriter writerBlue = new BufferedWriter(new OutputStreamWriter(socketBlue.getOutputStream()));
        writerBlue.write("gameBegin");
        writerBlue.newLine();
        writerBlue.flush();
    }

    public void beginGame() {
        Game game = new Game(this);
        try {
            game.start();
        } catch (Exception ignore) {
            checkRoom();
        }
    }
    public void removeAll() {
        userNum = 0;
        try {
            socketBlue.close();
            socketRed.close();
        } catch (Exception ignore) {

        }
    }
    public void input(String command) {
        isInputted = true;
        this.command = command;
    }

    private class Game extends Thread {
        Room room;
        public Game(Room room) {
            this.room = room;
        }
        public void run() {
            OutputStream output;
            BufferedWriter writerBlue, writerRed;
            try {
                output = socketBlue.getOutputStream();
                writerBlue = new BufferedWriter(new OutputStreamWriter(output));

                output = socketRed.getOutputStream();
                writerRed = new BufferedWriter(new OutputStreamWriter(output));

                int nowPlayer = 1;
                Chessboard_OLD chessboard = new Chessboard_OLD();
                while (chessboard.isEnd() == 0) {
                    if (nowPlayer == 1) {
                        isInputted = false;
                        while (!isInputted) {
                            Thread.sleep(100);
                        }


                        chessboard.takeAction(nowPlayer, command);

                        System.out.printf("[Blue] %s\n", command);

                        writerRed.write(command);
                        writerRed.newLine();
                        writerRed.flush();

                        nowPlayer = -1;
                    } else {
                        isInputted = false;
                        while (!isInputted) {
                            Thread.sleep(100);
                        }

                        chessboard.takeAction(nowPlayer, command);

                        System.out.printf("[Red] %s\n", command);

                        writerBlue.write(command);
                        writerBlue.newLine();
                        writerBlue.flush();

                        nowPlayer = 1;
                    }
                }
                chessboard.save();
            } catch (Exception e) {
                room.removeAll();
                interrupt();
            }
        }
    }
}
