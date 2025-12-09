package com.neon;

public enum RecycleBinEnum {
    INSTANCE;

    // 还原文件
    public void restore(String file) {
        System.out.println("还原文件：" + file);
    }

    // 删除文件
    public void delete(String file) {
        System.out.println("删除文件：" + file);
    }
}


