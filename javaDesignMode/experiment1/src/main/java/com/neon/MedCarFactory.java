package com.neon;

// 中级车工厂
public class MedCarFactory implements CarFactory {
    @Override
    public ISUV createSUV() {
        return new MedSUV();
    }

    @Override
    public ISport createSport() {
        return new MedSport();
    }
}
