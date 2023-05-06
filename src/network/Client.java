package network;

import structures.Chessboard_NEW;
import structures.Game;
import structures.History;
import structures.chesses.Chess;
import structures.chesses.Elephant;
import structures.players.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Client {
    static GUI myGUI;
    static int nowPlayer;
    static Player player;
    static int colorMode = 1;
    // gameMode = 1 : 先手
    // gameMode = -1 : 后手
    static int counterPlayerMode = 0;
    // counterPlayerMode = 1 : 入门
    // counterPlayerMode = 2 : 简单
    // counterPlayerMode = 3 : 普通
    static Game game = new Game();

    public static void setNowPlayer(int nowPlayer) {
        Client.nowPlayer = nowPlayer;
    }

    public static void setPlayer(Player player) {
        Client.player = player;
    }

    public static void winPaint(int player) {
        myGUI.winPaint(player);
    }

    public static void updateGamePaint() {
        myGUI.updateGamePaint();
    }

    public static void setCountTime(int nowPlayer, int time) {
        myGUI.countTimePaint(nowPlayer, time);
    }
    public static void main(String[] args) {

        try {
            String lookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
            UIManager.setLookAndFeel(lookAndFeel);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        myGUI = new GUI();
        myGUI.run();

    }
    private static class GUI {
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
        JPanel mapDrawPanel = new MyDrawPanel();
        //按钮
        JButton localGameButton = new JButton("本地游戏");
        JButton onlineGameButton = new JButton("联机对战");
        JButton exitGameButton = new JButton("退出游戏");
        JButton againstPlayerButton = new JButton("玩家VS玩家");
        JButton againstComputerButton = new JButton("玩家VS电脑");
        JButton backInitButton = new JButton("返回主界面");
        JButton pauseButton = new JButton("暂停");
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
        //文本框
        JTextArea messageBox = new JTextArea(35, 40);
        JTextField inputBox = new JTextField(20);
        JScrollPane messageBoxScroller = new JScrollPane(messageBox);
        //图片
        ImageIcon mainFrameImgOrig = new ImageIcon("data/img/Jungle.jpg");
        ImageIcon mainFrameImg = new ImageIcon("data/img/Jungle.jpg");
        // source: https://www.zcool.com.cn/work/ZMzg3NjU3Njg=.html
        ImageIcon mapImgOrig = new ImageIcon("data/img/Map3.png");
        ImageIcon mapImg = new ImageIcon("data/img/Map3.png");
        //标签
        JLabel mainFrameImgLabel = new JLabel(mainFrameImg);
        JLabel mapImgLabel = new JLabel(mapImg);
        JLabel chooseDifficultyLabel = new JLabel("难度：");
        JLabel chooseColorLabel = new JLabel("行动：");
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

            if (clicked) {
                drawChoose();
            }

            mainFramePanel.add(mapImgLabel);
            mainFramePanel.add(gameButtonPanel);
            mainFramePanel.add(messagePanel);

            mainFrame.repaint();

        }

        private void drawAnimals() {
            mainFramePanel.setLayout(null);
            Chessboard_NEW chessboard = game.getChessboard();
            for (int i = 1; i <= Chessboard_NEW.getSizeY(); i++) {
                for (int j = 1; j <= Chessboard_NEW.getSizeX(); j++) {
                    Chess chess = chessboard.getChess(j, i);
                    if (chess != null) {
                        int posX = 65 + 80 * j;
                        int posY = 45 + 80 * i;

                        ImageIcon chessImg = chess.getImg();

                        chessImg.setImage(chessImg.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));

                        JLabel imgLabel = new JLabel(chessImg);
                        imgLabel.setBounds(posX, posY, 80, 80);

                        mainFramePanel.add(imgLabel);
                    }
                }
            }
            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));
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

        private void updateMessageBox() {
            messageBox.setText("");
            String[] lines = game.getMessages();
            for (String line : lines) {
                messageBox.append(line + "\n");
            }
        }

        private void winPaint(int player) {
            String winner;
            if (player == 1) {
                winner = "蓝方";
            } else {
                winner = "红方";
            }
            JOptionPane.showMessageDialog(mainFrame, winner + "获胜！");
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

        public void countTimePaint(int nowPlayer, int time) {
            // TODO: ERROR
            int posY = 10;
            int posX = 10;

            ImageIcon image = new ImageIcon("data/img/animals/blue/1.png");
            JLabel testLabel = new JLabel(image);

            testLabel.setBounds(50 * time, 10, 100, 100);

//            mainFramePanel.removeAll();
            mainFramePanel.setLayout(null);
            mainFramePanel.add(testLabel);
            mainFramePanel.repaint();
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

            mainFrame.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //在游戏中才生效
                    if (page == 4) {
                        int[] clickPos = getClickPos(e);
                        if (!Chess.isOutOfBound(clickPos)) {
                            System.out.printf("(%d, %d) Clicked.\n", clickPos[0], clickPos[1]);
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
                        System.out.printf("(%d, %d)\n", move[0], move[1]);
                    }
                } else {
                    System.out.println("Illegal Click");
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
                } else {
                    System.out.println("Illegal Click");
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
            exitGameButton.setFont(buttonFont);

            localGameButton.addActionListener(new localGameButtonListener());
            onlineGameButton.addActionListener(new onlineGameButtonListener());
            exitGameButton.addActionListener(new exitGameButtonListener());

            // 将三个按钮依次加入初始界面按钮面板中
            initButtonPanel.setLayout(new BoxLayout(initButtonPanel, BoxLayout.Y_AXIS));
            initButtonPanel.add(localGameButton);
            initButtonPanel.add(onlineGameButton);
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
            pauseButton.setFont(buttonFont);
            regretButton.setFont(buttonFont);
            resetButton.setFont(buttonFont);
            saveButton.setFont(buttonFont);
            loadButton.setFont(buttonFont);
            backButton.setFont(buttonFont);

            pauseButton.addActionListener(new pauseButtonListener());
            regretButton.addActionListener(new regretButtonListener());
            resetButton.addActionListener(new resetButtonListener());
            saveButton.addActionListener(new saveButtonListener());
            loadButton.addActionListener(new loadButtonListener());
            backButton.addActionListener(new backButtonListener());

            gameButtonPanel.setLayout(new BoxLayout(gameButtonPanel, BoxLayout.Y_AXIS));
            gameButtonPanel.add(pauseButton);
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












        private class localGameButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                localGamePaint();
            }
        }
        private class onlineGameButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                onlineGamePaint();
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
        private class pauseButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        }
        private class regretButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getPlayer().getIdentity() == 1 && game.getHistorySize() > 1) {
                    game.buildFromHistory(2);
                    updateGamePaint();
                    game.messageInput("Regret", nowPlayer);
                    System.out.println("Regretted successfully!");
                    JOptionPane.showMessageDialog(mainFrame, "悔棋成功");
                } else {
                    if (game.getPlayer().getIdentity() != 1) {
                        JOptionPane.showMessageDialog(mainFrame, "悔棋失败：不是你的回合");
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "悔棋失败：无可悔棋步骤");
                    }
                }
            }
        }
        private class resetButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                gameStart.interrupt();
                game = new Game(game.getPlayerBlue(), game.getPlayerRed());
                gameStart = new GameStart();
                gameStart.start();
                System.out.println("Resettled successfully!");
                JOptionPane.showMessageDialog(mainFrame, "重置成功");
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
                    GameStart.interrupted();
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
                    for (int i = 1; i <= size; i++) {
                        String line = fHistoryReader.readLine();
                        String[] elements = line.split(" ");
                        history[i][0][0] = Integer.parseInt(elements[0]);
                        history[i][0][1] = Integer.parseInt(elements[1]);
                        history[i][1][0] = Integer.parseInt(elements[2]);
                        history[i][1][1] = Integer.parseInt(elements[3]);
                    }
                    History h = new History(history, size);
                    game.buildFromHistory(h);
                } catch (FileNotFoundException e) {
                    System.out.println("历史文件读取错误");
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }








        class MyDrawPanel extends JPanel {
            public void paintComponent(Graphics g) {
                Image mapImgDraw = mapImg.getImage();
                g.drawImage(mapImgDraw, 0, 0, this);
            }
        }

        static class GameStart extends Thread {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                game.start();
            }
        }

    }
}
