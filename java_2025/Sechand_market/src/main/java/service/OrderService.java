package service;

import db.DBUtil;
import entity.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    // 创建订单（待付款状态）
    public boolean createOrder(int buyerId, int itemId, double price) {
        String sql = "INSERT INTO `order` (userId, itemid, orderstatus, transactionprice) " +
                "VALUES (?, ?, '待付款', ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, buyerId);
            pstmt.setInt(2, itemId);
            pstmt.setDouble(3, price);

            // 同时更新物品状态为已售出
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE item SET status = '已售出' WHERE itemId = " + itemId);
            }
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 处理订单
    public boolean dealOrder(int orderId) {
        String sql = "UPDATE `order` SET orderstatus = '已处理' WHERE orderId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取所有订单
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `order`";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("orderId"));
                order.setUserId(rs.getInt("userId"));
                order.setItemId(rs.getInt("itemid"));
                order.setOrderStatus(rs.getString("orderstatus"));
                order.setTransactionPrice(rs.getDouble("transactionprice"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}