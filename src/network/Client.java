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
        ImageIcon mainFrameImg = new ImageIcon("data/img/Jungle.jpg");
        // source: https://www.zcool.com.cn/work/ZMzg3NjU3Njg=.html
        //标签
        JLabel mainFrameImgLabel = new JLabel(mainFrameImg);
        //字体
        Font buttonFont = new Font("楷体", Font.BOLD, 20);
        public static void main(String[] args) {
            // 设置Swing窗体风格
            try {
                String lookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
                UIManager.setLookAndFeel(lookAndFeel);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 新建GUI对象并运行
            GUI gui = new GUI();
            gui.run();
        }
        public void run() {
            // 加载主窗体
            loadMainFrame();

            // 加载初始界面的按钮面板
            loadInitButtonPanel();

            // 加载本地游戏选择界面的按钮面板
            loadLocalGameButtonPanel();

            // 设置主窗体的图片大小
            mainFrameImg.setImage(mainFrameImg.getImage().getScaledInstance(700, 500, Image.SCALE_DEFAULT));

            // 在上述准备工作均完成后，绘制主窗体
            initPaint();
        }
        private void initPaint() {
            // 清空主窗体
            mainFramePanel.removeAll();

            // 在主窗体中加入主窗体图片以及初始界面的按钮面板
            mainFramePanel.add(mainFrameImgLabel);
            mainFramePanel.add(initButtonPanel);

            // 设置mainFrame大小以包装上述组件并显示
            mainFrame.pack();
            mainFrame.setVisible(true);
        }
        private void localGamePaint() {
            // 清空主窗体
            mainFramePanel.removeAll();

            // 在主窗体中加入主窗体图片以及本地游戏选择按钮面板
            mainFramePanel.add(mainFrameImgLabel);
            mainFramePanel.add(localGameButtonPanel);

            // 设置mainFrame大小以包装上述组件并显示
            mainFrame.pack();
        }
        private void onlineGamePaint() {

        }
        private void loadMainFrame() {
            // 设置主窗体面板的布局
            mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));

            // 设置主窗体基本属性，并将主窗体面板加入到主窗体上
            mainFrame = new JFrame();
            mainFrame.setContentPane(mainFramePanel);
            mainFrame.setLocation(300, 150);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
