package com.yt.atm;

import org.ietf.jgss.GSSContext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {

    public static String balance = "50000";

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(2525);

        //把本地文件中正确的用户名和密码获取到
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("servicedir\\userinfo.txt");
        prop.load(fis);
        fis.close();

        //只要来了一个客户端，就开一条线程处理
        while (true) {
            Socket socket = ss.accept();
            System.out.println("有客户端来连接");
            new Thread(new MyRunnable(socket, prop)).start();
        }

    }


}

class MyRunnable implements Runnable {
    Socket socket;
    Properties prop;

    public MyRunnable(Socket socket, Properties prop) {
        this.prop = prop;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //用while包裹，保证能输错后还能继续输入
            while (true) {
                //第一次读：读取用户输入的登录信息
                String userInfo = br.readLine();
                //调用登录函数判断信息是否正确
                judgeLoginInfo(userInfo);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void judgeLoginInfo(String userInfo) throws IOException {
        //获取输出流
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //拆分用户输入的信息
        String[] userInfoArr = userInfo.split("&");
        String usernameInput = userInfoArr[0];
        String passwordInput = userInfoArr[1];
        System.out.println("用户输入的用户名为：" + usernameInput);
        System.out.println("用户输入的密码为：" + passwordInput);
        if (prop.containsKey(usernameInput)) {
            //第一次回写：登录成功响应码码
            bw.write("525");
            bw.newLine();
            bw.flush();
            //登录成功，进入功能选择界面
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //接受客户端转来的信息，并判断应该执行的操作
                String choice = br.readLine();
                switch (choice) {
                    case "BALA":
                        //收到客户端传来的“BALA"信息，将余额传回客户端
                        bw.write(Server.balance);
                        bw.newLine();
                        bw.flush();
                        break;
                    case "WDRA":
                        //收到客户端传来的”WDRA“信息，执行取款操作
                        //再接受客户端传来的取款金额
                        String withdrawNum = br.readLine();
                        //将计算后的余额传回客户端
                        if ((Integer.parseInt(Server.balance) - Integer.parseInt(withdrawNum)) < 0) {
                            bw.write("401");
                            bw.newLine();
                            bw.flush();
                            bw.write(Server.balance);
                            bw.newLine();
                            bw.flush();
                            break;
                        } else {
                            bw.write("525");
                            bw.newLine();
                            bw.flush();
                            Server.balance = String.valueOf(Integer.parseInt(Server.balance) - Integer.parseInt(withdrawNum));
                            bw.write(Server.balance);
                            bw.newLine();
                            bw.flush();
                            break;
                        }


                    case "SAVE":
                        //收到客户端传来的”SAVA“信息，执行取款操作
                        //再接受客户端传来的存款金额
                        String saveNum = br.readLine();
                        //将计算后的余额传回客户端
                        Server.balance = String.valueOf(Integer.parseInt(Server.balance) + Integer.parseInt(saveNum));
                        bw.write(Server.balance);
                        bw.newLine();
                        bw.flush();
                        break;
                    case "BYE":
                        bw.write("BYE");
                        bw.newLine();
                        bw.flush();
                        break;

                }
            }
        } else {
            //第一次回写：登录失败响应码
            bw.write("401");
            bw.newLine();
            bw.flush();
        }

    }
}
