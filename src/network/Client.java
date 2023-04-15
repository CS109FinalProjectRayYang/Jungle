package network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Client {
    public static void main(String[] args) {

    }
    private static class GUI {
        //窗体
        JFrame mainFrame;
        //面板
        JPanel mainFramePanel = new JPanel();
        JPanel initButtonPanel = new JPanel();
        JPanel localGameButtonPanel = new JPanel();
        //按钮
        JButton localGameButton = new JButton("本地游戏");
        JButton onlineGameButton = new JButton("联机对战");
        JButton exitGameButton = new JButton("退出游戏");
        JButton againstPlayerButton = new JButton("玩家VS玩家");
        JButton againstComputerButton = new JButton("玩家VS电脑");
        JButton backInitButton = new JButton("返回主界面");
        //文本框
        //图片
        ImageIcon jungleImg = new ImageIcon("data/img/Jungle.jpg");
        //标签
        JLabel jungleImgLabel = new JLabel(jungleImg);
        //字体
        Font buttonFont = new Font("楷体", Font.BOLD, 20);
        public static void main(String[] args) {
            try {
                String lookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
                UIManager.setLookAndFeel(lookAndFeel);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            GUI gui = new GUI();
            gui.run();
        }
        public void run() {
            loadMainFrame();
            loadInitButtonPanel();
            loadLocalGameButtonPanel();
            jungleImg.setImage(jungleImg.getImage().getScaledInstance(700, 500, Image.SCALE_DEFAULT));
            initPaint();
        }
        private void initPaint() {
            mainFramePanel.removeAll();
            mainFramePanel.add(jungleImgLabel);
            mainFramePanel.add(initButtonPanel);

            mainFrame.pack();
            mainFrame.setVisible(true);
        }
        private void localGamePaint() {
            mainFramePanel.removeAll();
            mainFramePanel.add(jungleImgLabel);
            mainFramePanel.add(localGameButtonPanel);

            mainFrame.pack();
        }
        private void onlineGamePaint() {

        }
        private void loadMainFrame() {
            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));

            mainFrame = new JFrame();
            mainFrame.setContentPane(mainFramePanel);
            mainFrame.setLocation(300, 150);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        }
        private void loadInitButtonPanel() {
            localGameButton.setFont(buttonFont);
            onlineGameButton.setFont(buttonFont);
            exitGameButton.setFont(buttonFont);

            localGameButton.addActionListener(new localGameButtonListener());
            onlineGameButton.addActionListener(new onlineGameButtonListener());
            exitGameButton.addActionListener(new exitGameButtonListener());

            initButtonPanel.setLayout(new BoxLayout(initButtonPanel, BoxLayout.Y_AXIS));
            initButtonPanel.add(localGameButton);
            initButtonPanel.add(onlineGameButton);
            initButtonPanel.add(Box.createVerticalGlue());
            initButtonPanel.add(exitGameButton);
        }
        private void loadLocalGameButtonPanel() {
            againstPlayerButton.setFont(buttonFont);
            againstComputerButton.setFont(buttonFont);
            backInitButton.setFont(buttonFont);

            againstPlayerButton.addActionListener(new againstPlayerButtonListener());
            againstComputerButton.addActionListener(new againstComputerButtonListener());
            backInitButton.addActionListener(new backInitButtonListener());

            localGameButtonPanel.setLayout(new BoxLayout(localGameButtonPanel, BoxLayout.Y_AXIS));
            localGameButtonPanel.add(againstPlayerButton);
            localGameButtonPanel.add(againstComputerButton);
            localGameButtonPanel.add(Box.createVerticalGlue());
            localGameButtonPanel.add(backInitButton);
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
            }
        }
        private class againstComputerButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        }
        private class backInitButtonListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                initPaint();
            }
        }
    }
}
