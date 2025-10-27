package com.neon;

public class Bank {
    public void sendSMS(String phone, String content) {
        System.out.println("短信发送到 " + phone + "：" + content);
    }
}
