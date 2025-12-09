package com.neon;

// 中级Sport
public class MedSport implements ISport {
    @Override
    public void ignition() {
        System.out.println("中级Sport：点火成功");
    }
    @Override
    public void startUp() {
        System.out.println("中级Sport：启动成功");
    }
}
