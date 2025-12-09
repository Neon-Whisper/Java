package com.neon;

// 高级车工厂
public class SuperCarFactory implements CarFactory {
    @Override
    public ISUV createSUV() {
        return new SuperSUV();
    }

    @Override
    public ISport createSport() {
        return new SuperSport();
    }
}
