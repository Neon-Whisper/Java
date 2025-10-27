package com.neon;

// 低级Sport
public class SlowSport implements ISport {
    @Override
    public void ignition() {
        System.out.println("低级Sport：点火成功");
    }
    @Override
    public void startUp() {
        System.out.println("低级Sport：启动成功");
    }
}
