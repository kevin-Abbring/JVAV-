package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;

import Server.*;

public class LogFrame extends JFrame implements ActionListener {
    //定义控件
    private JLabel lbAccount = new JLabel("账号");
    private JButton btRegister = new JButton("注册");
    private JLabel lbPassword = new JLabel("密码");
    private JButton btLogin = new JButton("登录");
    private JButton btExit = new JButton("退出");
    private JPasswordField pfPassword = new JPasswordField(10);
    private JTextField tfAccount = new JTextField(10);
    private JPanel JPpassword = new JPanel();
    private JPanel JPaccount = new JPanel();
    private JPanel JPb = new JPanel();
    private Socket socket = null;
    private ObjectOutputStream OPS = null;
    private ObjectInputStream IPS = null;
    //用构造函数完成界面初始化和监听
    public LogFrame() {
        super("登录");
        this.setLayout(new GridLayout(3, 1));
        JPaccount.add(lbAccount);
        JPaccount.add(tfAccount);
        JPpassword.add(lbPassword);
        JPpassword.add(pfPassword);
        JPb.add(btLogin);
        JPb.add(btRegister);
        JPb.add(btExit);
        this.add(JPaccount);
        this.add(JPpassword);
        this.add(JPb);
        this.setSize(240, 180);
        toCenter(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        btLogin.addActionListener(this);
        btRegister.addActionListener(this);
        btExit.addActionListener(this);
    }

    public static void toCenter(Component comp) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rec = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        comp.setLocation(((int) rec.getWidth() - comp.getWidth()) / 2, ((int) rec.getHeight() - comp.getHeight()) / 2);
    }
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (e.getSource() == btLogin) {
            //调用登录的函数
            this.login();
        } else if (e.getSource() == btRegister) {
            //关闭这个界面
            this.dispose();
            //打开注册的界面
            new RegisterFrame();
        } else {
            JOptionPane.showMessageDialog(null, "退出");
            System.exit(0);
        }
    }
    public void login() {
        User cus = new User();
        cus.setAccount(tfAccount.getText());
        cus.setPassword(new String(pfPassword.getPassword()));
        Message msg = new Message();//新建一个消息对象
        msg.setType(MessageConfigure.LOGIN); //设置消息的类型是LOGIN
        msg.setContent(cus);  //消息的内容是登录
        try {

            socket = new Socket(StartServer.serverIP, StartServer.port);  //实例化 socket 和输入输出流
            OPS = new ObjectOutputStream(socket.getOutputStream());
            IPS = new ObjectInputStream(socket.getInputStream());
            OPS.writeObject(msg);    //将消息通过对象输出流写入，传到服务器端
            Message receiveMsg = (Message) IPS.readObject();  //获取服务器端的消息
            String type = receiveMsg.getType();   //获取消息类型
            String name = (String) receiveMsg.getContent();      //获取服务器端的消息
            if (type.equals(MessageConfigure.LOGINFALL)) {    //如果类型为LOGINFALL
                JOptionPane.showMessageDialog(this, "登录失败");
            } else if (type.equals(MessageConfigure.CUS)) {
                JOptionPane.showMessageDialog(this, "登录成功");
                receiveMsg = (Message) IPS.readObject();   //新建聊天窗口，将输入输出流，收到的消息传入
                new ChatFrame(IPS, OPS, receiveMsg, name, cus.getAccount());
                this.dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "网络连接异常");
            System.exit(-1);
        }
    }
}
