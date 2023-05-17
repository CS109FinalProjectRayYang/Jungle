package network;

import structures.*;
import structures.chesses.Chess;
import structures.players.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Client {
    static int password = 0;
    static GUI myGUI;
    static int nowPlayer = 1;
    static Player player;
    static int colorMode = 1;
    // gameMode = 1 : 先手
    // gameMode = -1 : 后手
    static int counterPlayerMode = 0;
    // counterPlayerMode = 1 : 入门
    // counterPlayerMode = 2 : 简单
    // counterPlayerMode = 3 : 普通
    static Game game = new Game();
    static BufferedReader reader;
    static BufferedWriter writer;
    static History playbackHistory = new History();
    static Observer observer;
    static int observerStep = 1;
    static Chessboard_NEW observerChessboard;
    static String username;
    static String[] listData;

    public static void setNowPlayer(int nowPlayer, int value) {
        if (value == password) {
            Client.nowPlayer = nowPlayer;
        }
    }

    public static void setPlayer(Player player, int value) {
        if (value == password) {
            Client.player = player;
        }
    }

    public static void winPaint(int player, int value) {
        if (value == password) {
            myGUI.winPaint(player);
        }
    }

    public static void updateGamePaint(int value) {
        if (value == password) {
            myGUI.updateGamePaint();
        }
    }

    public static void setCountTime(int time, int password) {
        if (password == Client.password) {
            myGUI.setCountTime(time);
        }
    }

    public static void informServerGameEnd(String command) {
        try {
            writer.write(command + " " + username);
            writer.newLine();
            writer.flush();
        } catch (Exception ignore) {

        }
    }
    public static void main(String[] args) {

        try {
            String lookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
            UIManager.setLookAndFeel(lookAndFeel);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        TPlayMusic musicPlayer = new TPlayMusic();
        musicPlayer.start();
        myGUI = new GUI();
        myGUI.run();

    }

    private static class TPlayMusic extends Thread {
        @Override
        public void run() {
            PlaySound.run();
        }
    }
    private static class GUI {
        LoginGUI loginGUI;
        //当前页面
        // 0 : 无页面
        // 1 : 主页面
        // 2 : 本地游戏页面
        // 3 : 联机游戏页面
        // 4 : 游戏进行中页面
        // 5 : 选择AI难度
        // 6 : 选择先后手
        int page = 0;
        //是否有
        boolean clicked = false;
        int[] clickedPos = new int[2];
        ArrayList<int[]> legalMove;
        //窗体
        JFrame mainFrame;
        //面板
        JPanel mainFramePanel = new JPanel();
        JPanel initButtonPanel = new JPanel();
        JPanel localGameButtonPanel = new JPanel();
        JPanel gameButtonPanel = new JPanel();
        JPanel difficultyPanel = new JPanel();
        JPanel chooseColorPanel = new JPanel();
        JPanel messagePanel = new JPanel();
        JPanel messageButtonPanel = new JPanel();
        JPanel playbackButtonPanel = new JPanel();
        JPanel onlineButtonPanel = new JPanel();
        JPanel onlinePage = new JPanel();

        //按钮
        JButton localGameButton = new JButton("本地游戏");
        JButton onlineGameButton = new JButton("联机对战");
        JButton exitGameButton = new JButton("退出游戏");
        JButton againstPlayerButton = new JButton("玩家VS玩家");
        JButton againstComputerButton = new JButton("玩家VS电脑");
        JButton backInitButton = new JButton("返回主界面");
        JButton themeButton = new JButton("主题");
        JButton regretButton = new JButton("悔棋");
        JButton resetButton = new JButton("重置");
        JButton saveButton = new JButton("保存");
        JButton loadButton = new JButton("加载");
        JButton backButton = new JButton("返回");
        JButton easyButton = new JButton("入门");
        JButton normalButton = new JButton("简单");
        JButton difficultButton = new JButton("普通");
        JButton chooseBlueButton = new JButton("先手");
        JButton chooseRedButton = new JButton("后手");
        JButton sendMessageButton = new JButton("发送");
        JButton clearMessageButton = new JButton("清空");
        JButton nextStepButton = new JButton("前进");
        JButton lastStepButton = new JButton("后退");
        JButton jumpStepButton = new JButton("跳转");
        JButton playbackButton = new JButton("历史记录");
        JButton personalHomepage = new JButton("个人页面");
        JButton findGameButton = new JButton("联机对战");
        JButton createRoom = new JButton("创建房间");
        JButton enterRoom = new JButton("加入房间");
        //文本框
        JTextField jumpStepBox = new JTextField(10);
        JTextArea messageBox = new JTextArea(35, 40);
        JTextField inputBox = new JTextField(20);
        JTextField roomNameBox = new JTextField(15);
        JList<String> roomList = new JList<>();
        JScrollPane messageBoxScroller = new JScrollPane(messageBox);
        JScrollPane roomListScroller = new JScrollPane(roomList);
        //图片
        ImageIcon mainFrameImgOrig = new ImageIcon("data/img/Jungle.jpg");
        ImageIcon mainFrameImg = new ImageIcon("data/img/Jungle.jpg");
        // source: https://www.zcool.com.cn/work/ZMzg3NjU3Njg=.html
        ImageIcon mapImgOrig = new ImageIcon("data/img/Map3.png");
        ImageIcon mapImg = new ImageIcon("data/img/Map3.png");
        int mapImgIndex = 3;
        //标签
        JLabel mainFrameImgLabel = new JLabel(mainFrameImg);
        JLabel mapImgLabel = new JLabel(mapImg);
        JLabel chooseDifficultyLabel = new JLabel("难度：");
        JLabel chooseColorLabel = new JLabel("行动：");
        JLabel timeLabel = new JLabel();
        JLabel usernameLabel = new JLabel();
        JLabel gameNumberLabel = new JLabel();
        JLabel winningRateLabel = new JLabel();
        JLabel[][] imgLabels = new JLabel[2][9];
        JLabel[][] imgLabelsGrey = new JLabel[2][9];
        //字体
        Font buttonFont = new Font("楷体", Font.BOLD, 20);
        Font messageFont = new Font("楷体", Font.BOLD, 14);
        //进程
        GameStart gameStart = new GameStart();




        public void run() {
            // 设置图片大小
            mainFrameImg.setImage(mainFrameImgOrig.getImage().getScaledInstance(700, 560, Image.SCALE_DEFAULT));
            mapImg.setImage(mapImgOrig.getImage().getScaledInstance(1000, 700, Image.SCALE_DEFAULT));

            // 加载主窗体
            loadMainFrame();

            // 加载初始界面的按钮面板
            loadInitButtonPanel();

            // 加载本地游戏选择界面的按钮面板
            loadLocalGameButtonPanel();

            loadGameButtonPanel();

            loadDifficultyPanel();

            loadChooseColorPanel();

            loadMessagePanel();

            loadPlaybackButtonPanel();

            loadImg();

            // 在上述准备工作均完成后，绘制主窗体
            initPaint();
        }







        private void initPaint() {
            page = 1;

            // 清空主窗体
            mainFramePanel.removeAll();

            // 在主窗体中加入主窗体图片以及初始界面的按钮面板
            mainFramePanel.add(mainFrameImgLabel);
            mainFramePanel.add(initButtonPanel);

            // 设置mainFrame大小以包装上述组件并显示
            mainFrame.setLocation(300, 150);
            mainFrame.pack();
            mainFrame.setVisible(true);
        }


        private void localGamePaint() {
            page = 2;

            // 清空主窗体
            mainFramePanel.removeAll();

            // 在主窗体中加入主窗体图片以及本地游戏选择按钮面板
            mainFramePanel.add(mainFrameImgLabel);
            mainFramePanel.add(localGameButtonPanel);

            // 设置mainFrame大小以包装上述组件并显示
            mainFrame.setLocation(300, 150);
            mainFrame.pack();
        }


        private void onlineGamePaint() {
            page = 3;
        }


        private void playbackPaint() {
            mainFramePanel.removeAll();

            mainFramePanel.add(mapImgLabel);
            mainFramePanel.add(playbackButtonPanel);
            mainFramePanel.add(messagePanel);

            mainFrame.setLocation(50, 30);
            mainFrame.pack();
        }

        private void gamePaint() {
            page = 4;

            mainFramePanel.removeAll();

            mainFramePanel.add(mapImgLabel);
            mainFramePanel.add(gameButtonPanel);
            mainFramePanel.add(messagePanel);

            mainFrame.setLocation(50, 30);
            mainFrame.pack();

            gameStart = new GameStart();
            gameStart.start();
        }
        private void updateGamePaint() {
            mainFramePanel.removeAll();

            updateMessageBox();

            drawAnimals();

//            drawTime();

            if (clicked) {
                drawChoose();
            }

            mainFramePanel.add(mapImgLabel);
            mainFramePanel.add(gameButtonPanel);
            mainFramePanel.add(messagePanel);

            mainFrame.repaint();

//            Repaint painter = new Repaint();
//            painter.start();


            try {
                Thread.sleep(100);
            } catch (Exception ignore) {

            }
        }

        private void observerPaint() {
            mainFramePanel.removeAll();

            mainFramePanel.add(mapImgLabel);
            mainFramePanel.add(playbackButtonPanel);

            mainFrame.pack();
        }

        private void updateObserverPaint() {

            mainFramePanel.removeAll();

            drawAnimalsByObserver();

            mainFramePanel.add(mapImgLabel);
            mainFramePanel.add(playbackButtonPanel);

            mainFrame.repaint();

            try {
                Thread.sleep(100);
            } catch (Exception ignore) {

            }
        }


        private void drawAnimalsByObserver() {
            mainFramePanel.setLayout(null);
            Chessboard_NEW chessboard = observer.getChessboard();
            for (int i = 1; i <= Chessboard_NEW.getSizeY(); i++) {
                for (int j = 1; j <= Chessboard_NEW.getSizeX(); j++) {
                    Chess chess = chessboard.getChess(j, i);
                    if (chess != null) {
                        int posX = 50 + 80 * j;
                        int posY = 45 + 80 * i;

                        int team = chess.getTeam();
                        if (team == -1) team = 0;
                        JLabel imgLabel = imgLabels[team][chess.getID()];

                        imgLabel.setBounds(posX, posY, 116, 80);

                        mainFramePanel.add(imgLabel);
                    }
                }
            }
            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));
        }

        private void drawAnimals() {
            mainFramePanel.setLayout(null);
            Chessboard_NEW chessboard = game.getChessboard();
            for (int i = 1; i <= Chessboard_NEW.getSizeY(); i++) {
                for (int j = 1; j <= Chessboard_NEW.getSizeX(); j++) {
                    Chess chess = chessboard.getChess(j, i);
                    if (chess != null) {
                        int posX = 50 + 80 * j;
                        int posY = 45 + 80 * i;
//                        ImageIcon chessImg;
//                        if (nowPlayer == chess.getTeam()) {
//                            chessImg = chess.getImg();
//                        } else {
//                            chessImg = chess.getGreyImg();
//                        }

//                        chessImg.setImage(chessImg.getImage().getScaledInstance(116, 80, Image.SCALE_DEFAULT));

//                        JLabel imgLabel = new JLabel(chessImg);
                        int team = chess.getTeam();
                        if (team == -1) team = 0;
                        JLabel imgLabel;
                        if (nowPlayer == chess.getTeam()) {
                            imgLabel = imgLabels[team][chess.getID()];
                        } else {
                            imgLabel = imgLabelsGrey[team][chess.getID()];
                        }
                        imgLabel.setBounds(posX, posY, 116, 80);

                        mainFramePanel.add(imgLabel);
                    }
                }
            }
            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));
        }

        private void loadImg() {
            Chessboard_NEW chessboard = new Chessboard_NEW();
            chessboard.initBoard();
            for (int i = 1; i <= Chessboard_NEW.getSizeX(); i++) {
                for (int j = 1; j <= Chessboard_NEW.getSizeY(); j++) {
                    Chess chess = chessboard.getChess(i, j);
                    if (chess != null) {
                        int id = chess.getID();
                        int team = chess.getTeam();
                        if (team == -1) team = 0;
//                        System.out.println("team: %d, id: %d".formatted(team, id));
                        ImageIcon chessImg = chess.getImg();
                        ImageIcon chessImgGrey = chess.getGreyImg();
                        chessImg.setImage(chessImg.getImage().getScaledInstance(116, 80, Image.SCALE_DEFAULT));
                        chessImgGrey.setImage(chessImgGrey.getImage().getScaledInstance(116, 80, Image.SCALE_DEFAULT));
                        imgLabels[team][id] = new JLabel(chessImg);
                        imgLabelsGrey[team][id] = new JLabel(chessImgGrey);
                    }
                }
            }
        }

        private void drawChoose() {
            String path;
            mainFramePanel.setLayout(null);
            Chess chess = game.getChessboard().getChess(clickedPos);
            ImageIcon imgIconTemp;
            for (int[] temp : chess.getLegalMove(clickedPos)) {
                if (chess.getTeam() == 1) {
                    path = "data/img/ChosenBlue.png";
                } else {
                    path = "data/img/ChosenRed.png";
                }

                imgIconTemp = new ImageIcon(path);
                imgIconTemp.setImage(imgIconTemp.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));

                JLabel chosenImgLabel = new JLabel(imgIconTemp);
                chosenImgLabel.setBounds(63 + 80 * temp[0], 41 + 80 * temp[1], 80, 80);

                mainFramePanel.add(chosenImgLabel);
            }

            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));
        }

        private void drawTime() {
            int posX = 460 + 440 * nowPlayer;
            int posY = 10;

            mainFramePanel.setLayout(null);

            timeLabel.setBounds(posX, posY, 80, 80);

            mainFramePanel.add(timeLabel);

            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));
        }

        private void updateMessageBox() {
            messageBox.setText("");
            String[] lines = game.getMessages();
            for (String line : lines) {
                messageBox.append(line + "\n");
            }
        }

        private void winPaint(int player) {
            String winner;
            if (player != 0) {
                if (player == 1) {
                    winner = "蓝方";
                } else {
                    winner = "红方";
                }
                JOptionPane.showMessageDialog(mainFrame, winner + "获胜！");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "双方平局！");
            }
        }

        private void chooseDifficultyPaint() {
            page = 5;

            mainFramePanel.removeAll();

            mainFramePanel.add(mainFrameImgLabel);
            mainFramePanel.add(difficultyPanel);

            mainFrame.repaint();

            mainFrame.setLocation(300, 150);
            mainFrame.pack();
        }

        private void chooseColorPaint() {
            page = 6;

            mainFramePanel.removeAll();

            mainFramePanel.add(mainFrameImgLabel);
            mainFramePanel.add(chooseColorPanel);

            mainFrame.repaint();

            mainFrame.setLocation(300, 150);
            mainFrame.pack();
        }

        public void setCountTime(int time) {
            timeLabel.setText("%d".formatted(time));
            updateGamePaint();
        }










        private void loadMainFrame() {
            // 设置主窗体面板的布局
            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));
            // 设置主窗体基本属性，并将主窗体面板加入到主窗体上
            mainFrame = new JFrame();
