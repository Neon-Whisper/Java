package com.neon;

// 中级SUV
public class MedSUV implements ISUV {
    @Override
    public void ignition() {
        System.out.println("中级SUV：点火成功");
    }
    @Override
    public void startUp() {
        System.out.println("中级SUV：启动成功");
    }
}
