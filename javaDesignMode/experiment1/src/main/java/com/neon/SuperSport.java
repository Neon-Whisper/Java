package com.neon;

// 高级Sport
public class SuperSport implements ISport {
    @Override
    public void ignition() {
        System.out.println("高级Sport：点火成功");
    }
    @Override
    public void startUp() {
        System.out.println("高级Sport：启动成功");
    }
}
