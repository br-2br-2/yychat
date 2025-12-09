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

public class FriendChat extends JFrame implements ActionListener {
    JTextArea jta;
    JScrollPane jsp;
    JTextField jtf;
    JButton jb;
    String sender;
    String receiver;

    public FriendChat(String sender, String receiver) {
        this.sender=sender;
        this.receiver=receiver;

        jta = new JTextArea();//多行本文框
        jta.setForeground(Color.red);
        jsp = new JScrollPane(jta);
        this.add(jsp, "Center");

        jtf = new JTextField(15);//单行文本框
        jb = new JButton("发送");
        jb.addActionListener(this);

        jb.setForeground(Color.blue);
        JPanel jp = new JPanel();
        jp.add(jtf);jp.add(jb);
        this.add(jp, "South");

        this.setSize(350, 250);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.setTitle(sender + "to" + receiver + "的聊天界面");

        this.setIconImage(new ImageIcon("resources/duck2.gif").getImage());
        this.setVisible(true);
    }

    public static void main(String[] args) {
        //FriendChat fc=new FriendChat();
    }

    public void append(Message mess) {
        jta.append(mess.getSendTime().toString()+"\r\n"+
                mess.getSender() + "对你说：" + mess.getContent() + "\r\n");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb)
            jta.append(jtf.getText() + "\r\n");

        Message mess = new Message();
        mess.setSender(sender);
        mess.setReceiver(receiver);
        System.out.println("sender: "+sender + " receiver: " + receiver);
        mess.setContent(jtf.getText());
        mess.setMessageType(MessageType.COMMON_CHAT_MESSAGE);

        try {
            OutputStream os = YychatClientConnection.s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(mess);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
