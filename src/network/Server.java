package network;

import structures.Room;

import javax.print.DocFlavor;
import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

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
    private static class Connect extends Thread {
        Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
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
            roomNum++;
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i) == null) {
                    rooms.set(i, new Room(i, roomName, username, socket));
                    hasBeenCreated = true;
                    break;
                }
            }
            if (!hasBeenCreated) {
                rooms.add(new Room(rooms.size(), roomName, username, socket));
            }
        }
        private void searchRoom() throws IOException {
            sendMessage(String.valueOf(roomNum));
            for (Room room : rooms) {
                if (room.isWaiting()) {
                    sendMessage("%d %s %s".formatted(room.getRoomID(), room.getRoomName(), room.getRoomMate()));
                }
            }
        }
        private void joinRoom(String username, String roomID) throws IOException {
            String command = reader.readLine();
            String[] commands = command.split(" ");
            rooms.get(Integer.parseInt(commands[2])).joinRoom(commands[1], socket);
        }
        private void exitRoom(String username, String roomID) {
            // TODO: Complete the method exitRoom.
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
        private void sendMessage(String message) throws IOException {
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
    }
}
