package com.yt.atm;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static com.yt.atm.Client.login;

public class MainFrame {
    //1.创建Socket对象并连接服务端
    Socket socket = new Socket("127.0.0.1", 2525);
    //创建客户端对象
    Client client = new Client();


    //创建一个登录窗口
    //利用多态创建对象
    JFrame loginFrame = new JFrame("ATM");


    //为文本框前添加标签控件
    JLabel userIdLabel = new JLabel("用户名");
    JLabel passwordLabel = new JLabel("密码");
    JLabel loginLabel = new JLabel("ATM");
    //添加文本框
    JTextField userIdText = new JTextField(20);
    JTextField passwordText = new JTextField(20);
    //添加登录按钮
    JButton loginButton = new JButton("登录");
    //添加复选框控件
    JCheckBox agreeCheckBox = new JCheckBox("同意用户协议");

    public MainFrame() throws IOException {
    }


    //组装窗口
    public void init() {
        loginFrame.setBounds(800, 400, 440, 330);
        loginFrame.setResizable(false);

        JPanel root = new JPanel();
        loginFrame.setContentPane(root);
        root.setLayout(null);

        //为登录窗口添加背景图片
        ImageIcon background = new ImageIcon("images\\1.jpg");
        // 创建一个JLabel对象，显示背景图片
        JLabel label = new JLabel(background);
        // 设置标签的大小和位置为图片的大小和位置
        label.setBounds(0, 0, 440, 330);
        // 将内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
        JPanel imagePanel = (JPanel) loginFrame.getContentPane();
        imagePanel.setOpaque(false);
        // 将背景图片添加到分层窗格的最底层作为背景
        loginFrame.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));


        //为文本框前添加标签控件
        root.add(userIdLabel);
        root.add(passwordLabel);
        root.add(loginLabel);
        userIdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginLabel.setFont(new Font("微软雅黑", Font.PLAIN, 25));


        //添加文本框
        root.add(userIdText);
        root.add(passwordText);

        //添加登录按钮
        root.add(loginButton);
        //取消按钮焦点
        loginButton.setFocusPainted(false);
        //为按钮添加监听器
        loginButton.addActionListener((e) -> {
            //判断输入的密码是否正确，如果正确的话，就跳转到新窗口
            String judgePassword = judgePassword();
            if ("525".equals(judgePassword)) {
                JOptionPane.showMessageDialog(loginFrame, "登录成功");
                try {
                    new FuncFrame().init(socket, client);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                loginFrame.dispose();
            } else if ("401".equals(judgePassword)) {
                JOptionPane.showMessageDialog(loginFrame, "密码或用户名输入错误，请重试！");
            }
        });

        //添加复选框控件,并与登录按钮绑定
        root.add(agreeCheckBox);
        agreeCheckBox.setSelected(false);
        loginButton.setEnabled(false);
        agreeCheckBox.addActionListener((e) -> {
            if (agreeCheckBox.isSelected()) {
                loginButton.setEnabled(true);
            } else {
                loginButton.setEnabled(false);
            }
        });
        agreeCheckBox.setOpaque(false);

        int width = loginFrame.getWidth();
        int height = loginFrame.getHeight();
        loginLabel.setLocation((int) (width * 0.43), (int) (height * 0.18));
        loginLabel.setSize(loginLabel.getPreferredSize());
        userIdLabel.setLocation((int) (width * 0.19), (int) (height * 0.32));
        userIdLabel.setSize(userIdLabel.getPreferredSize());
        passwordLabel.setLocation((int) (width * 0.21), (int) (height * 0.42));
        passwordLabel.setSize(passwordLabel.getPreferredSize());
        userIdText.setLocation((int) (width * 0.31), (int) (height * 0.33));
        userIdText.setSize(userIdText.getPreferredSize());
        passwordText.setLocation((int) (width * 0.31), (int) (height * 0.43));
        passwordText.setSize(passwordText.getPreferredSize());
        agreeCheckBox.setLocation((int) (width * 0.41), (int) (height * 0.51));
        agreeCheckBox.setSize(agreeCheckBox.getPreferredSize());
        loginButton.setBounds((int) (width * 0.22), (int) (height * 0.61), 250, 25);

        loginFrame.setVisible(true);
    }

    private String judgePassword() {
        //获取用户输入的值
        String inputUserId = userIdText.getText();
        String inputPassword = passwordText.getText();
        try {
            //调用客户端login函数,并返回收到的状态码
            return login(inputUserId, inputPassword, socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
