package com.neon;

// 抽象工厂接口
public interface CarFactory {
    ISUV createSUV();
    ISport createSport();
}
