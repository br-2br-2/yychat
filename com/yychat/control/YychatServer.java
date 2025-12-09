package com.yychat.control;

import com.yychat.model.Message;
import com.yychat.model.MessageType;
import com.yychat.model.User;
import com.yychat.model.UserType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class YychatServer {
    public static HashMap<String, Socket> hmSocket = new HashMap<>();
    ServerSocket ss;
    Socket s;

    public YychatServer() {
        try {
            ss = new ServerSocket(3456);
            System.out.println("服务器启动成功，正在监听3456端口...");
            while (true) {
                s = ss.accept();
                System.out.println("连接成功：" + s);
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                User user = (User) ois.readObject();
                String userName = user.getUserName();
                String passWord = user.getPassWord();
                System.out.println("服务器端接收到的客户端登录信息\nuserName：" + userName + "\npassWord：" + passWord);
//                Class.forName("com.mysql.cj.jdbc.Driver");
//                String db_url="jdbc:mysql://localhost:3306/yychat2022s?useUnicode=true&characterEncoding=utf-8";
//                Connection conn;
//                boolean loginSuccess=false;
//                try {
//                    conn= DriverManager.getConnection(db_url,"root","123456");
//                    String user_query_str="select * from user where username=? and password=?";
//                    PreparedStatement psmt = conn.prepareStatement(user_query_str);
//                    psmt.setString(1,userName);
//                    psmt.setString(2,passWord);
//                    ResultSet rs=psmt.executeQuery();
//                    loginSuccess=rs.next();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                Message mess = new Message();
                if (user.getUserType().equals(UserType.USER_REGISTER)) {
                    if (DBUtil.seekUser(userName)) {
                        mess.setMessageType(MessageType.USER_REGISTER_FAILURE);
                    } else {
                        DBUtil.insertIntoUser(userName, passWord);
                        mess.setMessageType(MessageType.USER_REGISTER_SUCCESS);
                    }
                    oos.writeObject(mess);
                    s.close();
                }
                if (user.getUserType().equals(UserType.USER_LOGIN_VALLDATE)) {
                    boolean loginSuccess = DBUtil.loginValidate(userName, passWord);
                    if (loginSuccess) {
                        System.out.println("密码正确");
                        String allFriend=DBUtil.seekAllUser(userName,1);
                        mess.setContent(allFriend);
                        mess.setMessageType(MessageType.LOGIN_VALLDATE_SUCCESS);
                        oos.writeObject(mess);
                        hmSocket.put(userName, s);
                        new ServerReceiverThread(s).start();
                        System.out.println("线程启动成功");
                    } else {
                        System.out.println("密码错误");
                        mess.setMessageType(MessageType.LOGIN_VALLDATE_FALLURE);
                        oos.writeObject(mess);
                        s.close();
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

}
