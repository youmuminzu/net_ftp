package com.net.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server {

    public void startSever() {
        ServerSocket server = null;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String localIP = inetAddress.getHostAddress();
            System.out.println("请在浏览器中输入如下服务器IP地址:");
            System.out.println(localIP);
            server = new ServerSocket(80);
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            while (true) {
                Socket socket = server.accept();
                Handler handler = new Handler(socket);
                Future<Boolean> future = executorService.submit(handler);
                //Boolean result = future.get();
//                System.out.println(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                if(server != null) {
                    server.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }
}
