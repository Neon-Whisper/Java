package com.neon;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ReflectClient {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            // 加载配置文件
            properties.load(new FileInputStream("src/config.properties"));
            // 获取配置的工厂类名
            String factoryClassName = properties.getProperty("carFactory");
            // 通过反射创建工厂实例
            Class<?> factoryClass = Class.forName(factoryClassName);
            CarFactory factory = (CarFactory) factoryClass.
                    getDeclaredConstructor().newInstance();
            // 创建汽车并使用
            Car car = factory.createCar();
            car.drive();
        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}