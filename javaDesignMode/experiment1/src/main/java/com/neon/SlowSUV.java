package com.neon;

// 低级SUV
public class SlowSUV implements ISUV {
    @Override
    public void ignition() {
        System.out.println("低级SUV：点火成功");
    }
    @Override
    public void startUp() {
        System.out.println("低级SUV：启动成功");
    }
}
