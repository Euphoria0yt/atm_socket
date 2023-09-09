package com.yt.atm;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class FuncFrame {

    JFrame funcFrame=new JFrame("ATM");
    public void init(Socket socket,Client client) throws IOException {
        funcFrame.setBounds(800, 400, 440, 400);
        funcFrame.setResizable(false);
        JPanel root = new JPanel();
        funcFrame.setContentPane(root);
        root.setLayout(null);

        //获取窗口大小
        int width = funcFrame.getWidth();
        int height = funcFrame.getHeight();

        //为登录窗口添加背景图片
        ImageIcon background = new ImageIcon("images\\1.jpg");
        // 创建一个JLabel对象，显示背景图片
        JLabel label = new JLabel(background);
        // 设置标签的大小和位置为图片的大小和位置
        label.setBounds(0, 0, 440, 600);
        // 将内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
        JPanel imagePanel = (JPanel) funcFrame.getContentPane();
        imagePanel.setOpaque(false);
        // 将背景图片添加到分层窗格的最底层作为背景
        funcFrame.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));

        //添加提示标签
        JLabel tipsLabel=new JLabel("请选择您要进行的业务");
        root.add(tipsLabel);
        tipsLabel.setBounds((int) (width * 0.32), (int) (height * 0.04), 200, 40);
        tipsLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));

        //获取输出流
        BufferedWriter bw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        //获取输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        //添加查询按钮
        JButton balanceButton=new JButton("查询余额");
        //取消按钮焦点
        balanceButton.setFocusPainted(false);
        root.add(balanceButton);
        balanceButton.setBounds((int) (width * 0.27), (int) (height * 0.18), 200, 40);
        balanceButton.addActionListener((e)->{
            try {
                //写入选择的功能，通过规定的字符传给客服端
                bw.write("BALA");
                bw.newLine();
                bw.flush();

                //接收服务端传来的信息,并显示
                JOptionPane.showMessageDialog(funcFrame,"您当前的余额为："+br.readLine());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        //添加取款按钮
        JButton withdrawButton=new JButton("取款");
        //取消按钮焦点
        withdrawButton.setFocusPainted(false);
        root.add(withdrawButton);
        withdrawButton.setBounds((int) (width * 0.27), (int) (height * 0.31), 200, 40);
        withdrawButton.addActionListener((e)->{
            try {
                //写入选择的功能，通过规定的字符传给客服端
                bw.write("WDRA");
                bw.newLine();
                bw.flush();

                //获取取款金额
                String withdrawNum = JOptionPane.showInputDialog(funcFrame, "请输入您的取款金额：", "输入", JOptionPane.PLAIN_MESSAGE);
                //再将取款金额传进去
                bw.write(withdrawNum);
                bw.newLine();
                bw.flush();
                //接收服务端传来的取款后的余额信息,并显示
                if("401".equals(br.readLine())){
                    JOptionPane.showMessageDialog(funcFrame,"取款失败，当前余额不足！\n 您当前的余额为："+br.readLine());
                }else{
                    JOptionPane.showMessageDialog(funcFrame,"取款成功！\n 您当前的余额为："+br.readLine());

                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        //添加存款按钮
        JButton saveButton=new JButton("存款");
        //取消按钮焦点
        saveButton.setFocusPainted(false);
        root.add(saveButton);
        saveButton.setBounds((int) (width * 0.27), (int) (height * 0.44), 200, 40);
        saveButton.addActionListener((e)->{
            String saveNum = JOptionPane.showInputDialog(funcFrame, "请输入您的存款金额：", "输入", JOptionPane.PLAIN_MESSAGE);
            try {
                //写入选择的功能，通过规定的字符传给客服端
                bw.write("SAVE");
                bw.newLine();
                bw.flush();

                //再将取款金额传进去
                bw.write(saveNum);
                bw.newLine();
                bw.flush();

                //接收服务端传来的取款后的余额信息,并显示
                JOptionPane.showMessageDialog(funcFrame,"存款成功！\n 您当前的余额为："+br.readLine());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        //添加帮助按钮
        JButton helpButton=new JButton("帮助");
        //取消按钮焦点
        helpButton.setFocusPainted(false);
        root.add(helpButton);
        helpButton.setBounds((int) (width * 0.27), (int) (height * 0.57), 200, 40);
        helpButton.addActionListener((e)->{
            JOptionPane.showMessageDialog(funcFrame, "请拨打在线客服：4006 700 700");
        });

        //添加退出按钮
        JButton exitButton=new JButton("退出");
        //取消按钮焦点
        exitButton.setFocusPainted(false);
        root.add(exitButton);
        exitButton.setBounds((int) (width * 0.27), (int) (height * 0.70), 200, 40);
        exitButton.addActionListener((e)->{
            int result = JOptionPane.showConfirmDialog(funcFrame, "您确认要退出程序吗？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    //用户点击确认后，发送”BYE“给服务端
                    bw.write("BYE");
                    bw.newLine();
                    bw.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                //收到服务端的"BYE指令后退出程序
                try {
                    if("BYE".equals(br.readLine())){
                        funcFrame.dispose();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        funcFrame.setVisible(true);
    }

}
