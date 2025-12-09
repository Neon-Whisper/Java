package com.neon;

public class Test {
    public static void main(String[] args) throws Exception {
        SmsTarget adapter = new SmsAdapter();
        System.out.println("开始发送短信...");
        new SmsClient(adapter).sendMessage(
                "13800138000",
                "验证码：6666（5分钟有效）");
    }
}
