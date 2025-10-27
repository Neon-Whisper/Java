package com.neon;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入车型（SUV/Sport）：");
        String carType = scanner.next();
        System.out.print("请输入配置级别（Super/Med）：");
        String level = scanner.next();

        // 根据级别获取具体工厂
        CarFactory factory = null;
        if ("Super".equals(level)) {
            factory = new SuperCarFactory();
        } else if ("Med".equals(level)) {
            factory = new MedCarFactory();
        }else if ("Slow".equals(level)) {
            factory = new SlowCarFactory();
        } else {
            System.out.println("输入的级别不存在！");
            return;
        }

        // 创建赛车并启动
        if ("SUV".equals(carType)) {
            ISUV suv = factory.createSUV();
            suv.ignition();
            suv.startUp();
        } else if ("Sport".equals(carType)) {
            ISport sport = factory.createSport();
            sport.ignition();
            sport.startUp();
        } else {
            System.out.println("输入的车型不存在！");
        }
        scanner.close();
    }
}