package com.net.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Handler implements Callable {

    protected Socket socket = null;


    public Handler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public Boolean call() {
         InputStream inputStream = null;
         BufferedInputStream bufferedInputStream = null;
         BufferedWriter bufferedWriter = null;
         HttpRequest request = null;
        try {
            inputStream = this.socket.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[2048];
            String requestString = "";
            int length;
            while ((length = bufferedInputStream.read(buffer))!=-1) {
                String newString = new String(buffer);
                requestString += newString;
                if(length<2048) {
                    break;
                }
            }
            request = new HttpRequest(requestString);
            //System.out.println(requestString);
            HttpResponse response = new HttpResponse(request);
            String html = response.responseHtml();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            bufferedWriter.write(html);
            bufferedWriter.flush();
            Boolean transResult = false;
            if(response.isDownload) {
                HandleFile handleFile = new HandleFile();
                try {
                    transResult = handleFile.transFile(this.socket,request.getParam("path"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            System.out.println("一个客户端已经连接");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream!=null) {
                    inputStream.close();
                }
                if(bufferedInputStream!=null) {
                    bufferedInputStream.close();
                }
                if(bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if(this.socket != null) {
                    this.socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
