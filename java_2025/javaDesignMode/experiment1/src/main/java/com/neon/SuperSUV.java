package com.neon;

// 高级SUV
public class SuperSUV implements ISUV {
    @Override
    public void ignition() {
        System.out.println("高级SUV：点火成功");
    }
    @Override
    public void startUp() {
        System.out.println("高级SUV：启动成功");
    }
}
