package com.yychat.view;

import com.yychat.control.YychatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartServer extends JFrame implements ActionListener {
    JButton jb1, jb2;

    public StartServer() {
        jb1 = new JButton("启动服务器");

        jb1.addActionListener(this);

        jb1.setFont(new Font("宋体", Font.BOLD, 25));
        jb2 = new JButton("停止服务器");
        jb2.setFont(new Font("宋体", Font.BOLD, 25));
        this.setLayout(new GridLayout(1, 2));
        this.add(jb1);
        this.add(jb2);
        this.setSize(400, 100);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon("resources/duck2.gif").getImage());
        this.setTitle("Yychat 服务器");
        this.setVisible(true);
    }

    public static void main(String[] args) {
        StartServer ss = new StartServer();
    }

    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == jb1)
            new YychatServer();
    }
}



