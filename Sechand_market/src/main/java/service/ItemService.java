package service;

import db.DBUtil;
import entity.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemService {
    // 发布物品
    public boolean addItem(Item item) {
        String sql = "INSERT INTO item (userId, itemname, category, price, description, status, imagePath) " +
                "VALUES (?, ?, ?, ?, ?, '在售', ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, item.getUserId());
            pstmt.setString(2, item.getItemName());
            pstmt.setString(3, item.getCategory());
            pstmt.setDouble(4, item.getPrice());
            pstmt.setString(5, item.getDescription());
            pstmt.setString(6, item.getImagePath()); // 新增设置图片路径
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取所有在售物品
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE status = '在售'";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("itemId"));
                item.setUserId(rs.getInt("userId"));
                item.setItemName(rs.getString("itemName"));
                item.setCategory(rs.getString("category"));
                item.setPrice(rs.getDouble("price"));
                item.setDescription(rs.getString("description"));
                item.setStatus(rs.getString("status"));
                item.setImagePath(rs.getString("imagePath")); // 新增获取图片路径
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> searchItems(String name, String category, String sort) {
        List<Item> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM item WHERE status = '在售'");

        // 添加名称条件
        if (!name.isEmpty()) {
            sql.append(" AND itemName LIKE ?");
        }

        // 添加类别条件
        if (!"全部".equals(category)) {
            sql.append(" AND category = ?");
        }

        // 添加排序条件
        if ("价格升序".equals(sort)) {
            sql.append(" ORDER BY price ASC");
        } else if ("价格降序".equals(sort)) {
            sql.append(" ORDER BY price DESC");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // 设置名称参数
            if (!name.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + name + "%");
            }

            // 设置类别参数
            if (!"全部".equals(category)) {
                pstmt.setString(paramIndex, category);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setItemId(rs.getInt("itemId"));
                    item.setUserId(rs.getInt("userId"));
                    item.setItemName(rs.getString("itemName"));
                    item.setCategory(rs.getString("category"));
                    item.setPrice(rs.getDouble("price"));
                    item.setDescription(rs.getString("description"));
                    item.setStatus(rs.getString("status"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // 下架物品
    public boolean removeItem(int itemId) {
        String sql = "UPDATE item SET status = '下架' WHERE itemId = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}