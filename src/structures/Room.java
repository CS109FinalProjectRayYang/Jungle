package structures;

import network.Server;

import java.io.*;
import java.net.Socket;

public class Room {
    int roomID;
    int userNum = 0;
    String roomName;
    String userBlue = null;
    String userRed = null;
    Socket socketBlue, socketRed;
    Server.Connect connectBlue, connectRed;
    boolean isInputted = false;
    int nowWriter = -1;
    String command;

    public void setConnectBlue(Server.Connect connectBlue) {
        this.connectBlue = connectBlue;
    }

    public void setConnectRed(Server.Connect connectRed) {
        this.connectRed = connectRed;
    }

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
        connectBlue.sendMessage("gameBegin");
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
        System.out.println(this + "得到消息" + command);
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

                        connectRed.sendMessage(command);

                        nowPlayer = -1;
                    } else {
                        isInputted = false;
                        while (!isInputted) {
                            Thread.sleep(100);
                        }

                        chessboard.takeAction(nowPlayer, command);

                        System.out.printf("[Red] %s\n", command);

                        connectBlue.sendMessage(command);

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
