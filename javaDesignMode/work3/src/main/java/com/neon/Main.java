package com.neon;

public class Main {
    public static void main(String[] args) {
        // 获取单例实例
        RecycleBin recycleBin = RecycleBin.getInstance();
        // 还原文件
        recycleBin.restore("spring.java");
    }
}