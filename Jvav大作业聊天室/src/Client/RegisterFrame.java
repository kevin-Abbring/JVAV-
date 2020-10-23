package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;

import Server.*;

/*
 * 用户注册界面
 */
public class RegisterFrame extends JFrame implements ActionListener {
    //定义各控件
    private JLabel lbName = new JLabel("请你输入姓名");
    private JTextField tfName = new JTextField(10);
    private JLabel lbAccount = new JLabel("请你输入账号");
    private JTextField tfAccount = new JTextField(10);
    private JLabel lbPassword1 = new JLabel("请你输入密码");
    private JPasswordField pfPassword1 = new JPasswordField(10);
    private JLabel lbPassword2 = new JLabel("输入确认密码");
    private JPasswordField pfPassword2 = new JPasswordField(10);
    private JButton btLogin = new JButton("登录");
    private JButton btRegister = new JButton("注册");
    private JButton btExit = new JButton("退出");
    private JPanel JPname = new JPanel();
    private JPanel JPaccount = new JPanel();
    private JPanel JPpassword1 = new JPanel();
    private JPanel JPpassword2 = new JPanel();
    private JPanel JPb = new JPanel();

    private Socket socket = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    public RegisterFrame() {
        super("注册");
        this.setLayout(new GridLayout(5, 1));
        JPname.add(lbName);
        JPname.add(tfName);
        JPaccount.add(lbAccount);
        JPaccount.add(tfAccount);
        JPpassword1.add(lbPassword1);
        JPpassword1.add(pfPassword1);
        JPpassword2.add(lbPassword2);
        JPpassword2.add(pfPassword2);
        JPb.add(btLogin);
        JPb.add(btRegister);
        JPb.add(btExit);
        this.add(JPname);
        this.add(JPaccount);
        this.add(JPpassword1);
        this.add(JPpassword2);
        this.add(JPb);
        this.setSize(300, 240);
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

    public void register() throws IOException, ClassNotFoundException {
        User cus = new User();
        cus.setName(tfName.getText());
        cus.setAccount(tfAccount.getText());
        cus.setPassword(new String(pfPassword1.getPassword()));
        Message msg = new Message();
        msg.setType(MessageConfigure.REGISTER);
        msg.setContent(cus);
        try {
//实例化 socket 和输入输出流
            socket = new Socket(StartServer.serverIP, StartServer.port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
//将消息通过对象输出流写入，传到服务器端
            oos.writeObject(msg);
//获取服务器端的消息
            Message receiveMsg = (Message) ois.readObject();
//获取消息类型
            String type = receiveMsg.getType();
//如果类型为REGISTERFALL
            if (type.equals(MessageConfigure.REGISTERFALL)) {
                JOptionPane.showMessageDialog(this, "注册失败");
            } else {
                JOptionPane.showMessageDialog(this, "注册成功");
//打开登陆界面
                new LogFrame();
//关闭界面
                this.dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "网络连接异常");
            System.exit(0);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (e.getSource() == btRegister) {
            //获取两个密码并比对
            String password1 = new String(pfPassword1.getPassword());
            String password2 = new String(pfPassword2.getPassword());
            if (!password1.equals(password2)) {
                JOptionPane.showMessageDialog(this, "两个密码不相同");
                return;

            }
            //连接到服务器并发送注册消息
            try {
                this.register();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        } else if (e.getSource() == btLogin) {
            //关闭界面
            this.dispose();
            //打开登陆界面
            new LogFrame();
        } else {
            JOptionPane.showMessageDialog(this, "再见");
            System.exit(0);
        }
    }
}
