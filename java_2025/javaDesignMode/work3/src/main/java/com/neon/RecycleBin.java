package com.neon;

public class RecycleBin {
    private static final RecycleBin instance = new RecycleBin();

    // 私有构造
    private RecycleBin() {}

    // 公共方法获取唯一实例
    public static RecycleBin getInstance() {
        return instance;
    }

    public void restore(String file) {
        System.out.println("还原文件：" + file);
    }

    public void delete(String file) {
        System.out.println("删除文件：" + file);
    }
}