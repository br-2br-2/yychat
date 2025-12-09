package com.yychat.view;

import com.yychat.control.YychatClientConnection;
import com.yychat.model.Message;
import com.yychat.model.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class GroupChat extends JFrame implements ActionListener {
    JTextArea jta;
    JScrollPane jsp;
    JTextField jtf;
    JButton jb;
    String sender;
    String groupId;
    String groupName;

    public GroupChat(String sender, String groupId, String groupName) {
        this.sender = sender;
        this.groupId = groupId;
        this.groupName = groupName;

        jta = new JTextArea();//多行文本框
        jta.setForeground(Color.red);
        jsp = new JScrollPane(jta);
        this.add(jsp, "Center");

        jtf = new JTextField(15);//单行文本框
        jb = new JButton("发送");
        jb.addActionListener(this);

        jb.setForeground(Color.blue);
        JPanel jp = new JPanel();
        jp.add(jtf);
        jp.add(jb);
        this.add(jp, "South");

        this.setSize(400, 300);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.setTitle("群聊 - " + groupName + " (" + groupId + ")");

        this.setIconImage(new ImageIcon("resources/duck2.gif").getImage());
        this.setVisible(true);
    }

    public static void main(String[] args) {
        //GroupChat gc = new GroupChat();
    }

    public void append(Message mess) {
        jta.append(mess.getSendTime().toString() + "\r\n" +
                mess.getSender() + " 在群聊中说：" + mess.getContent() + "\r\n");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb)
            jta.append(sender + ": " + jtf.getText() + "\r\n");

        Message mess = new Message();
        mess.setSender(sender);
        mess.setReceiver("Server"); // 群聊消息发给服务器进行转发
        mess.setContent(jtf.getText());
        mess.setMessageType(MessageType.GROUP_CHAT_MESSAGE);
        mess.setGroupId(groupId);
        mess.setGroupName(groupName);

        try {
            OutputStream os = YychatClientConnection.s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mess);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        jtf.setText(""); // 清空输入框
    }
}