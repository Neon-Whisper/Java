package com.neon;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientController {
    private HttpClient httpClient;
    private List<Flight> lastSearchResult;
    private List<Booking> bookings;
    private Scanner scanner;

    public ClientController() {
        this.httpClient = new HttpClient();
        this.bookings = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

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
                    searchFlights();
                    break;
                case 2:
                    bookFlightFromSearch();
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

        // 请求服务器数据
        lastSearchResult = httpClient.searchFlights(depCity, arrCity, date);

        // 筛选满足座位要求的航班
        lastSearchResult.removeIf(f -> f.getRemainingSeats() < count);

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

        // 发送预订请求到服务器
        if (httpClient.createBooking(booking)) {
            System.out.println("\n正在发送订单至服务器");
            // 打印收据
            System.out.println("\n===== 预订成功！收据如下 =====");
            booking.printReceipt();
        } else {
            System.out.println("预订失败！");
        }
    }

    private void cancelBooking() {
        System.out.print("请输入预订编号：");
        String bookingId = scanner.nextLine();
        System.out.println("正在请求服务器");

        if (httpClient.cancelBooking(bookingId)) {
            System.out.println("预订已取消！");
        } else {
            System.out.println("未找到该预订！");
        }
    }
}
