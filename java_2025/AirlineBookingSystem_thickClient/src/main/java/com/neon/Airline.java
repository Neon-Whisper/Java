package com.neon;

public class Airline {
    private String code;    // 航空代码
    private String name;    // 航空名称
    private double discount;// 折扣率

    public Airline(String code, String name, double discount) {
        this.code = code;
        this.name = name;
        this.discount = discount;
    }

    public String getName() { return name; }
    public double getOwnDiscount() { return discount; }
}