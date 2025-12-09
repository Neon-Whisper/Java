package com.neon;

public class Airport {
    private String code; // 机场代码
    private String city; // 所在城市
    private String country; // 所在国家
    private int connectionTime; // 转机时间（分钟）

    public Airport(String code, String city, String country, int connectionTime) {
        this.code = code;
        this.city = city;
        this.country = country;
        this.connectionTime = connectionTime;
    }

    public String getCode() { return code; }
    public String getCity() { return city; }
    public int getConnectionTime() { return connectionTime; }
}