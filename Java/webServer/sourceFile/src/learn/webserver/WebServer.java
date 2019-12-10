package learn.webserver;

import java.io.*;
import java.net.*;

public class WebServer {
    public static void main(String args[]) throws URISyntaxException {
        //Port Message
        int port;
        ServerSocket server_socket;

        //读取服务器端口号
        try {
            port = Integer.parseInt(args[0]);     //用户从参数传入指定端口号
        }
        catch (Exception e) {
            port = 8888;      //设置默认端口
        }
        try {
            server_socket = new ServerSocket(port);     //创建ServerSocket实例，并指定监听端口
            System.out.println("WebServer is running on port : " +
                    server_socket.getLocalPort());      //getLocalPort()：返回此套接字绑定到的本地端口

            //显示启动信息
            while(true) {

                Socket socket = server_socket.accept();      //服务器接收线程，等待客服端连接，accept()在连接之前一直阻塞
                System.out.println("New connection accepted " +
                        socket.getInetAddress() +
                        ":" + socket.getPort());

                //创建分线程
                try {
                    HttpRequestHandler request =
                            new HttpRequestHandler(socket);
                    Thread thread = new Thread(request);      //创建线程
                    thread.start();      //start thread
                }
                catch(Exception e) {
                    System.out.println(e);      //Print error Massage
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);       //Print error Massage
        }
    }
}
