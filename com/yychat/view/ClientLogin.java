package com.yychat.view;

import com.yychat.control.YychatClientConnection;
import com.yychat.model.Message;
import com.yychat.model.MessageType;
import com.yychat.model.User;
import com.yychat.model.UserType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ClientLogin extends JFrame implements ActionListener {
    public static HashMap<String, FriendList> hmFriendList = new HashMap<String, FriendList>();
    JLabel jl;
    JButton jb1, jb2, jb3;
    JPanel jp;
    JLabel jl1, jl2, jl3, jl4;
    JTextField jtf;
    JPasswordField jpf;
    JButton jb4;
    JCheckBox jc1, jc2;
    JPanel jp1, jp2, jp3;
    JTabbedPane jtp;

    public ClientLogin() {
        // j1=new JLabel("                                    JAVA 教学聊天室");
        jl = new JLabel(new ImageIcon("resources/head.gif"));
        //建立JLabel对象jl，初始化为head.gif图片
        this.add(jl, "North");//将此图片添加到窗口北部

        //创建登录界面中间部分的组件
        jl1 = new JLabel("YY号码: ", JLabel.CENTER);
        jl2 = new JLabel("YY密码: ", JLabel.CENTER);
        jl3 = new JLabel("忘记密码", JLabel.CENTER);
        jl3.setForeground(Color.blue);//设置字体颜色
        jl4 = new JLabel("申请密码保护", JLabel.CENTER);
        jb4 = new JButton(new ImageIcon("resources/clear.gif"));
        jtf = new JTextField();
        jpf = new JPasswordField();
        jc1 = new JCheckBox("隐身登录");
        jc2 = new JCheckBox("记住密码");
        jp1 = new JPanel(new GridLayout(3, 3));//设置网格布局模式
        jp1.add(jl1);
        jp1.add(jtf);
        jp1.add(jb4);
        jp1.add(jl2);
        jp1.add(jpf);
        jp1.add(jl3);
        jp1.add(jc1);
        jp1.add(jc2);
        jp1.add(jl4);

        jtp = new JTabbedPane();//创建选项卡面板
        jtp.add(jp1, "YY号码");//在选项卡中添加3个JPanel
        jp2 = new JPanel();
        jp3 = new JPanel();
        jtp.add(jp2, "手机号码");
        jtp.add(jp3, "电子邮箱");
        this.add(jtp, "Center");//选项卡面板添加到窗体中部

        jb1 = new JButton(new ImageIcon("resources/login.gif"));
        jb1.addActionListener(this);//实验七

        jb2 = new JButton(new ImageIcon("resources/register.gif"));
        jb2.addActionListener(this);

        jb3 = new JButton(new ImageIcon("resources/cancel.gif"));
        jp = new JPanel();
        jp.add(jb1);
        jp.add(jb2);
        jp.add(jb3);//创建面板，并将3个按钮加入到面板中
        this.add(jp, "South");
        //this.setBounds(800,600,350,250);//设置窗口在屏幕上的位置及大小
        Image im = new ImageIcon("resources/duck2.gif").getImage();
        this.setIconImage(im);

        this.setLocationRelativeTo(null);
        this.setSize(350, 250);//设置窗口的大小
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("YY聊天");//设置窗口标题
        this.setVisible(true);//使窗口可视化
    }

    public static void main(String[] args) {
        ClientLogin c1 = new ClientLogin();
    }

    public  void actionPerformed(ActionEvent arg0) {
        String name = jtf.getText();
        String password = new String(jpf.getPassword());
        User user = new User();
        user.setUserName(name);
        user.setPassWord(password);
        if (arg0.getSource() == jb2) {
            user.setUserType(UserType.USER_REGISTER);
            if (new YychatClientConnection().registerUser(user)) {
                JOptionPane.showMessageDialog(this, name + "用户注册成功了！");
            } else {
                JOptionPane.showMessageDialog(this, name + "用户名重复了，请重试！");
            }
        }
        if (arg0.getSource() == jb1) {
            user.setUserType(UserType.USER_LOGIN_VALLDATE);
            Message mess = new YychatClientConnection().loginValidate1(user);
            if (mess.getMessageType().equals(MessageType.LOGIN_VALLDATE_SUCCESS)) {
                String allFriend=mess.getContent();
                FriendList fl=new FriendList(name,allFriend);
                hmFriendList.put(name,fl);
                this.dispose();
            }else {
                JOptionPane.showMessageDialog(this,"密码错误，请重试！");
            }
////            String name = jtf.getText();
////            String password = new String(jpf.getPassword());
////            User user = new User();
////            user.setUserName(name);
////            user.setPassWord(password);
//            if (new YychatClientConnection().loginValidate(user)) {
//                hmFriendList.put(name, new FriendList(name));
//                System.out.println("客户端登录成功");
//                Message mess = new Message();
//                mess.setSender(name);
//                mess.setReceiver("Server");
//                mess.setMessageType(MessageType.REQUEST_ONLINE_FRIEND);
//                sendMessage(YychatClientConnection.s, mess);
//                mess.setMessageType(MessageType.NEW_ONLINE_TO_ALL_FRIEND);
//                sendMessage(YychatClientConnection.s, mess);
//                this.dispose();
//            } else {
//                JOptionPane.showMessageDialog(this, "密码错误，请重试！！！");
//            }
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
