package Server;

import Client.LogFrame;
import Server.Server;

public class StartServer {

    public static String serverIP;
    public static int port;
    public static void main(String[] args) throws Exception{
        new Server();
        serverIP = "127.0.0.1";
        port = 8866;
        new LogFrame();
    }
}