//            mainFrame.addComponentListener(new MainFrameAdapter());
            mainFrame.setContentPane(mainFramePanel);
            mainFrame.setLocation(300, 150);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(700, 500);

            mainFrame.setResizable(false);

            mainFrame.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //在游戏中才生效
                    if (page == 4) {
                        int[] clickPos = getClickPos(e);
                        if (!Chess.isOutOfBound(clickPos)) {
//                            System.out.printf("(%d, %d) Clicked.\n", clickPos[0], clickPos[1]);
                            if (!game.getInputted() && player.getIdentity() == 1) {
                                click(clickPos);
                            }
                        }
                    }
                }
            });
        }


        private void click (int[] clickPos){
            if (!clicked) {
                // 如果没有棋子被选择

                // 判断该棋子能否在该回合行动
                Chess chess = game.getChessboard().getChess(clickPos);
                if (chess != null && chess.getTeam() == nowPlayer) {
                    clickedPos = clickPos;
                    legalMove = chess.getLegalMove(clickPos);
                    clicked = true;
                    for (int[] move : legalMove) {
//                        System.out.printf("(%d, %d)\n", move[0], move[1]);
                    }
                } else {
//                    System.out.println("Illegal Click");
                }
            } else {
                // 如果有棋子被选择

                // 判断该行动是否合法
                boolean isLegalMove = false;
                for (int[] move : legalMove) {
                    if (move[0] == clickPos[0] && move[1] == clickPos[1]) {
                        isLegalMove = true;
                        break;
                    }
                }
                if (isLegalMove) {
                    game.input(clickedPos, clickPos, "%s: (%d, %d) -> (%d, %d)".formatted(game.getChessboard().getChess(clickedPos).getChessName(), clickedPos[0], clickedPos[1], clickPos[0], clickPos[1]));
                    if (game.isNetworkGame()) {
                        try {
                            writer.write("Action %d %d %d %d".formatted(clickedPos[0], clickedPos[1], clickPos[0], clickPos[1]));
                            writer.newLine();
                            writer.flush();
                        } catch (Exception ignore) {
                        }
                    }
                } else {
//                    System.out.println("Illegal Click");
                }

                // 复位
                clicked = false;
            }
            updateGamePaint();
        }


        private int[] getClickPos(MouseEvent e) {
            int[] ret = new int[2];
            ret[0] = (e.getX() - 70) / 80;
            ret[1] = (e.getY() - 70) / 80;
            return ret;
        }


        private void loadInitButtonPanel() {
            // 设置 本地游戏、在线游戏、退出游戏 这三个按钮的基本属性，并加入监听
            localGameButton.setFont(buttonFont);
            onlineGameButton.setFont(buttonFont);
            playbackButton.setFont(buttonFont);
            exitGameButton.setFont(buttonFont);

            localGameButton.addActionListener(new localGameButtonListener());
            onlineGameButton.addActionListener(new onlineGameButtonListener());
            exitGameButton.addActionListener(new exitGameButtonListener());
            playbackButton.addActionListener(e -> {
                observer = null;
                getHistory();
                if (observer != null) {
                    try {
                        observer.setChessboard(1);
                    } catch (Exception ignore) {

                    }
                    observerPaint();
                }
            });

            // 将三个按钮依次加入初始界面按钮面板中
            initButtonPanel.setLayout(new BoxLayout(initButtonPanel, BoxLayout.Y_AXIS));
            initButtonPanel.add(localGameButton);
            initButtonPanel.add(onlineGameButton);
            initButtonPanel.add(playbackButton);
            initButtonPanel.add(Box.createVerticalGlue()); // 退出游戏按钮上方做一定分隔
            initButtonPanel.add(exitGameButton);
        }


        private void loadLocalGameButtonPanel() {
            // 设置 玩家对战、电脑对战、返回主界面 这三个按钮的基本属性，并加入监听
            againstPlayerButton.setFont(buttonFont);
            againstComputerButton.setFont(buttonFont);
            backInitButton.setFont(buttonFont);

            againstPlayerButton.addActionListener(new againstPlayerButtonListener());
            againstComputerButton.addActionListener(new againstComputerButtonListener());
            backInitButton.addActionListener(new backInitButtonListener());

            // 将这三个按钮加入本地游戏按钮面板中
            localGameButtonPanel.setLayout(new BoxLayout(localGameButtonPanel, BoxLayout.Y_AXIS));
            localGameButtonPanel.add(againstPlayerButton);
            localGameButtonPanel.add(againstComputerButton);
            localGameButtonPanel.add(Box.createVerticalGlue());
            localGameButtonPanel.add(backInitButton);
        }


        private void loadGameButtonPanel() {
            timeLabel.setFont(new Font("楷体", Font.BOLD, 50));

            themeButton.setFont(buttonFont);
            regretButton.setFont(buttonFont);
            resetButton.setFont(buttonFont);
            saveButton.setFont(buttonFont);
            loadButton.setFont(buttonFont);
            backButton.setFont(buttonFont);

            themeButton.addActionListener(new themeButtonListener());
            regretButton.addActionListener(new regretButtonListener());
            resetButton.addActionListener(new resetButtonListener());
            saveButton.addActionListener(new saveButtonListener());
            loadButton.addActionListener(new loadButtonListener());
            backButton.addActionListener(new backButtonListener());

            gameButtonPanel.setLayout(new BoxLayout(gameButtonPanel, BoxLayout.Y_AXIS));
            gameButtonPanel.add(themeButton);
            gameButtonPanel.add(regretButton);
            gameButtonPanel.add(resetButton);
            gameButtonPanel.add(saveButton);
            gameButtonPanel.add(loadButton);
            gameButtonPanel.add(Box.createVerticalGlue());
            gameButtonPanel.add(backButton);
        }

        private void loadDifficultyPanel() {
            easyButton.setFont(buttonFont);
            normalButton.setFont(buttonFont);
            difficultButton.setFont(buttonFont);
            chooseDifficultyLabel.setFont(buttonFont);

            easyButton.addActionListener(e -> {
                counterPlayerMode = 1;
                chooseColorPaint();
            });
            normalButton.addActionListener(e -> {
                counterPlayerMode = 2;
                chooseColorPaint();
            });
            difficultButton.addActionListener(e -> {
                counterPlayerMode = 3;
                chooseColorPaint();
            });

            difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS));
            difficultyPanel.add(chooseDifficultyLabel);
            difficultyPanel.add(easyButton);
            difficultyPanel.add(normalButton);
            difficultyPanel.add(difficultButton);
            difficultyPanel.add(Box.createVerticalGlue());
        }

        private void loadChooseColorPanel() {
            chooseBlueButton.setFont(buttonFont);
            chooseRedButton.setFont(buttonFont);
            chooseColorLabel.setFont(buttonFont);

            chooseBlueButton.addActionListener(e -> {
                colorMode = 1;
                loadGame();
            });
            chooseRedButton.addActionListener(e -> {
                colorMode = -1;
                loadGame();
            });

            chooseColorPanel.setLayout(new BoxLayout(chooseColorPanel, BoxLayout.Y_AXIS));
            chooseColorPanel.add(chooseColorLabel);
            chooseColorPanel.add(chooseBlueButton);
            chooseColorPanel.add(chooseRedButton);
            chooseColorPanel.add(Box.createVerticalGlue());
        }

        private void loadGame() {
            game.setPassword(-1);
            game = new Game();
            Player counterPlayer = new HumanPlayer();
            if (counterPlayerMode == 1) {
                counterPlayer = new CP_Monkey();
            } else if (counterPlayerMode == 2) {
                counterPlayer = new CP_ShortSighted();
            } else if (counterPlayerMode == 3) {
                counterPlayer = new CP_ForeSighted();
            }
            if (colorMode == 1) {
                game.setPlayer(new HumanPlayer(), counterPlayer);
            } else {
                game.setPlayer(counterPlayer, new HumanPlayer());
            }
            gamePaint();
        }

        private void loadMessagePanel() {
            messageBoxScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            messageBoxScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

            messageBox.setFont(messageFont);
            inputBox.setFont(messageFont);

            sendMessageButton.setFont(buttonFont);
            clearMessageButton.setFont(buttonFont);

            sendMessageButton.addActionListener(e -> {
                game.messageInput(inputBox.getText());
                if (game.isNetworkGame()) {
                    try {
                        writer.write("Message %d %s".formatted(game.getTeam(), inputBox.getText()));
                        writer.newLine();
                        writer.flush();
                    } catch (Exception ignore) {

                    }
                }
                inputBox.setText("");
                updateMessageBox();
            });

            messageButtonPanel.setLayout(new BoxLayout(messageButtonPanel, BoxLayout.X_AXIS));
            messageButtonPanel.add(Box.createHorizontalGlue());
            messageButtonPanel.add(sendMessageButton);

            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            messagePanel.add(messageBoxScroller);
            messagePanel.add(inputBox);
            messagePanel.add(messageButtonPanel);
        }

        private void loadPlaybackButtonPanel() {
            nextStepButton.setFont(buttonFont);
            lastStepButton.setFont(buttonFont);
            jumpStepButton.setFont(buttonFont);
            jumpStepBox.setFont(buttonFont);

            nextStepButton.addActionListener(e -> {
                if (observerStep < observer.getSize()) {
                    observerStep++;
                    jumpStepBox.setText("%d".formatted(observerStep));
                    try {
                        observer.setChessboard(observerStep);
                    } catch (Exception ignore) {
                    }
                    observerChessboard = observer.getChessboard();
                    updateObserverPaint();
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "超过最大页数");
                }
            });
            lastStepButton.addActionListener(e -> {
                if (observerStep > 0) {
                    observerStep--;
                    jumpStepBox.setText("%d".formatted(observerStep));
                    try {
                        observer.setChessboard(observerStep);
                    } catch (Exception ignore) {

                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "超过最小页数");
                }
                observerChessboard = observer.getChessboard();
                updateObserverPaint();
            });
            jumpStepButton.addActionListener(e -> {
                int jumpStep = Integer.parseInt(jumpStepBox.getText());
                if (jumpStep < 1) {
                    jumpStep = 1;
                    JOptionPane.showMessageDialog(mainFrame, "超过最小页数");
                }
                if (jumpStep > observer.getSize()) {
                    jumpStep = observer.getSize();
                    JOptionPane.showMessageDialog(mainFrame, "超过最大页数");
                }
                jumpStepBox.setText("%d".formatted(jumpStep));
                observerStep = jumpStep;
                try {
                    observer.setChessboard(observerStep);
                } catch (Exception ignore) {

                }
                observerChessboard = observer.getChessboard();
                updateObserverPaint();
            });

            playbackButtonPanel.setLayout(new BoxLayout(playbackButtonPanel, BoxLayout.Y_AXIS));
            playbackButtonPanel.add(nextStepButton);
            playbackButtonPanel.add(lastStepButton);
            playbackButtonPanel.add(jumpStepBox);
            playbackButtonPanel.add(jumpStepButton);
            playbackButtonPanel.add(backInitButton);
        }











        private class localGameButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                localGamePaint();
            }
        }
        private class onlineGameButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                onlineGame();
            }
        }
        private class exitGameButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }
        private class againstPlayerButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                game = new Game();
                game.setPlayer(new HumanPlayer(), new HumanPlayer());
                gamePaint();
            }
        }
        private class againstComputerButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDifficultyPaint();
            }
        }
        private class backInitButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                initPaint();
            }
        }
        private class themeButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapImgIndex = mapImgIndex % 3 + 1;
                String path = "data/img/Map%d.png".formatted(mapImgIndex);
                mapImgOrig = new ImageIcon(path);
                mapImg.setImage(mapImgOrig.getImage().getScaledInstance(1000, 700, Image.SCALE_DEFAULT));

                updateGamePaint();
            }
        }
        private class regretButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getPlayer().getIdentity() == 1 && game.getHistorySize() > 1 && !game.isNetworkGame() && !game.isGameEnd()) {
                    game.buildFromHistory(2);
                    updateGamePaint();
                    game.messageInput("Regret", nowPlayer);
                    System.out.println("Regretted successfully!");
                    JOptionPane.showMessageDialog(mainFrame, "悔棋成功");
                    updateGamePaint();
                } else {
                    if (game.getPlayer().getIdentity() != 1) {
                        JOptionPane.showMessageDialog(mainFrame, "悔棋失败：不是你的回合");
                    } else if (game.getHistorySize() < 2){
                        JOptionPane.showMessageDialog(mainFrame, "悔棋失败：无可悔棋步骤");
                    } else if (game.isNetworkGame()) {
                        JOptionPane.showMessageDialog(mainFrame, "悔棋失败：网络对战禁用悔棋");
                    } else if (game.isGameEnd()) {
                        JOptionPane.showMessageDialog(mainFrame, "悔棋失败：游戏已结束");
                    }
                }
            }
        }
        private class resetButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.isNetworkGame()) {
                    gameStart.interrupt();
                    game.setPassword(-1);
                    game = new Game(game.getPlayerBlue(), game.getPlayerRed());
                    gameStart = new GameStart();
                    gameStart.start();
                    System.out.println("Resettled successfully!");
                    JOptionPane.showMessageDialog(mainFrame, "重置成功");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "重置失败：联机对战禁用重置");
                }
            }
        }
        private class saveButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                long nowTime = System.currentTimeMillis();
                String historyName = JOptionPane.showInputDialog("请输入记录名称","history"+nowTime);
                if (historyName != null) {
                    saveHistory(historyName);
                }
            }
        }
        private class loadButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                loadHistory();
            }
        }
        private class backButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                int userOption = JOptionPane.showConfirmDialog(mainFrame, "确认退出游戏吗？");
                if (userOption == JOptionPane.OK_OPTION) {
                    gameStart.interrupt();
                    game.setPassword(-1);
                    game = new Game();
                    initPaint();
                }
            }
        }










        private void saveHistory(String historyName) {
            File fHistory = new File("data/save/%s.txt".formatted(historyName));
            if (fHistory.exists()) {
                JOptionPane.showMessageDialog(mainFrame, "文件名重复");
            } else {
                try {
                    BufferedWriter fHistoryWriter = new BufferedWriter(new FileWriter(fHistory));
                    fHistoryWriter.write("%d".formatted(game.getHistorySize()));
                    fHistoryWriter.newLine();
                    for (int i = 1; i <= game.getHistorySize(); i++) {
                        int[][] nowStep = game.getHistory(i);
                        fHistoryWriter.write("%d %d %d %d".formatted(nowStep[0][0], nowStep[0][1], nowStep[1][0], nowStep[1][1]));
                        fHistoryWriter.newLine();
                    }
                    fHistoryWriter.flush();
                    fHistoryWriter.close();
                    JOptionPane.showMessageDialog(mainFrame, "保存成功");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(mainFrame, "历史文件操作错误");
                    throw new RuntimeException(e);
                }

            }
        }

        private void getHistory() {
            JFileChooser historyChooser = new JFileChooser("data/save/");
            historyChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int flag = historyChooser.showOpenDialog(mainFrame);
            if (flag == JFileChooser.APPROVE_OPTION) {
                File fHistory = historyChooser.getSelectedFile();
                try {
                    BufferedReader fHistoryReader = new BufferedReader(new FileReader(fHistory));
                    int size = Integer.parseInt(fHistoryReader.readLine());
                    isLegalHistory(fHistoryReader, size);
                    playbackHistory = new History(fHistory);
                    observer = new Observer(playbackHistory);
                } catch (Exception ignore) {
                    JOptionPane.showMessageDialog(mainFrame, "文件已损坏");
                }
            }

        }

        private void isLegalHistory(BufferedReader fHistoryReader, int size) throws Exception {
            Chessboard_NEW testBoard = new Chessboard_NEW();
            testBoard.initBoard();
            for (int i = 1; i <= size; i++) {
                String line = fHistoryReader.readLine();
                String[] elements = line.split(" ");

                int[] testPos = new int[]{Integer.parseInt(elements[0]), Integer.parseInt(elements[1])};
                int[] testNextPos = new int[]{Integer.parseInt(elements[2]), Integer.parseInt(elements[3])};
                Chess testChess = testBoard.getChess(testPos);
                if (testChess == null) {
                    // 当前位置无棋子
                    throw new Exception();
                } else {
                    ArrayList<int[]> legalMoves = testChess.getLegalMove(testPos);
                    boolean isLegalMove = false;
                    for (int[] legalMove : legalMoves) {
                        if (legalMove[0] == testNextPos[0] && legalMove[1] == testNextPos[1]) {
                            isLegalMove = true;
                        }
                    }
                    if (!isLegalMove) {
                        // 棋子移动不合法
                        throw new Exception();
                    }
                }
                testBoard.moveChess(testPos, testNextPos);
            }
        }

        private void loadHistory() {
            JFileChooser historyChooser = new JFileChooser("data/save/");
            historyChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int flag = historyChooser.showOpenDialog(mainFrame);
            if (flag == JFileChooser.APPROVE_OPTION) {
                File fHistory = historyChooser.getSelectedFile();
                try {
                    BufferedReader fHistoryReader = new BufferedReader(new FileReader(fHistory));
                    int size = Integer.parseInt(fHistoryReader.readLine());
                    int[][][] history = new int[3000][2][2];
                    Chessboard_NEW testBoard = new Chessboard_NEW();
                    testBoard.initBoard();
                    for (int i = 1; i <= size; i++) {
                        String line = fHistoryReader.readLine();
                        String[] elements = line.split(" ");

                        int[] testPos = new int[]{Integer.parseInt(elements[0]), Integer.parseInt(elements[1])};
                        int[] testNextPos = new int[]{Integer.parseInt(elements[2]), Integer.parseInt(elements[3])};
                        Chess testChess = testBoard.getChess(testPos);
                        if (testChess == null) {
                            // 当前位置无棋子
                            throw new Exception();
                        } else {
                            ArrayList<int[]> legalMoves = testChess.getLegalMove(testPos);
                            boolean isLegalMove = false;
                            for (int[] legalMove : legalMoves) {
                                if (legalMove[0] == testNextPos[0] && legalMove[1] == testNextPos[1]) {
                                    isLegalMove = true;
                                }
                            }
                            if (!isLegalMove) {
                                // 棋子移动不合法
                                throw new Exception();
                            }
                        }
                        history[i][0] = testPos;
                        history[i][1] = testNextPos;
                        testBoard.moveChess(testPos, testNextPos);
                    }
                    History h = new History(history, size);
                    game.setPassword(-1);
                    game = new Game(game.getPlayerBlue(), game.getPlayerRed());
                    game.buildFromHistory(h);
                    gamePaint();
                } catch (Exception ignore) {
                    JOptionPane.showMessageDialog(mainFrame, "文件已损坏");
                }

            }
        }










        static class GameStart extends Thread {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                game.start();
            }
        }


        private void onlineGame() {
            try {
                String ip = "10.27.40.151";
//                ip = "127.0.0.1";
                ip = "121.5.129.39";

                Socket socket = new Socket(ip, 8080);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                loginGUI = new LoginGUI();
                loginGUI.run();
                closeWaitingPaint();
            } catch (Exception ignore) {
                System.out.println("Server Error");
                JOptionPane.showMessageDialog(mainFrame, "无法连接至服务器");
                closeWaitingPaint();
            }
        }

        class LoginGUI {
            JFrame loginFrame = new JFrame();
            JTextField usernameField = new JTextField(15);
            JTextField passwordField = new JPasswordField(15);
            JLabel usernameLabel = new JLabel(" 用户：");
            JLabel passwordLabel = new JLabel(" 密码：");
            JButton loginButton = new JButton("登录");
            JButton registerButton = new JButton("注册");
            JPanel textPanel = new JPanel();
            JPanel labelPanel = new JPanel();
            JPanel upperPanel = new JPanel();
            JPanel lowerPanel = new JPanel();
            JPanel mainPanel = new JPanel();
            public void run() {
                loginButton.addActionListener(e -> {
                    try {
                        writer.write("login %s %s".formatted(usernameField.getText(), passwordField.getText()));
                        writer.newLine();
                        writer.flush();
                        String message = reader.readLine();

                        switch (message) {
                            case "LoginSuccessfully" -> {
                                JOptionPane.showMessageDialog(loginFrame, "登录成功");
                                loginFrame.setVisible(false);
                                username = usernameField.getText();
                                login();
                            }
                            case "PasswordError" -> JOptionPane.showMessageDialog(loginFrame, "登陆失败：密码错误");
                            case "UsernameNotFound" -> JOptionPane.showMessageDialog(loginFrame, "登录失败：用户名不存在");
                            default -> JOptionPane.showMessageDialog(loginFrame, "登陆失败：未知错误");
                        }
                    } catch (IOException ignore) {
                        System.out.println("Server Error");
                        JOptionPane.showMessageDialog(loginFrame, "无法连接至服务器");
                    }
                });

                registerButton.addActionListener(e -> {
                    try {
                        writer.write("register %s %s".formatted(usernameField.getText(), passwordField.getText()));
                        writer.newLine();
                        writer.flush();
                        JOptionPane.showMessageDialog(loginFrame, "注册成功");
                    } catch (IOException ignore) {
                        System.out.println("Server Error");
                        JOptionPane.showMessageDialog(loginFrame, "无法连接至服务器");
                    }
                });

                usernameLabel.setFont(new Font("楷体", Font.BOLD, 17));
                passwordLabel.setFont(new Font("楷体", Font.BOLD, 17));

                labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
                labelPanel.add(usernameLabel);
                labelPanel.add(passwordLabel);

                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.add(usernameField);
                textPanel.add(passwordField);

                upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
                upperPanel.add(labelPanel);
                upperPanel.add(textPanel);

                lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
                lowerPanel.add(loginButton);
                lowerPanel.add(registerButton);

                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                mainPanel.add(upperPanel);
                mainPanel.add(lowerPanel);

                loginFrame.getContentPane().add(mainPanel);
                loginFrame.setLocation(500, 300);
                loginFrame.pack();
                loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                loginFrame.setVisible(true);
            }

        }
        private void login() {
            try {
                loadLogin();
            } catch (Exception ignore) {
                initPaint();
                JOptionPane.showMessageDialog(mainFrame, "无法连接至服务器");
            }

        }
        private void loadLogin() throws Exception{

            usernameLabel.setFont(buttonFont);
            gameNumberLabel.setFont(buttonFont);
            winningRateLabel.setFont(buttonFont);

            writer.write("getUserData %s".formatted(username));
            writer.newLine();
            writer.flush();

            String username = reader.readLine();
            String password = reader.readLine();
            int gameNum = Integer.parseInt(reader.readLine());
            int winNum = Integer.parseInt(reader.readLine());

            usernameLabel.setText("用户名：%s".formatted(username));
            gameNumberLabel.setText("对局数：%d".formatted(gameNum));
            if (gameNum != 0) {
                winningRateLabel.setText("胜率：%.2f%%".formatted((double) winNum / gameNum * 100));
            } else {
                winningRateLabel.setText("胜率：暂无胜率");
            }

            personalHomepage.setFont(buttonFont);
            findGameButton.setFont(buttonFont);

//            personalHomepage.setContentAreaFilled(false);

//            findGameButton.setContentAreaFilled(false);


            onlineButtonPanel.removeAll();
            onlineButtonPanel.setLayout(new BoxLayout(onlineButtonPanel, BoxLayout.Y_AXIS));
            onlineButtonPanel.add(personalHomepage);
            onlineButtonPanel.add(findGameButton);
            onlineButtonPanel.add(Box.createVerticalGlue());
            onlineButtonPanel.add(backInitButton);

            personalHomepage.addActionListener(e -> {
                personalHomepagePaint();
            });
            findGameButton.addActionListener(e -> {
                findGamePaint();
            });

            personalHomepagePaint();

        }
        private void personalHomepagePaint() {
            mainFramePanel.removeAll();

            onlinePage.removeAll();
            onlinePage.setSize(600, 500);
            onlinePage.setLayout(new BoxLayout(onlinePage, BoxLayout.Y_AXIS));

            onlinePage.add(usernameLabel);
            onlinePage.add(gameNumberLabel);
            onlinePage.add(winningRateLabel);
            onlinePage.add(Box.createVerticalGlue());

            mainFramePanel.add(onlineButtonPanel);
            mainFramePanel.add(onlinePage);

            mainFrame.setSize(700, 500);
            mainFrame.repaint();
        }
        private void findGamePaint() {
            mainFramePanel.removeAll();

            onlinePage.removeAll();
            onlinePage.setSize(600, 500);
            onlinePage.setLayout(null);

            listData = null;
            try {
                writer.write("searchRoom");
                writer.newLine();
                writer.flush();
                int roomNum = Integer.parseInt(reader.readLine());
                listData = new String[roomNum + 1];
                listData[0] = "%10s%20s%20s".formatted("Room ID", "Room Name", "Username");
                for (int i = 1; i <= roomNum; i++) {
                    listData[i] = reader.readLine();
                }
            } catch (Exception ignore) {

            }
            roomList.setListData(listData);

            roomListScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            roomListScroller.setBounds(10, 10, 400, 400);

            enterRoom.setFont(buttonFont);
            createRoom.setFont(buttonFont);

            enterRoom.setBounds(420, 10, 130, 40);
            createRoom.setBounds(420, 60, 130, 40);

            enterRoom.addActionListener(e -> {
                int i = roomList.getSelectedIndex();
                if (i > 0) {
                    int flag = JOptionPane.showConfirmDialog(mainFrame, "是否加入该房间？");
                    if (flag == JOptionPane.OK_OPTION) {
                        try {
                            writer.write("joinRoom %s %d".formatted(username, i));
                            writer.newLine();
                            writer.flush();
                            String command = reader.readLine();
                            if (command.equals("joinRoomSuccessfully")) {
                                game = new Game(new NetworkPlayer(), new HumanPlayer());
                                gamePaint();
                                TReceive tReceive = new TReceive();
                                tReceive.start();
                            } else {
                                throw new Exception();
                            }
                        } catch (Exception ignore) {
                            JOptionPane.showMessageDialog(mainFrame, "服务器连接错误1");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "请选择要加入的房间");
                }
            });
            createRoom.addActionListener(e -> {
                String roomName = JOptionPane.showInputDialog(mainFrame, "房间名:");
                try {
                    writer.write("newRoom %s %s".formatted(username, roomName));
                    writer.newLine();
                    writer.flush();
//                    waitingPaint("等待加入...");
                    findGamePaint();
                    String command = reader.readLine();
                    if (command.equals("gameBegin")) {
                        game = new Game(new HumanPlayer(), new NetworkPlayer());
                        gamePaint();
                        TReceive tReceive = new TReceive();
                        tReceive.start();
                    }
                } catch (Exception ignore) {
                    JOptionPane.showMessageDialog(mainFrame, "服务器连接错误2");
                    initPaint();
                }
            });

            onlinePage.add(roomListScroller);
            onlinePage.add(enterRoom);
            onlinePage.add(createRoom);

            mainFramePanel.add(onlineButtonPanel);
            mainFramePanel.add(onlinePage);

            mainFrame.setSize(700, 500);
            mainFrame.repaint();
        }
        JFrame waitingFrame = new JFrame();
        private void waitingPaint(String content) {
            JLabel waitingLabel = new JLabel();
            JPanel waitingPanel = new JPanel();
            waitingLabel.setFont(buttonFont);
            waitingLabel.setText(content);

            waitingPanel.add(waitingLabel);

            waitingFrame.setContentPane(waitingPanel);
            waitingFrame.setResizable(false);
            waitingFrame.setSize(300, 150);
            waitingFrame.setLocation(500, 300);
            waitingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            waitingFrame.setVisible(true);
        }
        private  void closeWaitingPaint() {
            waitingFrame.setVisible(false);
        }
        static class TReceive extends Thread {
            @Override
            public void run() {
                try {
                    while (true) {
                        String command = reader.readLine();
                        System.out.println("接收到消息："+command);
                        game.input(command);
                    }
                } catch (Exception ignore) {

                }

            }
        }
    }
}
