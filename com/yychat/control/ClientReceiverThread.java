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
                
                // 处理群聊消息
                if (mess.getMessageType().equals(MessageType.GROUP_CHAT_MESSAGE)) {
                    // 这里需要实现群聊消息的处理逻辑
                    // 可以打开群聊窗口或更新现有群聊窗口
                    System.out.println("收到群聊消息: " + mess.getContent() + " from " + mess.getSender());
                }
                
                // 处理删除好友响应
                if (mess.getMessageType().equals(MessageType.DELETE_FRIEND)) {
                    String sender = mess.getSender();
                    FriendList fl = (FriendList) ClientLogin.hmFriendList.get(sender);
                    String allFriend = mess.getContent();
                    fl.showAllFriend(allFriend);
                    JOptionPane.showMessageDialog(null, "好友删除成功！");
                }
                
                // 处理拉黑好友响应
                if (mess.getMessageType().equals(MessageType.BLACKLIST_FRIEND)) {
                    String sender = mess.getSender();
                    FriendList fl = (FriendList) ClientLogin.hmFriendList.get(sender);
                    String allFriend = mess.getContent();
                    fl.showAllFriend(allFriend);
                    JOptionPane.showMessageDialog(null, "好友拉黑成功！");
                }
                
                // 处理创建群聊响应
                if (mess.getMessageType().equals(MessageType.CREATE_GROUP_CHAT)) {
                    JOptionPane.showMessageDialog(null, mess.getContent()); // 显示群聊创建结果
                }
                
                // 处理文件传输请求
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_REQUEST)) {
                    // 显示文件传输请求对话框，询问用户是否接收文件
                    String message = mess.getSender() + " 想要发送文件给您:\n" + 
                                   mess.getFileName() + " (" + mess.getFileSize() + " bytes)\n" + 
                                   "是否接收?";
                    int result = JOptionPane.showConfirmDialog(null, message, "文件传输请求", JOptionPane.YES_NO_OPTION);
                    
                    // 创建响应消息
                    Message response = new Message();
                    response.setSender(mess.getReceiver()); // 接收方回复
                    response.setReceiver(mess.getSender()); // 发送给原发送方
                    response.setTransferId(mess.getTransferId()); // 保持传输ID一致
                    
                    if (result == JOptionPane.YES_OPTION) {
                        // 用户同意接收文件
                        response.setMessageType(MessageType.FILE_TRANSFER_RESPONSE);
                        response.setContent("同意接收");
                    } else {
                        // 用户拒绝接收文件
                        response.setMessageType(MessageType.FILE_TRANSFER_RESPONSE);
                        response.setContent("拒绝接收");
                    }
                    
                    // 发送响应给服务器
                    YychatClientConnection.sendToServer(response);
                }
                
                // 处理文件传输响应（来自接收方的同意/拒绝）
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_RESPONSE)) {
                    // 根据响应更新发送方的UI状态
                    if (mess.getContent().equals("同意接收")) {
                        JOptionPane.showMessageDialog(null, mess.getReceiver() + " 同意接收文件");
                        // 这里可以启动文件传输过程
                    } else {
                        JOptionPane.showMessageDialog(null, mess.getReceiver() + " 拒绝接收文件");
                    }
                }
                
                // 处理文件数据传输
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_DATA)) {
                    // 接收文件数据并保存到本地
                    // 这里需要实现具体的文件保存逻辑
                    System.out.println("接收到文件数据块: " + mess.getChunkIndex() + "/" + mess.getTotalChunks() + 
                                     " 文件名: " + mess.getFileName());
                }
                
                // 处理文件传输完成
                if (mess.getMessageType().equals(MessageType.FILE_TRANSFER_COMPLETE)) {
                    // 显示传输完成消息
                    JOptionPane.showMessageDialog(null, "文件传输完成: " + mess.getFileName());
                }
                
                // 处理用户状态更新
                if (mess.getMessageType().equals(MessageType.USER_STATUS_UPDATE)) {
                    // 更新好友列表中的用户状态
                    String sender = mess.getSender(); // 状态变化的用户
                    String userStatus = mess.getUserStatus(); // 新状态
                    String receiver = mess.getReceiver(); // 当前客户端用户
                    
                    // 获取当前用户的FriendList实例
                    FriendList fl = (FriendList) ClientLogin.hmFriendList.get(receiver);
                    if (fl != null) {
                        // 更新好友列表中对应用户的状态显示
                        fl.updateUserStatus(sender, userStatus);
                    }
                }
                
                // 处理查询用户状态的响应
                if (mess.getMessageType().equals(MessageType.RESPONSE_USER_STATUS)) {
                    // 显示查询到的用户状态
                    String targetUser = mess.getTargetUser();
                    String userStatus = mess.getUserStatus();
                    JOptionPane.showMessageDialog(null, targetUser + " 的状态: " + userStatus);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
