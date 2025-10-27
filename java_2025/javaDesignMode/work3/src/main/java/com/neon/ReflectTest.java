package com.neon;

import java.lang.reflect.Constructor;

public class ReflectTest {
    public static void main(String[] args) {
        try {
            // 获取枚举类的构造器
            Constructor<RecycleBinEnum> constructor = RecycleBinEnum.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            // 尝试创建新实例（预期失败）
            RecycleBinEnum newInstance = constructor.newInstance();
        } catch (Exception e) {
            System.out.println("反射创建枚举单例失败，异常：" + e.getMessage());
        }
    }
}
