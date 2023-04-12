package network;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) throws IOException{

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
                    }
                } catch (IOException e) {
                    System.out.println(socket.getRemoteSocketAddress()+" disconnected.");
                }
            }
        }
        private void register(String username, String password) {
            // TODO: Complete the method register.
        }
        private void delete(String username, String password) {
            // TODO: Complete the method delete.
        }
        private void login(String username, String password) {
            // TODO: Complete the method login.
        }
        private void newRoom(String username, String roomName) {
            // TODO: Complete the method newRoom.
        }
        private void searchRoom() {
            // TODO: Complete the method searchRoom.
        }
        private void joinRoom(String username, String roomID) {
            // TODO: Complete the method joinRoom.
        }
        private void exitRoom(String username, String roomID) {
            // TODO: Complete the method exitRoom.
        }
    }
}
