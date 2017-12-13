package com.net.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

public class HttpRequest {
    public String url;
    public HashMap<String,String> paramsMap = new HashMap<>();
    public String method;

    public HttpRequest(String originalString) {
        originalString = originalString.trim();
        String[] requestBlocks = originalString.split("\r\n\r\n");
        String requestHead = requestBlocks[0];
        String requestBody = null;
        if(requestBlocks.length>1) {
            requestBody = requestBlocks[1];
        }
        String[] requestHeadArray = requestHead.trim().split("\r\n");

        String[] firstLine = requestHeadArray[0].split(" ");
        if(firstLine.length>1) {
            this.method = firstLine[0].toUpperCase();
            this.url = firstLine[1];
        }
        String paramStr = null;
        if(this.url.contains("?")) {
            paramStr = this.url.substring(this.url.indexOf("?")+1);
            String[] paramsArrGet = paramStr.split("&");
            for (String param: paramsArrGet) {
                int indexCut = param.indexOf("=");
                this.paramsMap.put(param.substring(0,indexCut),param.substring(indexCut+1));
            }
        }
        if(requestBody!=null && this.method.equals("POST")) {
            String[] paramsPost = requestBody.split("&");
            for (String param: paramsPost) {
                int indexCut = param.indexOf("=");
                this.paramsMap.put(param.substring(0,indexCut),param.substring(indexCut+1));
            }
        }
    }


    public HashMap<String, String> getParamsMap() {
        return paramsMap;
    }

    public String getMethod() {
        return this.method;
    }

    public String getParam(String key) {
        String result = null;
        try{
            result = URLDecoder.decode(this.paramsMap.get(key),"UTF-8");
        } catch (NullPointerException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return this.paramsMap.get(key);
        }
        return result;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url='" + url + '\'' +
                ", paramsMap=" + paramsMap.toString() +
                ", method='" + method + '\'' +
                '}';
    }
}
