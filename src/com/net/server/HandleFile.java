package com.net.server;

import javax.print.attribute.standard.Finishings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class HandleFile {

    public HashMap<String, String> scanning(String filePath) {
        HashMap<String, String> fileList = new HashMap<>();
        if(filePath.equals("root")) {
            File[] files = File.listRoots();
            for (File file: files) {
                fileList.put(file.getPath(),"DIR");
            }
        } else {
            File file = new File(filePath);
            if(file.isDirectory()) {
                for (File children:file.listFiles()) {
                    if(children.isFile()) {
                        fileList.put(children.getPath(),"FILE");
                    } else if(children.isDirectory()) {
                        fileList.put(children.getPath(),"DIR");
                    }
                }
            }
            if(file.isFile()) {
                fileList.put(filePath,"FILE");
            }
        }
        return fileList;
    }

    public Boolean transFile(Socket socket,String path) throws Exception {
        FileInputStream fileInputStream = null;
        int len;
        try {
            fileInputStream = new FileInputStream(path);
            byte[] outBytes = new byte[1024];
            while ((len = fileInputStream.read(outBytes))>0) {
                socket.getOutputStream().write(outBytes,0,len);
                if(len<1024) {
                    break;
                }
            }
        } catch (IOException e) {
           throw e;
        } finally {
            try {
                if(fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e1) {
                throw e1;
            }
        }
        return true;
    }
}
