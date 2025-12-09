package com.neon;

// 目标接口
public interface SmsTarget {
    String sendSms(String phoneNumber, String content) throws Exception;
}
