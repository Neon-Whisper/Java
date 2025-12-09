package com.neon;

// 低级车工厂
public class SlowCarFactory implements CarFactory {
    @Override
    public ISUV createSUV() {
        return new SlowSUV();
    }

    @Override
    public ISport createSport() {
        return new SlowSport();
    }
}
