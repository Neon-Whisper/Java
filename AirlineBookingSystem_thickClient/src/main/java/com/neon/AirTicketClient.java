package com.neon;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//单体实现


public class AirTicketClient {
    private List<Airline> airlines;
    private List<Flight> flights;
    private List<Booking> bookings;
    private Scanner scanner;

    // 初始化测试数据
    public AirTicketClient() {
        airlines = new ArrayList<>();
        airlines.add(new Airline("CA", "中国国航", 0.05));
        airlines.add(new Airline("MU", "东方航空", 0.03));

        flights = new ArrayList<>();
        flights.add(new Flight("CA1301", "北京", "上海", "08:00", "10:20",
                "2025-10-28", 1200, airlines.get(0)));
        flights.add(new Flight("MU5102", "北京", "上海", "09:30", "11:50",
                "2025-10-28", 1100, airlines.get(1)));
        flights.add(new Flight("CA1503", "北京", "上海", "14:00", "16:15",
                "2025-10-28", 1050, airlines.get(0)));

        bookings = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    // 主菜单
    public void showMainMenu() {
        while (true) {
            System.out.println("\n===== 航空订票系统 =====");
            System.out.println("1. 查询航班");
            System.out.println("2. 预订航班（从查询结果中选择）");
            System.out.println("3. 取消预订");
            System.out.println("4. 退出");
            System.out.print("请选择：");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 吸收换行

            switch (choice) {
                case 1:
                    searchFlights(); // 先查询，保存结果供预订使用
                    break;
                case 2:
                    bookFlightFromSearch(); // 从查询结果中预订
                    break;
                case 3:
                    cancelBooking();
                    break;
                case 4:
                    System.out.println("再见！");
                    return;
                default:
                    System.out.println("输入错误！");
            }
        }
    }

    // 存储查询结果（供预订用例调用）
    private List<Flight> lastSearchResult;

    // 1. 查询航班（结果保存到lastSearchResult）
    private void searchFlights() {
        System.out.println("\n===== 查询航班 =====");
        System.out.println("正在请求服务器数据");
        System.out.print("出发城市：");
        String depCity = scanner.nextLine();
        System.out.print("目的城市：");
        String arrCity = scanner.nextLine();
        System.out.print("出行日期（yyyy-MM-dd）：");
        String date = scanner.nextLine();
        System.out.print("排序方式（1-价格 2-时间）：");
        int sortType = scanner.nextInt();
        System.out.print("出行人数：");
        int count = scanner.nextInt();
        scanner.nextLine();

        // 筛选航班
        lastSearchResult = new ArrayList<>();
        for (Flight f : flights) {
            if (f.getDepCity().equals(depCity) && f.getArrCity().equals(arrCity)
                    && f.getDate().equals(date) && f.getRemainingSeats() >= count) {
                lastSearchResult.add(f);
            }
        }

        // 排序
        if (sortType == 1) {
            lastSearchResult.sort((f1, f2) -> Double.compare(f1.getFare(), f2.getFare()));
        } else {
            lastSearchResult.sort((f1, f2) -> Integer.compare(f1.getFlightMinutes(), f2.getFlightMinutes()));
        }

        // 显示查询结果
        if (lastSearchResult.isEmpty()) {
            System.out.println("无符合条件的航班！");
            return;
        }
        System.out.println("\n===== 查询结果 =====");
        for (int i = 0; i < lastSearchResult.size(); i++) {
            Flight f = lastSearchResult.get(i);
            System.out.println((i+1) + ". " + f.getAirline().getName() + " " + f.getNumber());
            System.out.println("   时间：" + f.getDepTime() + "-" + f.getArrTime()
                    + "（" + f.getFlightMinutes() + "分钟）");
            System.out.println("   票价：" + f.getFare() + "元，剩余座位：" + f.getRemainingSeats() + "\n");
        }
        System.out.println("可选择以上航班进行预订（请选择功能2）");
    }

    // 2. 预订行程（完整子用例流程）
    private void bookFlightFromSearch() {
        // 检查是否有查询结果
        if (lastSearchResult == null || lastSearchResult.isEmpty()) {
            System.out.println("请先执行查询（功能1）并获取有效结果！");
            return;
        }

        System.out.println("\n===== 预订航班 =====");
        // 子用例1：从查询结果中选择目标行程
        System.out.println("请从以下查询结果中选择航班（输入序号）：");
        for (int i = 0; i < lastSearchResult.size(); i++) {
            Flight f = lastSearchResult.get(i);
            System.out.println((i+1) + ". " + f.getNumber() + " " + f.getDepTime()
                    + "-" + f.getArrTime() + " " + f.getFare() + "元");
        }
        System.out.print("选择序号：");
        int flightIdx = scanner.nextInt() - 1;
        scanner.nextLine();
        if (flightIdx < 0 || flightIdx >= lastSearchResult.size()) {
            System.out.println("选择无效！");
            return;
        }
        Flight selectedFlight = lastSearchResult.get(flightIdx);

        // 子用例2：选择座位偏好
        System.out.println("\n座位偏好：");
        System.out.println("1. 靠窗");
        System.out.println("2. 过道");
        System.out.println("3. 中间");
        System.out.print("请选择（1-3）：");
        int seatChoice = scanner.nextInt();
        scanner.nextLine();
        String preference = switch (seatChoice) {
            case 1 -> "靠窗";
            case 2 -> "过道";
            case 3 -> "中间";
            default -> "靠窗";
        };

        // 确认出行人数
        System.out.print("请输入出行人数：");
        int passengerCount = scanner.nextInt();
        scanner.nextLine();
        if (passengerCount <= 0 || selectedFlight.getRemainingSeats() < passengerCount) {
            System.out.println("人数无效或座位不足！");
            return;
        }

        // 子用例3：输入乘客信息
        System.out.println("\n请输入乘客信息：");
        System.out.print("姓名：");
        String name = scanner.nextLine();
        System.out.print("证件类型（身份证/护照）：");
        String idType = scanner.nextLine();
        System.out.print("证件号码：");
        String idNum = scanner.nextLine();

        // 子用例4：提交预订并获取收据
        // 按偏好选座
        List<Seat> chosenSeats = selectedFlight.chooseSeatsByPreference(preference, passengerCount);
        if (chosenSeats.isEmpty()) {
            System.out.println("预订失败，座位不足！");
            return;
        }

        // 计算最终价格（含折扣）
        double finalFare = selectedFlight.getFare() * (1 - selectedFlight.getAirline().getOwnDiscount()) * passengerCount;

        // 创建预订记录
        Booking booking = new Booking(
                "BOOK-" + (int)(Math.random()*10000),
                name, idType, idNum,
                selectedFlight,
                chosenSeats,
                finalFare
        );
        bookings.add(booking);

        System.out.println("\n正在发送订单至服务器");
        // 打印收据
        System.out.println("\n===== 预订成功！收据如下 =====");
        booking.printReceipt();
    }

    // 3. 取消预订
    private void cancelBooking() {
        System.out.print("请输入预订编号：");
        String bookingId = scanner.nextLine();
        System.out.println("正在请求服务器");
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)) {
                // 释放座位
                for (Seat s : b.getSeats()) {
                    s.setEmpty(true);
                }
                bookings.remove(b);
                System.out.println("预订已取消！");
                return;
            }
        }
        System.out.println("未找到该预订！");
    }

    public static void main(String[] args) {
        new AirTicketClient().showMainMenu();
    }
}