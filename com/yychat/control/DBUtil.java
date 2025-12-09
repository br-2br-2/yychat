package com.yychat.control;

import com.yychat.model.Message;

import java.sql.*;
import java.util.Date;

public class DBUtil {
    public static String db_url = "jdbc:mysql://localhost:3306/yychat2022s?useUnicode=true&characterEncoding=utf-8";
    public static Connection conn = getConnection();

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(db_url, "root", "123456");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static boolean loginValidate(String userName, String passWord) {
        boolean loginSuccess = false;
        try {
            String user_query_str = "select * from user where username=? and password=?";
            PreparedStatement psmt = conn.prepareStatement(user_query_str);
            psmt.setString(1, userName);
            psmt.setString(2, passWord);
            ResultSet rs = psmt.executeQuery();
            loginSuccess = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loginSuccess;
    }

    public static void insertIntoUser(String userName, String passWord) {
        int count = 0;
        String user_inset_into_str = "insert into user(username,password) value(?,?)";
        PreparedStatement psmt;
        try {
            psmt = conn.prepareStatement(user_inset_into_str);
            psmt.setString(1, userName);
            psmt.setString(2, passWord);
            count = psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return count;
    }

    public static boolean seekUser(String userName) {
        boolean seekSuccess = false;
        String user_query_str = "select * from user where username=?";
        PreparedStatement psmt;
        try {
            psmt = conn.prepareStatement(user_query_str);
            psmt.setString(1, userName);
            ResultSet rs = psmt.executeQuery();
            seekSuccess = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seekSuccess;
    }

    public static String seekAllUser(String userName, int FriendType) {
        String allFriend = "";
        String correlation_query_str = "select slaveuser from userrelation where masteruser=? and relation=?";
        PreparedStatement psmt;
        try {
            psmt = conn.prepareStatement(correlation_query_str);
            psmt.setString(1, userName);
            psmt.setInt(2, FriendType);
            ResultSet rs = psmt.executeQuery();
            while (rs.next())
                allFriend = allFriend + " " + rs.getString(1);
            System.out.println(userName+" 全部好友："+allFriend);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allFriend;
    }

    public static boolean seekFriend(String sender, String newFriend, int FriendType) {
        boolean seekSuccess=false;
        String userrelation_query_str="select * from userrelation where masteruser=? and slaveuser=? and relation=?";
        PreparedStatement psmt;
        try {
            psmt=conn.prepareStatement(userrelation_query_str);
            psmt.setString(1,sender);
            psmt.setString(2,newFriend);
            psmt.setInt(3,FriendType);
            ResultSet rs = psmt.executeQuery();
            seekSuccess=rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seekSuccess;
    }

    public static void insertIntoUser(String sender, String newFriend, int friendType) {
        int count=0;
        String userrelation_insertInto_str="insert into userrelation(masteruser, slaveuser, relation) values (?,?,?)";
        try {
            PreparedStatement psmt=conn.prepareStatement(userrelation_insertInto_str);
            psmt.setString(1,sender);
            psmt.setString(2,newFriend);
            psmt.setInt(3,friendType);
            count=psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return count;
    }

    public static void saveMessage(Message mess) {
        String message_insertInto_str="insert into message(sender, receiver, content, sendtime) values (?,?,?,?)";
        PreparedStatement psmt;
        try {
            psmt=conn.prepareStatement(message_insertInto_str);
            psmt.setString(1,mess.getSender());
            psmt.setString(2,mess.getReceiver());
            psmt.setString(3,mess.getContent());
            Date sendTime =mess.getSendTime();
            psmt.setTimestamp(4,new java.sql.Timestamp(sendTime.getTime()));
            psmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
