package com.neon;

import java.util.Scanner;

public class Test2 {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入车型（SUV/Sport）：");
        String carType = scanner.next();
        System.out.print("请输入级别（Super/Med/Slow）：");
        String level = scanner.next();

        // 简单工厂+反射创建赛车
        if ("SUV".equals(carType)) {
            ISUV suv = (ISUV) SimpleCarFactory.createCar(carType, level);
            suv.ignition();
            suv.startUp();
        } else if ("Sport".equals(carType)) {
            ISport sport = (ISport) SimpleCarFactory.createCar(carType, level);
            sport.ignition();
            sport.startUp();
        }
        scanner.close();
    }
}
