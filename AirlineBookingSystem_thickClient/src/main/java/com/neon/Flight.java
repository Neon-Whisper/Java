package com.neon;

import java.util.ArrayList;
import java.util.List;

public class Flight {
    private String number;       // 航班号
    private String depCity;      // 出发城市
    private String arrCity;      // 目的城市
    private String depTime;      // 出发时间（HH:mm）
    private String arrTime;      // 到达时间（HH:mm）
    private String date;         // 日期（yyyy-MM-dd）
    private double fare;         // 票价
    private Airline airline;     // 所属航空
    private List<Seat> seats;    // 座位列表

    public Flight(String number, String depCity, String arrCity, String depTime,
                  String arrTime, String date, double fare, Airline airline) {
        this.number = number;
        this.depCity = depCity;
        this.arrCity = arrCity;
        this.depTime = depTime;
        this.arrTime = arrTime;
        this.date = date;
        this.fare = fare;
        this.airline = airline;
        this.seats = new ArrayList<>();
        // 初始化座位（1-6排，A靠窗/B中间/C过道）
        for (int i = 1; i <= 6; i++) {
            seats.add(new Seat(i, "A", "靠窗", true));
            seats.add(new Seat(i, "B", "中间", true));
            seats.add(new Seat(i, "C", "过道", true));
        }
    }

    // 按偏好选择多个座位（支持多人）
    public List<Seat> chooseSeatsByPreference(String preference, int count) {
        List<Seat> chosen = new ArrayList<>();
        // 1. 优先选符合偏好的座位
        for (Seat seat : seats) {
            if (seat.isEmpty() && seat.getType().equals(preference)) {
                seat.setEmpty(false);
                chosen.add(seat);
                if (chosen.size() == count) {
                    return chosen; // 选够数量直接返回
                }
            }
        }
        // 2. 偏好座位不足，选其他空座
        for (Seat seat : seats) {
            if (seat.isEmpty()) {
                seat.setEmpty(false);
                chosen.add(seat);
                if (chosen.size() == count) {
                    return chosen;
                }
            }
        }
        return chosen; // 可能返回空（座位不足）
    }

    // 计算飞行时间（分钟）
    public int getFlightMinutes() {
        String[] dep = depTime.split(":");
        String[] arr = arrTime.split(":");
        int depTotal = Integer.parseInt(dep[0]) * 60 + Integer.parseInt(dep[1]);
        int arrTotal = Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]);
        return arrTotal < depTotal ? arrTotal + 24*60 - depTotal : arrTotal - depTotal;
    }

    // Getter方法
    public String getNumber() { return number; }
    public String getDepCity() { return depCity; }
    public String getArrCity() { return arrCity; }
    public String getDepTime() { return depTime; }
    public String getArrTime() { return arrTime; }
    public String getDate() { return date; }
    public double getFare() { return fare; }
    public Airline getAirline() { return airline; }
    public int getRemainingSeats() {
        int count = 0;
        for (Seat s : seats) if (s.isEmpty()) count++;
        return count;
    }
}