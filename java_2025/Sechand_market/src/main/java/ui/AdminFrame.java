package ui;

import db.DBUtil;
import entity.Item;
import entity.Order;
import entity.User;
import service.ItemService;
import service.OrderService;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminFrame extends JFrame {
    private User currentUser;
    private ItemService itemService = new ItemService();
    private OrderService orderService = new OrderService();
    private UserService userService = new UserService();
    private JTabbedPane tabbedPane;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTable itemTable;
    private DefaultTableModel itemTableModel;
    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    public AdminFrame(User user) {
        this.currentUser = user;
        setTitle("大学生二手货市场管理系统 - 管理员 - " + user.getUsername());
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建菜单
        createMenuBar();

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();

        // 创建用户管理面板
        JPanel userPanel = createUserPanel();
        tabbedPane.addTab("用户管理", userPanel);

        // 创建物品管理面板
        JPanel itemPanel = createItemPanel();
        tabbedPane.addTab("物品管理", itemPanel);

        // 创建订单管理面板
        JPanel orderPanel = createOrderPanel();
        tabbedPane.addTab("订单管理", orderPanel);

        add(tabbedPane);

        // 初始化数据
        loadUsers();
        loadItems();
        loadOrders();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        JMenuItem exitItem = new JMenuItem("退出系统");

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(AdminFrame.this,
                        "确定要退出登录吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose(); // 关闭主窗口
                    new LoginFrame().setVisible(true); // 打开登录窗口
                }
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(AdminFrame.this,
                        "确定要退出系统吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建表格
        String[] columnNames = {"用户ID", "用户名", "手机号", "角色"};
        userTableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton deleteUserButton = new JButton("删除用户");

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "请先选择要删除的用户！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int userId = (int) userTableModel.getValueAt(selectedRow, 0);
                if (userService.deleteUser(userId)) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "用户删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers(); // 刷新用户列表
                } else {
                    JOptionPane.showMessageDialog(AdminFrame.this, "用户删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(deleteUserButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createItemPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建表格
        String[] columnNames = {"物品ID", "名称", "类别", "价格", "描述", "状态"};
        itemTableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(itemTableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton removeItemButton = new JButton("下架物品");

        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "请先选择要下架的物品！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int itemId = (int) itemTableModel.getValueAt(selectedRow, 0);
                if (itemService.removeItem(itemId)) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "物品下架成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadItems(); // 刷新物品列表
                } else {
                    JOptionPane.showMessageDialog(AdminFrame.this, "物品下架失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(removeItemButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建表格
        String[] columnNames = {"订单ID", "用户ID", "物品ID", "订单状态", "交易价格"};
        orderTableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(orderTableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton dealOrderButton = new JButton("处理订单");

        dealOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "请先选择要处理的订单！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);
                if (orderService.dealOrder(orderId)) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "订单处理成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadOrders(); // 刷新订单列表
                } else {
                    JOptionPane.showMessageDialog(AdminFrame.this, "订单处理失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(dealOrderButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsers() {
        userTableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            userTableModel.addRow(new Object[]{user.getUserId(), user.getUsername(), user.getPhone(), user.getRole()});
        }
    }

    private void loadItems() {
        itemTableModel.setRowCount(0);
        List<Item> items = itemService.getAllItems();
        for (Item item : items) {
            itemTableModel.addRow(new Object[]{item.getItemId(), item.getItemName(), item.getCategory(), item.getPrice(), item.getDescription(), item.getStatus()});
        }
    }

    private void loadOrders() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderService.getAllOrders();
        for (Order order : orders) {
            orderTableModel.addRow(new Object[]{order.getOrderId(), order.getUserId(), order.getItemId(), order.getOrderStatus(), order.getTransactionPrice()});
        }
    }
}