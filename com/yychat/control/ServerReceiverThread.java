package com.yychat.control;

import com.yychat.model.Message;
import com.yychat.model.MessageType;
import com.yychat.model.GroupChat;

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
                
                // 处理创建群聊请求
                if (mess.getMessageType().equals(MessageType.CREATE_GROUP_CHAT)) {
                    String groupId = mess.getGroupId();
                    String groupName = mess.getGroupName();
                    String creator = mess.getSender();
                    
                    GroupChat group = GroupChatManager.createGroup(groupId, groupName, creator);
                    if (group != null) {
                        mess.setContent("群聊创建成功: " + groupName);
                        mess.setMessageType(MessageType.CREATE_GROUP_CHAT);
                    } else {
                        mess.setContent("群聊创建失败，群聊ID已存在");
                        mess.setMessageType(MessageType.CREATE_GROUP_CHAT);
                    }
                    sendMessage(s, mess);
                }
                
                // 处理群聊消息
                if (mess.getMessageType().equals(MessageType.GROUP_CHAT_MESSAGE)) {
                    String groupId = mess.getGroupId();
                    if (GroupChatManager.isUserInGroup(mess.getSender(), groupId)) {
                        // 向群聊所有成员发送消息
                        for (String member : GroupChatManager.getGroupMembers(groupId)) {
                            if (!member.equals(mess.getSender())) { // 不发送给发送者自己
                                Socket memberSocket = YychatServer.hmSocket.get(member);
                                if (memberSocket != null) {
                                    sendMessage(memberSocket, mess);
                                }
                            }
                        }
                    }
                }
                
                // 处理删除好友请求
                if (mess.getMessageType().equals(MessageType.DELETE_FRIEND)) {
                    String sender = mess.getSender();
                    String friendToDelete = mess.getContent(); // 要删除的好友用户名
                    
                    // 从数据库中删除好友关系
                    boolean success = DBUtil.deleteFriend(sender, friendToDelete);
                    if (success) {
                        // 更新发送者的好友列表
                        String allFriend = DBUtil.seekAllUser(sender, 1);
                        mess.setContent(allFriend);
                        mess.setMessageType(MessageType.DELETE_FRIEND);
                        sendMessage(s, mess);
                    }
                }
                
                // 处理拉黑好友请求
                if (mess.getMessageType().equals(MessageType.BLACKLIST_FRIEND)) {
                    String sender = mess.getSender();
                    String friendToBlacklist = mess.getContent(); // 要拉黑的好友用户名
                    
                    // 将好友关系从正常好友变为黑名单
                    boolean success = DBUtil.moveToBlacklist(sender, friendToBlacklist);
                    if (success) {
                        // 更新发送者的好友列表
                        String allFriend = DBUtil.seekAllUser(sender, 1);
                        mess.setContent(allFriend);
                        mess.setMessageType(MessageType.BLACKLIST_FRIEND);
                        sendMessage(s, mess);
                    }
                }
                
                // 处理文件传输请求
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_REQUEST)) {
                    // 接收方在线则转发文件传输请求
                    String receiver = mess.getReceiver();
                    Socket receiverSocket = YychatServer.hmSocket.get(receiver);
                    if (receiverSocket != null) {
                        // 转发文件传输请求给接收方
                        sendMessage(receiverSocket, mess);
                    } else {
                        // 接收方不在线，发送失败消息
                        mess.setMessageType(MessageType.FILE_TRANSFER_RESPONSE);
                        mess.setContent("接收方不在线");
                        sendMessage(s, mess);
                    }
                }
                
                // 处理文件传输响应（同意/拒绝）
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_RESPONSE)) {
                    // 将响应转发给发送方
                    String sender = mess.getSender();
                    Socket senderSocket = YychatServer.hmSocket.get(sender);
                    if (senderSocket != null) {
                        sendMessage(senderSocket, mess);
                    }
                }
                
                // 处理文件数据传输
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_DATA)) {
                    // 将文件数据转发给接收方
                    String receiver = mess.getReceiver();
                    Socket receiverSocket = YychatServer.hmSocket.get(receiver);
                    if (receiverSocket != null) {
                        sendMessage(receiverSocket, mess);
                    }
                }
                
                // 处理文件传输完成
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_COMPLETE)) {
                    // 通知双方传输完成
                    String sender = mess.getSender();
                    String receiver = mess.getReceiver();
                    
                    Socket senderSocket = YychatServer.hmSocket.get(sender);
                    Socket receiverSocket = YychatServer.hmSocket.get(receiver);
                    
                    if (senderSocket != null) {
                        sendMessage(senderSocket, mess);
                    }
                    if (receiverSocket != null) {
                        sendMessage(receiverSocket, mess);
                    }
                }
                
                // 处理用户状态更新
                if (mess.getMessageType().equals(MessageType.USER_STATUS_UPDATE)) {
                    // 广播用户状态给所有在线好友
                    String sender = mess.getSender();
                    Set<String> onlineFriendSet = YychatServer.hmSocket.keySet();
                    Iterator<String> it = onlineFriendSet.iterator();
                    
                    // 创建状态更新消息
                    Message statusMessage = new Message();
                    statusMessage.setMessageType(MessageType.USER_STATUS_UPDATE);
                    statusMessage.setSender(sender);
                    statusMessage.setUserStatus(mess.getUserStatus());
                    
                    while (it.hasNext()) {
                        String friend = (String) it.next();
                        if (!friend.equals(sender)) { // 不发送给自己
                            statusMessage.setReceiver(friend);
                            Socket friendSocket = YychatServer.hmSocket.get(friend);
                            if (friendSocket != null) {
                                sendMessage(friendSocket, statusMessage);
                            }
                        }
                    }
                }
                
                // 处理查询用户状态请求
                if (mess.getMessageType().equals(MessageType.REQUEST_USER_STATUS)) {
                    // 检查目标用户是否在线
                    String targetUser = mess.getTargetUser();
                    Socket targetSocket = YychatServer.hmSocket.get(targetUser);
                    
                    // 创建响应消息
                    mess.setMessageType(MessageType.RESPONSE_USER_STATUS);
                    if (targetSocket != null) {
                        mess.setUserStatus("在线");
                    } else {
                        mess.setUserStatus("离线");
                    }
                    mess.setReceiver(mess.getSender()); // 发送回请求方
                    sendMessage(s, mess);
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
