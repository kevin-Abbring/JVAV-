package Server;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Vector;
import javax.swing.*;

public class Server extends JFrame implements Runnable, ActionListener {
    //定义各控件
    private JLabel lbUser = new JLabel("在线：");
    private List lstUser = new List(0, true);
    private JTextField tfMsg = new JTextField();
    private JButton btRemove = new JButton("踢人");
    private JButton btSend = new JButton("发送");
    private JButton btClose = new JButton("关闭服务器");
    private JPanel plSend = new JPanel(new BorderLayout());
    private JPanel plUser_Msg = new JPanel(new BorderLayout());
    private JPanel plServer = new JPanel(new GridLayout(1, 2));

    private Socket socket = null;
    //服务端接受连接
    private ServerSocket serverSocket = null;
    //保存客户端线程
    private Vector<ChatThread> clients = new Vector<ChatThread>();
    private Vector<User> userList = new Vector<User>();
    private boolean canRun = true;

    public Server() throws Exception {
        this.setTitle("服务器");
        //界面初始化
        this.initFrame();
        //服务器打开端口接受连接
        serverSocket = new ServerSocket(8866);
        //接收客户连接的循环
        new Thread(this).start();
    }

    //界面初始化和增加监听
    public void initFrame() {
        //界面初始化
        plUser_Msg.add(lbUser, BorderLayout.NORTH);
        plUser_Msg.add(lstUser, BorderLayout.CENTER);
        plUser_Msg.add(btRemove, BorderLayout.SOUTH);
        plSend.add(tfMsg, BorderLayout.CENTER);
        plSend.add(btSend, BorderLayout.SOUTH);
        plServer.add(plUser_Msg);
        plServer.add(plSend);
        this.add(plServer, BorderLayout.CENTER);
        this.add(btClose, BorderLayout.SOUTH);
        //增加监听
        btRemove.addActionListener(this);
        btSend.addActionListener(this);
        btClose.addActionListener(this);
        this.setSize(300, 200);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

        public void sendMessage(){
            Message msg = new Message();
//设置消息的类型是Message
            msg.setType(MessageConfigure.MESSAGE);
//消息的内容是用户对象
            msg.setContent("服务器：" + tfMsg.getText());
            try {
                for (ChatThread ct : getClients()) {
                    ct.oos.writeObject(msg);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            tfMsg.setText("");
        }

        //更新用户队列
        public void initUserList(){
            lstUser.removeAll();
            for (User cus : userList) {
                lstUser.add(cus.getName() + "(" + cus.getAccount() + ")");
            }
        }

        //将用户踢出
        public void removeUser () {
            String[] reInfo = lstUser.getSelectedItems();
            for (int i = 0; i < reInfo.length; i++) {
            //移除个人信息和线程
                int j = 0;
                for (; j < clients.size(); j++) {
                    if (clients.get(j).customer.getName().equals(reInfo[i].split("\\(")[0]))
                    { break; }
                }
                clients.get(j).handleLgout();
            }
        }

        public Vector<ChatThread> getClients () { return clients; }

        public Vector<User> getUserList () { return userList; }

        public void run () {
            // TODO Auto-generated method stub
            try {
                while (canRun) {
                    socket = serverSocket.accept();
                    ChatThread ct = new ChatThread(socket, this);
                    //线程开始
                    ct.start();
                }
            } catch (Exception ex) {
                canRun = false;
                try {
                    serverSocket.close();
                } catch (Exception e) {
                }
            }
        }
//点击功能
        public void actionPerformed (ActionEvent e){
            if (e.getSource() == btSend) {
                this.sendMessage();
            } else if (e.getSource() == btRemove) {
                this.removeUser();
            } else if (e.getSource() == btClose) {
                System.exit(0);
            }
        }
    }

