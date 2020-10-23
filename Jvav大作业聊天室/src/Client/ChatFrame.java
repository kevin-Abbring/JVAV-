package Client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Server.User;
import Server.MessageConfigure;
import Server.Message;

public class ChatFrame extends JFrame implements ActionListener, Runnable {
    //定义各控件
    private JLabel lbUser = new JLabel("在线好友：");
    private List lstUser = new List(1, true);
    private JLabel lbMsg = new JLabel("聊天记录：");
    private JTextArea taMsg = new JTextArea();
    private JScrollPane spMsg = new JScrollPane(taMsg);
    private JTextField tfMsg = new JTextField();
    private JButton btSend = new JButton("发送");
    private JPanel plUser = new JPanel(new BorderLayout());
    private JPanel plMsg = new JPanel(new BorderLayout());
    private JPanel plUser_Msg = new JPanel(new GridLayout(1, 2));
    private JPanel plSend = new JPanel(new BorderLayout());
    //设置对象输入输出流
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    //设置是否正常运行的标志
    private boolean canRun = true;
    //账号存储
    private String name;
    private String account;

    public ChatFrame(ObjectInputStream IPS, ObjectOutputStream OPS, Message message, String name, String account) {
        this.ois = IPS;
        this.oos = OPS;
        this.name = name;
        this.account = account;
        this.initFrame();
        this.initUserList(message);
        new Thread(this).start();
    }

    //界面初始化和增加监听
    public void initFrame() {
        //界面初始化
        this.setTitle(" 当 前 在 线 :" + name);
        plUser.add(lbUser, BorderLayout.NORTH);
        plUser.add(lstUser, BorderLayout.CENTER);
        plUser_Msg.add(plUser);
        plMsg.add(lbMsg, BorderLayout.NORTH);
        plMsg.add(spMsg, BorderLayout.CENTER);
        plUser_Msg.add(plMsg);
        plSend.add(tfMsg, BorderLayout.CENTER);
        plSend.add(btSend, BorderLayout.EAST);
        this.add(plUser_Msg, BorderLayout.CENTER);
        this.add(plSend, BorderLayout.SOUTH);
        btSend.addActionListener(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 400);
        taMsg.setEditable(false);
        this.setLocation(800,400);
        this.setVisible(true);
    }

    //用户队列
    public void initUserList(Message message) {
        //将用户队列清空
        lstUser.removeAll();
        //加入全部人这个选项
        lstUser.add(MessageConfigure.ALL);
        //默认选第一个（所有人）
        lstUser.select(0);
        //建立好友列表，可自动增长的列表数组，message 包含了全部的好友列表
        Vector<User> userListVector = (Vector<User>) message.getContent();
        for (User cus : userListVector) {
            lstUser.add(cus.getName() + "(" + cus.getAccount() + ")");
        }

    }
    @Override
    public void run() {
        try {
            while (canRun) {
                Message msg = (Message) ois.readObject();
                if (msg.getType().equals(MessageConfigure.MESSAGE)) {
                    //在ChatFrame 的taMsg 内添加内容
                    taMsg.append(msg.getContent() + "\n");
                } else if (msg.getType().equals(MessageConfigure.USERLIST)) {
                    this.initUserList(msg);
                } else if (msg.getType().equals(MessageConfigure.LOGOUT)) {
                    this.initUserList(msg);
                }
            }
        } catch (Exception ex) {
            canRun = false;
            JOptionPane.showMessageDialog(this, "服务器关闭，自动退出");
            System.exit(-1);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        try {
            Message msg = new Message();
            msg.setType(MessageConfigure.MESSAGE);
            msg.setContent(name + "：" + tfMsg.getText());
            msg.setFrom(account);
            String[] toInfo = lstUser.getSelectedItems();
            String to = "";
            //如果是群发
            if (toInfo[0].split("\\(")[0].equals(MessageConfigure.ALL)) {
            } else {
                to = toInfo[0].split("\\(")[0];
                for (int i = 0; i < toInfo.length; i++) {
                    to += toInfo[i].split("\\(")[0] + ",";
                }
                to += name;
            }
            msg.setTo(to);
            oos.writeObject(msg);
            tfMsg.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "消息发送失败");
        }
    }
}

