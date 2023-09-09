package com.yt.atm;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        new MainFrame().init();

        System.out.println("服务器已经连接成功");
    }

    public static String login(String userId, String password, Socket socket) throws IOException {
        //获取输出流
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        //拼接收到的用户名和密码
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append("&").append(password);
        //第一次写：往服务器写用户名和密码
        bw.write(sb.toString());
        bw.newLine();
        bw.flush();

        //第一次接受数据
        //获取输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //接收从服务端返回的值
        return br.readLine();
    }
}

