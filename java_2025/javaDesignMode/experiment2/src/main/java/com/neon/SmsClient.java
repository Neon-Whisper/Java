package com.neon;

// 客户端
public class SmsClient {
    private SmsTarget smsTarget;

    public SmsClient(SmsTarget smsTarget) {
        this.smsTarget = smsTarget;
    }

    public void sendMessage(String phone, String content) throws Exception {
        String success = smsTarget.sendSms(phone, content);
    }
}
