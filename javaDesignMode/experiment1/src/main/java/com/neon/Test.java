package com.neon;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入车型（SUV/Sport）：");
        String carType = scanner.next();
        System.out.print("请输入工厂类全路径（如com.neon.SuperCarFactory）：");
        String factoryPath = scanner.next();

        // 反射创建具体工厂
        Class<?> factoryClass = Class.forName(factoryPath);
        CarFactory factory = (CarFactory) factoryClass.newInstance();

        // 创建赛车并启动
        if ("SUV".equals(carType)) {
            ISUV suv = factory.createSUV();
            suv.ignition();
            suv.startUp();
        } else if ("Sport".equals(carType)) {
            ISport sport = factory.createSport();
            sport.ignition();
            sport.startUp();
        }
        scanner.close();
    }
}
