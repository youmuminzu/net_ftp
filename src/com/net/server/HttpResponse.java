package com.net.server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpRequest request;
    public Boolean isDownload = false;
    public Boolean isVideo = false;
    public HttpResponse(HttpRequest request){
        this.request = request;
        if(this.request.getParam("action")!=null && this.request.getParam("action").equals("download")) {
            this.isDownload = true;
        }
        if(this.request.getParam("action")!=null && this.request.getParam("action").equals("video")) {
            this.isVideo = true;
        }
    }

    public String responseHtml() {
        String html = "";
        html +=(this.generateHead(this.request.getParam("action")) + this.generateBody(this.request.getParam("action")));
        return html;
    }

    private String generateHead(String action) {
        String head = "";
        head = "HTTP/1.1 200 OK\r\n" +
                "Server: MYNETFTP/1.0\r\n";
        Date date = new Date();
        head += ("Date: " + date.toString() + "\r\n");
        if (action != null && action.equals("download")){
            File downFile = new File(this.request.getParam("path"));

            head += ("Content-Disposition: attachment;filename=" + downFile.getName() + "\r\n");
            head += ("Content-Length: " + downFile.length() + "\r\n");
            head += ("Content-Type: application/octet-stream");
        } else if(action != null && action.equals("video")) {
            File videoFile = new File(this.request.getParam("path"));
            head += ("Content-Disposition: attachment;filename=" + videoFile.getName() + "\r\n");
            head += ("Content-Length: " + videoFile.length() + "\r\n");
            head += ("Accept-Ranges: bytes=0-"+ videoFile.length() +"\r\n");
            head += ("Content-Type: video/mp4");
        } else {
            head += ("Content-Type: text/html; charset=UTF-8");
        }
        return head;
    }

    private String generateBody(String action) {
        String body = "\r\n\r\n";
        if(action!=null && action.equals("play")) {
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            String localIP = inetAddress.getHostAddress();
            body += ("<html><head><title></title></head><body>" +
                    "<video width=\"320\" height=\"240\" src=\"http://"+inetAddress.getHostAddress() + "/?action=video&path=" + request.getParam("path") + "\" controls=\"controls\" >\n" +
                    "</body></html>");
        } else if(action!=null && action.equals("video")) {
            body += "</body></html>";
        } else {
            body += "<html><head><title></title></head><body>";
            if(request.getParam("path")!=null && !request.getParam("path").equals("root")) {
                body += this.getFileListHtml(request.getParam("path"));
            } else {
                body += this.getFileListHtml("root");

            }
            body += "</body></html>";
        }
        return body;
    }

    private String getFileListHtml(String path) {
        String parent = "root";
        if(!path.equals("root")){
            File current = new File(path);
            parent = current.getParent();
            if(parent == null) {
                parent = "root";
            }
        }

        String html = "<style>" +
                "        table,table tr th, table tr td { border:1px solid #0094ff; }\n" +
                "        table { min-height: 25px; line-height: 25px; text-align: center; border-collapse: collapse; padding:2px;}" +
                "    </style>" +
                "<table><tr><td colspan=\"2\">当前路径: " + path + "</td><td><a href=\"?action=return&path="+parent+"\">返回上级</a></td></tr>";
        HandleFile handleFile = new HandleFile();
        HashMap<String,String> fileList = handleFile.scanning(path);

        if(fileList.size()>0) {
            for (Map.Entry<String,String> entry:fileList.entrySet()) {
                html += "<tr><td style=\"text-align: left\">"+entry.getKey()+"</td>";
                if(entry.getValue().equals("DIR")) {
                    html += "<td><a href=\"?action=entry&path="+ entry.getKey() +"\">进入</a></td><td><a href=\"\"></a></td></tr>";
                }
                if(entry.getValue().equals("FILE")) {
                    html += "<td><a href=\"?action=download&path="+entry.getKey()+"\" target=\"blank\">下载</a></td><td><a href=\"?action=play&path="+ entry.getKey() +"\" target=\"blank\">播放</a></td></tr>";
                }
            }
        }
        html += "</table>";
        return html;
    }

    private String urlEncode(String string) {
        try {
            return URLEncoder.encode(string,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }

    }

    private String urlDecode(String string) {
        try {
            return URLDecoder.decode(string,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }
    }

}
