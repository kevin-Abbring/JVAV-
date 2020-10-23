package Server;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//为单个服务端服务，负责接收发送消息
public class ChatThread extends Thread {
    private Socket socket = null;
    private ObjectInputStream ois = null;
    public ObjectOutputStream oos = null;
    public User customer = null;
    private Server server;
    private boolean canRun = true;

    public ChatThread(Socket socket, Server server) throws Exception {
        this.socket = socket;
        this.server = server;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        try {
            while (canRun) {
                Message msg = (Message) ois.readObject();
//转发
                String type = msg.getType();
                if (type.equals(MessageConfigure.LOGIN)) {
                    this.handleLogin(msg);
                } else if (type.equals(MessageConfigure.REGISTER)) {
                    this.handleRegister(msg);

                } else if (type.equals(MessageConfigure.MESSAGE)) {
                    this.handleMessage(msg);
                }
            }
        } catch (Exception ex) {
            this.handleLgout();
        }
    }

    public void handleLogin(Message msg) throws Exception {
        User loginCustomer = (User) msg.getContent();
        String account = loginCustomer.getAccount();
        String password = loginCustomer.getPassword();
        User cus = FileLoader.getCustomerByAccount(account);
        Message newMsg = new Message();
        Message newMsg2 = new Message();
        if (cus == null || !cus.getPassword().equals(password)) {
            newMsg.setType(MessageConfigure.LOGINFALL);
            oos.writeObject(newMsg);
            canRun = false;
            socket.close();
            return;
        } else {
//将用户姓名返回客户端
//消息的内容是用户姓名
            newMsg2.setType(MessageConfigure.CUS);
            newMsg2.setContent(cus.getName());
            oos.writeObject(newMsg2);
        }
        this.customer = cus;
//将该线程放入 clients 集合中
        server.getClients().add(this);
// 将 customer 加 入 userList
        server.getUserList().add(this.customer);

//将所有在线用户都放入客户端
        newMsg.setType(MessageConfigure.USERLIST);
        newMsg.setContent(server.getUserList().clone());
//将该用户登录信息发送给所有用户
        this.sendMessage(newMsg, MessageConfigure.ALL);

        server.setTitle("当前在线:" + server.getClients().size() + "人");
    }
    /*注册*/

    public void handleRegister(Message msg) throws Exception {
        User registerCustomer = (User) msg.getContent();
        String account = registerCustomer.getAccount();
        User cus = FileLoader.getCustomerByAccount(account);
        Message newMsg = new Message();
        if (cus != null) {
            newMsg.setType(MessageConfigure.REGISTERFALL);
        } else {
            String password = registerCustomer.getPassword();
            String name = registerCustomer.getName();
            FileLoader.insertCustomer(account, password, name);
            newMsg.setType(MessageConfigure.REGISTERSUCCESS);
            oos.writeObject(newMsg);
        }
        oos.writeObject(newMsg);
        canRun = false;
        socket.close();
    }

    /*将msg 的内容以聊天信息形式转发*/
    public void handleMessage(Message msg) throws Exception {
        String[] to = msg.getTo().split("\\,");
        for (int i = 0; i < to.length; i++) {
            msg.setTo(to[i]);
            sendMessage(msg, to[i]);
        }
    }

    /*向所有其他客户端发送一个该客户端下线的消息*/
    public void handleLgout() {
        Message logoutMessage = new Message();
        server.getClients().remove(this);
        server.getUserList().remove(this.customer);
//传新的好友列表
        logoutMessage.setType(MessageConfigure.LOGOUT);
        logoutMessage.setContent(server.getUserList().clone());
        try {
            sendMessage(logoutMessage, MessageConfigure.ALL);
            canRun = false;
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        server.setTitle("当前在线：" + server.getClients().size() + "人");

    }

    //将消息发送给客户端
    public void sendMessage(Message msg, String to) throws Exception {
        for (ChatThread ct : server.getClients()) {
            if (ct.customer.getName().equals(to) || to.equals(MessageConfigure.ALL)) {
                ct.oos.writeObject(msg);
            }
        }
        if (msg.getType().equals(MessageConfigure.USERLIST) || msg.getType().equals(MessageConfigure.LOGOUT)) {
            server.initUserList();
        }
    }
}

