package com.yychat.view;

import com.yychat.control.YychatClientConnection;
import com.yychat.model.Message;
import com.yychat.model.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class FriendList extends JFrame implements ActionListener, MouseListener, WindowListener {
    public static HashMap<String, FriendChat> hmFriendChat = new HashMap<String, FriendChat>();
    final int MYFRIENDCOUNT = 50;
    final int STRANGERCOUNT = 20;
    JPanel friendPanel;
    JButton myFriendButton1;
    JButton myStrangerButton1;
    JButton blackListButton1;
    // 实验五
    JScrollPane friendListScrollPane;
    JPanel friendListPanel;
    JLabel[] friendLabel = new JLabel[MYFRIENDCOUNT];
    JPanel strangerPanel;
    JButton myFriendButton2;
    JButton myStrangerButton2;
    JButton blackListButton2;
    // 新增群聊相关组件
    JButton createGroupButton;
    JButton manageGroupsButton;
    CardLayout c1;
    String name;
    JPanel addFriendJPanel;
    JButton addFriendButton;
    
    // 声明陌生人列表相关组件
    JPanel strangerListPanel;
    JScrollPane strangerListScrollPane;
    JLabel[] strangerLabel = new JLabel[STRANGERCOUNT];

    public FriendList(String name, String allFriend) {
        this.name = name;
        //创建卡片一
        friendPanel = new JPanel(new BorderLayout());
        addFriendJPanel = new JPanel(new GridLayout(4, 1)); // 修改为4行布局
        addFriendButton = new JButton("添加好友");
        addFriendButton.addActionListener(this);
        myFriendButton1 = new JButton("我的好友");
        createGroupButton = new JButton("创建群聊"); // 添加创建群聊按钮
        createGroupButton.addActionListener(this);
        manageGroupsButton = new JButton("管理群聊"); // 添加管理群聊按钮
        manageGroupsButton.addActionListener(this);
        addFriendJPanel.add(addFriendButton);
        addFriendJPanel.add(myFriendButton1);
        addFriendJPanel.add(createGroupButton); // 添加到面板
        addFriendJPanel.add(manageGroupsButton); // 添加到面板
        friendPanel.add(addFriendJPanel, "North");

        friendListPanel = new JPanel();
        showAllFriend(allFriend);

//        String[]myFriend=allFriend.split(" ");
//        friendListPanel=new JPanel(new GridLayout(myFriend.length-1,1));
//        for (int i = 1; i < myFriend.length; i++) {
//            String imageStr = "resources/" + i % 6 + ".jpg";
//            ImageIcon imageIcon = new ImageIcon(imageStr);
//            friendLabel[i] = new JLabel(myFriend[i]+"" , imageIcon, JLabel.LEFT);
//            friendListPanel.add(friendLabel[i]);
//        }

//        friendListPanel = new JPanel(new GridLayout(MYFRIENDCOUNT, 1));
//        for (int i = 0; i < friendLabel.length; i++) {
//            //String imageStr = "resources/" + (int) (Math.random() * 6) + ".jpg";
//            //if (i != Integer.parseInt(name))
//                friendLabel[i].setEnabled(false);
//            friendLabel[i].addMouseListener(this);//实验九
//        }
        friendListScrollPane = new JScrollPane(friendListPanel);
        friendPanel.add(friendListScrollPane, "Center");

        myStrangerButton1 = new JButton("陌生人");
        myStrangerButton1.addActionListener(this);

        blackListButton1 = new JButton("黑名单");
        JPanel stranger_BlackPanel = new JPanel(new GridLayout(2, 1));
        stranger_BlackPanel.add(myStrangerButton1);
        stranger_BlackPanel.add(blackListButton1);
        friendPanel.add(stranger_BlackPanel, "South");
        //创建卡片二
        strangerPanel = new JPanel(new BorderLayout());
        myFriendButton2 = new JButton("我的好友");

        myFriendButton2.addActionListener(this);

        myStrangerButton2 = new JButton("陌生人");
        JPanel friend_strangerPanel = new JPanel(new GridLayout(2, 1));
        friend_strangerPanel.add(myFriendButton2);
        friend_strangerPanel.add(myStrangerButton2);
        strangerPanel.add(friend_strangerPanel, "North");

        strangerListPanel = new JPanel(new GridLayout(STRANGERCOUNT, 1));
        for (int i = 0; i < strangerLabel.length; i++) {
            strangerLabel[i] = new JLabel(i + "号陌生人", new ImageIcon("resources/tortoise.gif"), JLabel.LEFT);
            strangerListPanel.add(strangerLabel[i]);
        }
        strangerListScrollPane = new JScrollPane(strangerListPanel);
        strangerPanel.add(strangerListScrollPane, "Center");

        blackListButton2 = new JButton("黑名单");
        strangerPanel.add(blackListButton2, "South");

        c1 = new CardLayout();//创建卡片布局
        this.setLayout(c1);//窗体设置为卡片布局
        this.add(friendPanel, "card1");
        this.add(strangerPanel, "card2");

        this.setIconImage(new ImageIcon("resources/duck2.gif").getImage());
        this.setTitle(name + "好友列表");
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(this);
        this.setBounds(600, 300, 350, 350);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        // FriendList f1 = new FriendList();
    }

    public void showAllFriend(String allFriend) {
        String[] myFriend = allFriend.split(" ");
        friendListPanel.removeAll();
        friendListPanel.setLayout(new GridLayout(myFriend.length - 1, 1));
        for (int i = 1; i < myFriend.length; i++) {
            String imageStr = "resources/" + i % 6 + ".jpg";
            ImageIcon imageIcon = new ImageIcon(imageStr);
            friendLabel[i] = new JLabel(myFriend[i] + "", imageIcon, JLabel.LEFT);
            friendLabel[i].addMouseListener(this);
            friendListPanel.add(friendLabel[i]);
        }
        friendListPanel.revalidate();
    }

    public void activeNewOnlineFriendIcon(String newOnlineFriend) {
        //this.friendLabel[Integer.parseInt(newOnlineFriend)].setEnabled(true);
    }

    public void activeOnlineFriendIcon(Message mess) {
        String onlineFriend = mess.getContent();
        String[] onlineFriendName = onlineFriend.split("\\s+");
        for (String s : onlineFriendName) {
            if (s.isEmpty()) continue;
            //this.friendLabel[Integer.parseInt(s)].setEnabled(true);
        }
    }
    
    /**
     * 更新指定好友的在线状态
     * @param friendName 好友用户名
     * @param status 状态（在线/离线/忙碌等）
     */
    public void updateUserStatus(String friendName, String status) {
        // 遍历好友列表，找到对应的好友并更新其状态显示
        for (int i = 0; i < friendLabel.length; i++) {
            if (friendLabel[i] != null && friendLabel[i].getText() != null && 
                friendLabel[i].getText().equals(friendName)) {
                // 根据状态更新标签的显示
                if ("在线".equals(status)) {
                    // 在好友名称旁边添加在线指示器
                    friendLabel[i].setText(friendName + " [在线]");
                    // 可以设置不同的颜色或图标来表示在线状态
                    friendLabel[i].setForeground(Color.GREEN);
                } else if ("离线".equals(status)) {
                    // 移除在线指示器
                    friendLabel[i].setText(friendName);
                    friendLabel[i].setForeground(Color.BLACK);
                } else {
                    // 其他状态如"忙碌"、"离开"等
                    friendLabel[i].setText(friendName + " [" + status + "]");
                    friendLabel[i].setForeground(Color.ORANGE);
                }
                break;
            }
        }
    }

    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == addFriendButton) {
            String newFriend = JOptionPane.showInputDialog("请输入新好友的名字：");
            System.out.println("newFriend:" + newFriend);
            if (newFriend != null) {
                Message mess = new Message();
                mess.setSender(name);
                mess.setReceiver("Server");
                mess.setContent(newFriend);
                mess.setMessageType(MessageType.ADD_NEW_FRIEND);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
                    oos.writeObject(mess);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // 创建群聊功能
        if (arg0.getSource() == createGroupButton) {
            String groupId = JOptionPane.showInputDialog("请输入群聊ID：");
            if (groupId != null && !groupId.trim().isEmpty()) {
                String groupName = JOptionPane.showInputDialog("请输入群聊名称：");
                if (groupName != null && !groupName.trim().isEmpty()) {
                    Message mess = new Message();
                    mess.setSender(name);
                    mess.setReceiver("Server");
                    mess.setGroupId(groupId);
                    mess.setGroupName(groupName);
                    mess.setMessageType(MessageType.CREATE_GROUP_CHAT);
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
                        oos.writeObject(mess);
                        JOptionPane.showMessageDialog(this, "群聊创建请求已发送");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        // 管理群聊功能
        if (arg0.getSource() == manageGroupsButton) {
            // 这里可以打开群聊管理界面
            JOptionPane.showMessageDialog(this, "群聊管理功能待实现");
        }
        
        if (arg0.getSource() == myFriendButton2)
            c1.show(this.getContentPane(), "card1");
        if (arg0.getSource() == myStrangerButton1)
            c1.show(this.getContentPane(), "card2");
    }

    public void mouseClicked(MouseEvent arg0) {
        if (arg0.getClickCount() == 2) {
            JLabel j1 = (JLabel) arg0.getSource();
            String toName = j1.getText();
            FriendChat fc = new FriendChat(name, toName);
            hmFriendChat.put(name + "to" + toName, fc);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            processRightClick(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            processRightClick(e);
        }
    }

    private void processRightClick(MouseEvent e) {
        JLabel clickedLabel = (JLabel) e.getSource();
        String friendName = clickedLabel.getText();
        
        // 创建右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        
        // 删除好友菜单项
        JMenuItem deleteFriendItem = new JMenuItem("删除好友");
        deleteFriendItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    FriendList.this,
                    "确定要删除好友 " + friendName + " 吗？",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    Message mess = new Message();
                    mess.setSender(name);
                    mess.setReceiver("Server");
                    mess.setContent(friendName);
                    mess.setMessageType(MessageType.DELETE_FRIEND);
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
                        oos.writeObject(mess);
                        JOptionPane.showMessageDialog(FriendList.this, "已删除好友 " + friendName);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        // 拉黑好友菜单项
        JMenuItem blacklistFriendItem = new JMenuItem("拉黑好友");
        blacklistFriendItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    FriendList.this,
                    "确定要将 " + friendName + " 拉入黑名单吗？",
                    "确认拉黑",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    Message mess = new Message();
                    mess.setSender(name);
                    mess.setReceiver("Server");
                    mess.setContent(friendName);
                    mess.setMessageType(MessageType.BLACKLIST_FRIEND);
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
                        oos.writeObject(mess);
                        JOptionPane.showMessageDialog(FriendList.this, "已将 " + friendName + " 拉入黑名单");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        popupMenu.add(deleteFriendItem);
        popupMenu.add(blacklistFriendItem);
        
        // 查询好友状态菜单项
        JMenuItem queryStatusItem = new JMenuItem("查询状态");
        queryStatusItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建查询状态消息
                Message mess = new Message();
                mess.setSender(name); // 当前用户
                mess.setTargetUser(friendName); // 目标用户
                mess.setMessageType(MessageType.REQUEST_USER_STATUS);
                
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
                    oos.writeObject(mess);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        popupMenu.add(queryStatusItem);
        
        popupMenu.show(clickedLabel, e.getX(), e.getY());
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println(name + "准备关闭客户端... ");
        Message mess = new Message();
        mess.setSender(name);
        mess.setReceiver("Server");
        mess.setMessageType(MessageType.USER_EXIT_SERVER_THREAD_CLOSE);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(YychatClientConnection.s.getOutputStream());
            oos.writeObject(mess);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
