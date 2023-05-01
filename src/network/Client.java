package network;

import structures.Chessboard_NEW;
import structures.Game;
import structures.chesses.Chess;
import structures.players.ComputerPlayer;
import structures.players.HumanPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Client {
    static GUI myGUI;
    static int nowPlayer;
    static Game game = new Game();

    public static void setNowPlayer(int nowPlayer) {
        Client.nowPlayer = nowPlayer;
    }
    public static void winPaint(int player) {
        myGUI.winPaint(player);
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
        //按钮
        JButton localGameButton = new JButton("本地游戏");
        JButton onlineGameButton = new JButton("联机对战");
        JButton exitGameButton = new JButton("退出游戏");
        JButton againstPlayerButton = new JButton("玩家VS玩家");
        JButton againstComputerButton = new JButton("玩家VS电脑");
        JButton backInitButton = new JButton("返回主界面");
        JButton pauseButton = new JButton("暂停");
        JButton backButton = new JButton("返回");
        //文本框
        //图片
        ImageIcon mainFrameImgOrig = new ImageIcon("data/img/Jungle.jpg");
        ImageIcon mainFrameImg = new ImageIcon("data/img/Jungle.jpg");
        // source: https://www.zcool.com.cn/work/ZMzg3NjU3Njg=.html
        ImageIcon mapImgOrig = new ImageIcon("data/img/Map3.png");
        ImageIcon mapImg = new ImageIcon("data/img/Map3.png");
        //标签
        JLabel mainFrameImgLabel = new JLabel(mainFrameImg);
        JLabel mapImgLabel = new JLabel(mapImg);
        //字体
        Font buttonFont = new Font("楷体", Font.BOLD, 20);





        public void run() {
            // 加载主窗体
            loadMainFrame();

            // 加载初始界面的按钮面板
            loadInitButtonPanel();

            // 加载本地游戏选择界面的按钮面板
            loadLocalGameButtonPanel();

            loadGameButtonPanel();

            // 设置图片大小
            mainFrameImg.setImage(mainFrameImgOrig.getImage().getScaledInstance(700, 560, Image.SCALE_DEFAULT));
            mapImg.setImage(mapImgOrig.getImage().getScaledInstance(1000, 700, Image.SCALE_DEFAULT));

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

            mainFrame.setLocation(200, 100);
            mainFrame.pack();

            GameStart gameStart = new GameStart();
            gameStart.start();
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
                        System.out.printf("(%d, %d) Clicked.\n", clickPos[0], clickPos[1]);
                        if (!game.getInputted()) {
                            click(clickPos);
                        }
                    }
                }
            });
        }


        private void click ( int[] clickPos){
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
                    game.getChessboard().moveChess(clickedPos, clickPos);
                    game.input("Finished.");
                } else {
                    System.out.println("Illegal Click");
                }

                // 复位
                clicked = false;
            }
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
            backButton.setFont(buttonFont);

            pauseButton.addActionListener(new pauseButtonListener());
            backButton.addActionListener(new backButtonListener());

            gameButtonPanel.setLayout(new BoxLayout(gameButtonPanel, BoxLayout.Y_AXIS));
            gameButtonPanel.add(pauseButton);
            gameButtonPanel.add(Box.createVerticalGlue());
            gameButtonPanel.add(backButton);
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
                game = new Game();
                game.setPlayer(new HumanPlayer(), new ComputerPlayer());
                gamePaint();
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
        private class backButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                int userOption = JOptionPane.showConfirmDialog(mainFrame, "确认退出游戏吗？");
                if (userOption == JOptionPane.OK_OPTION) {
                    GameStart.interrupted();
                    initPaint();
                }
            }
        }
        static class GameStart extends Thread {
            @Override
            public void run() {
                game.start();
            }
        }
    }
}
