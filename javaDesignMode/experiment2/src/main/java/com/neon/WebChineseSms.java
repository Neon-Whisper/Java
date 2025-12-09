package com.neon;

import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class WebChineseSms {
    private static final String UID = "晚风Neon";
    private static final String KEY = "61EBF90E832FC61ADEF2068F5575E6E1";
    private static final String API_URL = "https://utf8api.smschinese.cn/";

    public String transmit(String phone, String content) throws Exception {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(API_URL);
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        NameValuePair[] data = {
                new NameValuePair("Uid", UID),
                new NameValuePair("Key", KEY),
                new NameValuePair("smsMob", phone),
                new NameValuePair("smsText", content)
        };
        post.setRequestBody(data);
        client.executeMethod(post);
        Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("statusCode:"+statusCode); //HTTP状态码
//        for(Header h : headers){
//            System.out.println(h.toString());
//        }
        String result = new String(post.getResponseBodyAsString().getBytes("utf-8"));
        System.out.println(result);  //打印返回消息状态

        post.releaseConnection();
        return result;
    }
}