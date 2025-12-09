package com.yychat.control;

import com.yychat.model.Message;
import com.yychat.model.MessageType;
import com.yychat.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class YychatClientConnection {
    public static Socket s;

    public YychatClientConnection() {
        try {
            s = new Socket("127.0.0.1", 3456);
            System.out.println("客户端连接成功：" + s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {
        boolean registerSuccess = false;
        try {
            OutputStream os = s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(user);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            Message mess = (Message) ois.readObject();
            if (mess.getMessageType().equals(MessageType.USER_REGISTER_SUCCESS)) {
                registerSuccess = true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return registerSuccess;
    }

    //    public void loginValidate(User user) {
    public boolean loginValidate(User user) {
        boolean loginSuccess = false;
        try {
            OutputStream os = s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(user);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            Message mess = (Message) ois.readObject();
            if (mess.getMessageType().equals(MessageType.LOGIN_VALLDATE_SUCCESS)) {
                loginSuccess = true;
                new ClientReceiverThread(s).start();
            } else s.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return loginSuccess;
    }

    public Message loginValidate1(User user) {
        Message mess = null;
        try {
            OutputStream os = s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(user);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            mess = (Message)ois.readObject();
            if (mess.getMessageType().equals(MessageType.LOGIN_VALLDATE_SUCCESS)) {
                new ClientReceiverThread(s).start();
            }else {
                s.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return mess;
    }
}
