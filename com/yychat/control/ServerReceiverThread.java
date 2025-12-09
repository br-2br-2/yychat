package com.yychat.control;

import com.yychat.model.Message;
import com.yychat.model.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

public class ServerReceiverThread extends Thread {
    Socket s;

    public ServerReceiverThread(Socket s) {
        this.s = s;
    }

    public void run() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                Message mess = (Message) ois.readObject();
                if (mess.getMessageType().equals(MessageType.USER_EXIT_SERVER_THREAD_CLOSE)) {
                    String sender = mess.getSender();
                    mess.setMessageType(MessageType.USER_EXIT_CLIENT_THREAD_CLOSE);
                    sendMessage(s, mess);
                    System.out.println(sender + "用户退出了！正在关闭其服务线程");
                    s.close();
                    break;
                }
                if (mess.getMessageType().equals(MessageType.ADD_NEW_FRIEND)) {
                    String sender = mess.getSender();
                    String newFriend = mess.getContent();
                    if (DBUtil.seekUser(newFriend)) {
                        if (DBUtil.seekFriend(sender, newFriend, 1)) {
                            mess.setMessageType(MessageType.ADD_NEW_FRIEND_FALLURE_ALREADY_FRIEND);
                        } else {
                            DBUtil.insertIntoUser(sender, newFriend, 1);
                            String allFriend = DBUtil.seekAllUser(sender, 1);
                            mess.setContent(allFriend);
                            mess.setMessageType(MessageType.ADD_NEW_FRIEND_SUCCESS);
                        }
                    } else {
                        mess.setMessageType(MessageType.ADD_NEW_FRIEND_FALLURE_NO_USER);
                    }
                    Socket ss = (Socket) YychatServer.hmSocket.get(sender);
                    sendMessage(ss, mess);
                }

                if (mess.getMessageType().equals(MessageType.COMMON_CHAT_MESSAGE)) {
                    System.out.println(mess.getSender() + "对" +
                            mess.getReceiver() + "说：" + mess.getContent());
                    mess.setSendTime(new java.util.Date());
                    DBUtil.saveMessage(mess);
                    String receiver = mess.getReceiver();
                    Socket rs = YychatServer.hmSocket.get(receiver);
                    System.out.println("接收方 " + receiver + " 的Socket 的对象：" + rs);

                    if (rs != null) {
                        ObjectOutputStream oos = new ObjectOutputStream(rs.getOutputStream());
                        oos.writeObject(mess);
                    } else
                        System.out.println(receiver + "不在线");
                }
                if (mess.getMessageType().equals(MessageType.REQUEST_ONLINE_FRIEND)) {
                    Set<String> onlineFriendSet = YychatServer.hmSocket.keySet();
                    Iterator<String> it = onlineFriendSet.iterator();
                    String onlineFriend = "";
                    while (it.hasNext()) {
                        onlineFriend = " " + (String) it.next() + onlineFriend;
                    }
                    mess.setReceiver(mess.getSender());
                    mess.setSender("Server");
                    mess.setMessageType(MessageType.RESPONSE_ONLINE_FRIEND);
                    mess.setContent(onlineFriend);
                    sendMessage(s, mess);
                }
                if (mess.getMessageType().equals(MessageType.NEW_ONLINE_TO_ALL_FRIEND)) {
                    mess.setMessageType(MessageType.NEW_ONLINE_FRIEND);
                    Set<String> onlineFriendSet = YychatServer.hmSocket.keySet();
                    Iterator<String> it = onlineFriendSet.iterator();
                    while (it.hasNext()) {
                        String receiver = (String) it.next();
                        mess.setReceiver(receiver);
                        Socket rs = (Socket) YychatServer.hmSocket.get(receiver);
                        sendMessage(rs, mess);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendMessage(Socket s, Message mess) {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(mess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
