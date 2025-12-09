package com.neon;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class Main {

    public static void main(String[] args)throws Exception{

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod("https://utf8api.smschinese.cn/");
        post.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");//在头文件中设置转码
        NameValuePair[] data ={
                new NameValuePair("Uid", "晚风Neon"),
                new NameValuePair("Key", "61EBF90E832FC61ADEF2068F5575E6E1"),
                new NameValuePair("smsMob","15912341234"),
                new NameValuePair("smsText","验证码：8888")};
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

    }

}



