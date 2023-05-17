package network;

import structures.Room;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Server {
    static ArrayList<Room> rooms = new ArrayList<>();
    static int roomNum = 0;
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("server is running...");
        System.out.println("本机IP: " + InetAddress.getLocalHost().getHostAddress());

        while (true) {
            Socket socket = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            System.out.println(socket.getRemoteSocketAddress() + " connected.");
            Thread tConnect = new Connect(socket, reader, writer);
            tConnect.start();
        }
    }
    public static class Connect extends Thread {
        Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        Room room;
        public Connect(Socket socket, BufferedReader reader, BufferedWriter writer) {
            this.socket = socket;
            this.reader = reader;
            this.writer = writer;
        }
        public void run() {
            while (true) {
                try {
                    String command = reader.readLine();
                    String[] commands = command.split(" ");
                    switch (commands[0]) {
                        case "register":
                            register(commands[1], commands[2]);
                            break;
                        case "delete":
                            delete(commands[1], commands[2]);
                            break;
                        case "login":
                            login(commands[1], commands[2]);
                            break;
                        case "newRoom":
                            newRoom(commands[1], commands[2]);
                            break;
                        case "searchRoom":
                            searchRoom();
                            break;
                        case "joinRoom":
                            joinRoom(commands[1], commands[2]);
                            break;
                        case "exitRoom":
                            exitRoom(commands[1], commands[2]);
                            break;
                        case "getUserData":
                            getUserData(commands[1]);
                            break;
                        case "gameWin":
                            gameWin(commands[1]);
                            break;
                        case "gameLose":
                            gameLose(commands[1]);
                            break;
                        case "Action":
                            room.act(command);
                            break;
                        case "Message":
                            room.input(command);
                            break;
                    }
                } catch (IOException e) {
                    System.out.println(socket.getRemoteSocketAddress()+" disconnected.");
                    break;
                }
            }
        }
        private void getUserData(String username) {
            String path = "data/user/%s/%s.txt".formatted(username, username);
            ArrayList<String> lines = readFile(path);

            try {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
            } catch (Exception ignore) {

            }
        }
        private void register(String username, String password) throws IOException {
            // 用户名不合法在客户端本地判断
            if (hasUsername(username)) {
                sendMessage("UsernameUsed");
            } else {
                String path = "data/user/usernames.txt";
                appendFile(path, username);
                path = "data/user/%s/%s.txt".formatted(username, username);
                writeFile(path, username, password, "0", "0");
            }
        }
        private void delete(String username, String password) {
            // TODO: Complete the method delete.
        }
        private void login(String username, String password) throws IOException {
            if (hasUsername(username)) {
                String path = "data/user/%s/%s.txt".formatted(username, username);
                ArrayList<String> lines = readFile(path);
                if (password.equals(lines.get(1))) {
                    sendMessage("LoginSuccessfully");
                    System.out.println("登录成功");
                } else {
                    sendMessage("PasswordError");
                    System.out.println("密码错误");
                }
            } else {
                sendMessage("UsernameNotFound");
                System.out.println("用户未注册");
            }
        }
        private void newRoom(String username, String roomName) {
            boolean hasBeenCreated = false;
            System.out.println("请求创建房间");
            roomNum++;
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i) == null) {
                    room = new Room(i, roomName, username, socket);
                    room.setConnectBlue(this);
                    System.out.println("创建房间已赋值");
                    rooms.set(i, room);
                    hasBeenCreated = true;
                    break;
                }
            }
            if (!hasBeenCreated) {
                System.out.println("创建房间已赋值");
                room = new Room(rooms.size(), roomName, username, socket);
                room.setConnectBlue(this);
                rooms.add(room);
            }
        }
        private void searchRoom() throws IOException {
            int count = 0;
            for (Room room : rooms) {
                room.checkRoom();
                if (room.isWaiting()) {
                    count++;
                }
            }
            sendMessage(String.valueOf(count));
            for (Room room : rooms) {
                if (room.isWaiting()) {
                    sendMessage("%10d%20s%20s".formatted(room.getRoomID(), room.getRoomName(), room.getRoomMate()));
                }
            }
        }
        private void joinRoom(String username, String countNum) throws IOException {
            System.out.println("请求加入房间");
            int count = 0;
            for (Room room : rooms) {
                if (room != null && room.isWaiting()) {
                    count++;
                }
                if (count == Integer.parseInt(countNum)) {
                    this.room = room;
                    this.room.setConnectRed(this);
                    if (this.room != null) {
                        System.out.println("加入房间已赋值");
                    }
                    System.out.println("找到对应房间");
                    room.joinRoom(username, socket);
                    try {
                        System.out.println("申请加入");
                        room.inform();
                        sendMessage("joinRoomSuccessfully");
                        System.out.println("加入成功");
                        room.beginGame();
                    } catch (Exception ignore) {
                        sendMessage("joinRoomFail");
                    }
                }
            }
        }
        private void exitRoom(String username, String roomID) {
            // TODO: Complete the method exitRoom.
        }

        private void gameWin(String username) {
            addGameData(1, username);
        }

        private void gameLose(String username) {
            addGameData(0, username);
        }

        private void addGameData(int addWinCount, String username) {
            String path = "data/user/%s/%s.txt".formatted(username, username);
            ArrayList<String> lines = readFile(path);
            int countAllGames = Integer.parseInt(lines.get(2)) + 1;
            int countWinGames = Integer.parseInt(lines.get(3)) + addWinCount;
            lines.set(2, countAllGames + "");
            lines.set(3, countWinGames + "");
            writeFile(path, lines.get(0), lines.get(1), lines.get(2), lines.get(3));
        }


        private boolean hasUsername(String username) {
            boolean hasUsername = false;
            String path = "data/user/usernames.txt";
            ArrayList<String> lines = readFile(path);
            for (String line : lines) {
                if (line.equals(username)) {
                    hasUsername = true;
                    break;
                }
            }
            return hasUsername;
        }
        private ArrayList<String> readFile(String path) {
            ArrayList<String> lines = new ArrayList<>();
            try {
                String line;
                File file = new File(path);
                BufferedReader fReader = new BufferedReader(new FileReader(file));
                while ((line = fReader.readLine()) != null) {
                    lines.add(line);
                }
                fReader.close();
            } catch (IOException e) {
                System.out.printf("Path %s don't exist.\n", path);
            }
            return lines;
        }
        private void appendFile(String path, String line) {
            try {
                File file = new File(path);
                BufferedWriter fWriter = new BufferedWriter(new FileWriter(file, true));
                fWriter.write(line);
                fWriter.newLine();
                fWriter.flush();
                fWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void writeFile(String path, String ... lines) {
            File file = new File(path);
            if (!file.exists()) {
                String rootPath = null;
                for (int i = path.length() - 1; i >= 0; i--) {
                    if (path.charAt(i) == '/') {
                        rootPath = path.substring(0, i);
                        break;
                    }
                }
                if (rootPath != null) {
                    File rootFile = new File(rootPath);
                    rootFile.mkdir();
                }
            }
            try {
                BufferedWriter fWriter = new BufferedWriter(new FileWriter(file));
                for (int i = 0; i < lines.length; i++) {
                    fWriter.write(lines[i]);
                    fWriter.newLine();
                }
                fWriter.flush();
                fWriter.close();
            } catch (IOException ignore) {}
        }
        private void writeFile(String path, boolean mkdir, String ... lines) {
            if (mkdir) {
                String rootPath = null;
                for (int i = path.length() - 1; i >= 0; i--) {
                    if (path.charAt(i) == '/') {
                        rootPath = path.substring(0, i);
                        break;
                    }
                }
                if (rootPath != null) {
                    File rootFile = new File(rootPath);
                    rootFile.mkdir();
                }
            } else {
                writeFile(path, lines);
            }
        }
        public void sendMessage(String message) throws IOException {
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
    }
}
