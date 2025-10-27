package com.neon;

import java.util.Scanner;

public class SimpleCarFactory {
    public static Object createCar(String carType, String level) throws Exception {

        String className = "com.neon." + level + carType;
        Class<?> carClass = Class.forName(className);
        return carClass.newInstance();
    }
}

