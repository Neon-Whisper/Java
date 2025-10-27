package com.neon;


// 抽象汽车
abstract class Car {
    public abstract void drive();
}

// 宝马汽车
class BMWCar extends Car {
    @Override
    public void drive() {
        System.out.println("宝马汽车行驶中");
    }
}

// 奔驰汽车
class BenzCar extends Car {
    @Override
    public void drive() {
        System.out.println("奔驰汽车行驶中");
    }
}

// 汽车工厂
abstract class CarFactory {
    public abstract Car createCar();
}

// 宝马工厂
class BMWFactory extends CarFactory {
    @Override
    public Car createCar() {
        return new BMWCar();
    }
}

// 奔驰工厂
class BenzFactory extends CarFactory {
    @Override
    public Car createCar() {
        return new BenzCar();
    }
}

public class Client {
    public static void main(String[] args) {
        // 宝马工厂生产宝马汽车
        CarFactory bmwFactory = new BMWFactory();
        Car bmwCar = bmwFactory.createCar();
        bmwCar.drive();

        // 奔驰工厂生产奔驰汽车
        CarFactory benzFactory = new BenzFactory();
        Car benzCar = benzFactory.createCar();
        benzCar.drive();
    }
}