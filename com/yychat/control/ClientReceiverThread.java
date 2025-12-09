package com.yychat.control;

import com.yychat.model.Message;
import com.yychat.model.MessageType;
import com.yychat.view.ClientLogin;
import com.yychat.view.FriendChat;
import com.yychat.view.FriendList;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientReceiverThread extends Thread {
    Socket s;

    public ClientReceiverThread(Socket s) {
        this.s = s;
    }

    public void run() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                Message mess = (Message) ois.readObject();
                if (mess.getMessageType().equals(MessageType.USER_EXIT_CLIENT_THREAD_CLOSE)) {
                    System.out.println("关闭 " + mess.getSender() + " 用户接收线程");
                    s.close();
                    break;
                }
                if (mess.getMessageType().equals(MessageType.ADD_NEW_FRIEND_FALLURE_NO_USER)) {
                    JOptionPane.showMessageDialog(null, "新好友名字不存在，添加好友失败！");
                }
                if (mess.getMessageType().equals(MessageType.ADD_NEW_FRIEND_FALLURE_ALREADY_FRIEND)) {
                    JOptionPane.showMessageDialog(null, "该用户已经是好友了，不能重复添加");
                }
                if (mess.getMessageType().equals(MessageType.ADD_NEW_FRIEND_SUCCESS)) {
                    JOptionPane.showMessageDialog(null, "添加好友成功！");
                    String sender = mess.getSender();
                    FriendList fl = (FriendList) ClientLogin.hmFriendList.get(sender);
                    String allFriend = mess.getContent();
                    fl.showAllFriend(allFriend);
                }
                if (mess.getMessageType().equals(MessageType.COMMON_CHAT_MESSAGE)) {
                    String receiver = mess.getReceiver();
                    String sender = mess.getSender();
                    FriendChat fc = (FriendChat) FriendList.hmFriendChat.get(receiver + "to" + sender);
                    if (fc != null) {
                        fc.append(mess);
                    } else
                        System.out.println("请打开" + receiver + "to" + sender + "的聊天界面");
                }
                if (mess.getMessageType().equals(MessageType.RESPONSE_ONLINE_FRIEND)) {
                    FriendList fl = (FriendList) ClientLogin.hmFriendList.get(mess.getReceiver());
                    fl.activeOnlineFriendIcon(mess);
                }
                if (mess.getMessageType().equals(MessageType.NEW_ONLINE_FRIEND)) {
                    String receiver = mess.getReceiver();
                    FriendList fl = (FriendList) ClientLogin.hmFriendList.get(receiver);
                    String sender = mess.getSender();
                    fl.activeNewOnlineFriendIcon(sender);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
