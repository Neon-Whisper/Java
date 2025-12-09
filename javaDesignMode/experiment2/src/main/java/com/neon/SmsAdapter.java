package com.neon;

// 适配器
public class SmsAdapter implements com.neon.SmsTarget {
    private WebChineseSms webChineseSms = new WebChineseSms();

    @Override
    public String sendSms(String phoneNumber, String content) throws Exception {
            String result = webChineseSms.transmit(phoneNumber, content);
            return result;
    }
}

