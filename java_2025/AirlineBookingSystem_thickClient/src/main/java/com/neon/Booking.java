package com.neon;

import java.util.List;

public class Booking {
    private String bookingId;
    private String passengerName;
    private String idType;
    private String idNum;
    private Flight flight;
    private List<Seat> seats;
    private double totalFare;

    public Booking(String bookingId, String passengerName, String idType, String idNum,
                   Flight flight, List<Seat> seats, double totalFare) {
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.idType = idType;
        this.idNum = idNum;
        this.flight = flight;
        this.seats = seats;
        this.totalFare = totalFare;
    }

    public String getBookingId() { return bookingId; }
    public List<Seat> getSeats() { return seats; }

    // 打印收据（子用例4核心）
    public void printReceipt() {
        System.out.println("预订编号：" + bookingId);
        System.out.println("乘客信息：" + passengerName + "（" + idType + "：" + idNum + "）");
        System.out.println("航班信息：" + flight.getAirline().getName() + " " + flight.getNumber());
        System.out.println("行程：" + flight.getDepCity() + "→" + flight.getArrCity());
        System.out.println("日期时间：" + flight.getDate() + " " + flight.getDepTime() + "-" + flight.getArrTime());
        System.out.println("座位：" + seats);
        System.out.println("总价：" + totalFare + "元（已含折扣）");
        System.out.println("==========================");
    }
}