package ui;

import db.DBUtil;
import entity.Item;
import entity.Order;
import entity.User;
import service.ItemService;
import service.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame {
    private User currentUser;
    private ItemService itemService = new ItemService();
    private OrderService orderService = new OrderService();
    private JTabbedPane tabbedPane;
    private JTable itemTable;
    private DefaultTableModel itemTableModel;
    private JTable myItemsTable;
    private DefaultTableModel myItemsTableModel;
    private JTable myOrdersTable;
    private DefaultTableModel myOrdersTableModel;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private BufferedImage backgroundImage;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("大学生二手货市场管理系统 - " + user.getUsername());
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 加载背景图片（建议放在 src/main/resources 下）
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/background.jpg"));
        } catch (IOException e) {
            System.err.println("无法加载背景图片");
            e.printStackTrace();
        }

//        // 创建一个自定义面板用于绘制背景
//        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                if (backgroundImage != null) {
//                    // 绘制背景图并拉伸至窗口大小
//                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
//                }
//            }
//        };

        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(240, 240, 255); // 浅蓝白渐变
                Color color2 = new Color(255, 255, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        setContentPane(backgroundPanel);

        // 创建菜单
        createMenuBar();

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();

        // 创建物品列表面板
        JPanel itemPanel = createItemPanel();
        tabbedPane.addTab("浏览物品", itemPanel);

        // 创建我的物品面板
        JPanel myItemsPanel = createMyItemsPanel();
        tabbedPane.addTab("我的物品", myItemsPanel);

        // 创建我的订单面板
        JPanel myOrdersPanel = createMyOrdersPanel();
        tabbedPane.addTab("我的订单", myOrdersPanel);

        add(tabbedPane);

        // 初始化数据
        loadItems();
        loadMyItems();
        loadMyOrders();
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
                int choice = JOptionPane.showConfirmDialog(MainFrame.this,
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
                int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要退出系统吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);

        // 商品菜单
        JMenu itemMenu = new JMenu("商品");
        JMenuItem addItemItem = new JMenuItem("发布商品");

        addItemItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });

        itemMenu.add(addItemItem);

        menuBar.add(fileMenu);
        menuBar.add(itemMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createItemPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建搜索面板
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");

        // 添加排序选项
        String[] sortOptions = {"默认排序", "价格升序", "价格降序"};
        sortComboBox = new JComboBox<>(sortOptions);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 实现搜索功能
                String keyword = searchField.getText().trim();
                String sortOption = (String) sortComboBox.getSelectedItem();
                if (keyword.isEmpty()) {
                    loadItems(sortOption); // 清空搜索条件，加载所有物品
                } else {
                    searchItems(keyword, sortOption);
                }
            }
        });

        searchPanel.add(new JLabel("搜索:"));
        searchPanel.add(searchField);
        searchPanel.add(sortComboBox);
        searchPanel.add(searchButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // 创建表格
        String[] columnNames = {"图片", "ID", "名称", "类别", "价格", "描述"};
        itemTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                // 设置第一列为 ImageIcon 类型，用于显示图片
                return column == 0 ? ImageIcon.class : Object.class;
            }
        };
        itemTable = new JTable(itemTableModel);
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(80); // 设置图片列宽度
        JScrollPane scrollPane = new JScrollPane(itemTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton buyButton = new JButton("购买");

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请先选择要购买的物品！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int itemId = (int) itemTableModel.getValueAt(selectedRow, 1); // ID在第2列

                // 创建订单
                if (orderService.createOrder(currentUser.getUserId(), itemId, (double) itemTableModel.getValueAt(selectedRow, 4))) {
                    JOptionPane.showMessageDialog(MainFrame.this, "订单创建成功！状态：待付款", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadItems((String) sortComboBox.getSelectedItem()); // 刷新物品列表
                    loadMyOrders(); // 刷新订单列表
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "订单创建失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(buyButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建表格
        String[] columnNames = {"图片", "ID", "名称", "类别", "价格", "描述", "状态"};
        myItemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                // 设置第一列为 ImageIcon 类型，用于显示图片
                return column == 0 ? ImageIcon.class : Object.class;
            }
        };
        myItemsTable = new JTable(myItemsTableModel);
        myItemsTable.getColumnModel().getColumn(0).setPreferredWidth(80); // 设置图片列宽度
        JScrollPane scrollPane = new JScrollPane(myItemsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("发布物品");
        JButton editButton = new JButton("编辑物品");
        JButton deleteButton = new JButton("下架物品");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = myItemsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请先选择要编辑的物品！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int itemId = (int) myItemsTableModel.getValueAt(selectedRow, 1); // ID在第2列
                String status = (String) myItemsTableModel.getValueAt(selectedRow, 6); // 状态在第7列

                // 已售出的物品不能编辑
                if ("已售出".equals(status)) {
                    JOptionPane.showMessageDialog(MainFrame.this, "已售出的物品不能编辑！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 获取当前物品信息
                Item item = new Item();
                item.setItemId(itemId);
                item.setItemName((String) myItemsTableModel.getValueAt(selectedRow, 2)); // 名称在第3列
                item.setCategory((String) myItemsTableModel.getValueAt(selectedRow, 3)); // 类别在第4列
                item.setPrice((Double) myItemsTableModel.getValueAt(selectedRow, 4)); // 价格在第5列
                item.setDescription((String) myItemsTableModel.getValueAt(selectedRow, 5)); // 描述在第6列
                item.setStatus(status);
                item.setImagePath(getItemImagePath(itemId)); // 获取图片路径

                // 显示编辑对话框
                showEditItemDialog(item);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = myItemsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请先选择要下架的物品！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int itemId = (int) myItemsTableModel.getValueAt(selectedRow, 1); // ID在第2列
                String status = (String) myItemsTableModel.getValueAt(selectedRow, 6); // 状态在第7列

                if ("已售出".equals(status)) {
                    JOptionPane.showMessageDialog(MainFrame.this, "已售出的物品不能下架！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要下架该物品吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // 下架物品（更新状态为已下架）
                    if (removeItem(itemId)) {
                        JOptionPane.showMessageDialog(MainFrame.this, "物品下架成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadMyItems(); // 刷新物品列表
                        loadItems((String) sortComboBox.getSelectedItem()); // 刷新所有物品列表
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "物品下架失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建表格
        String[] columnNames = {"订单ID", "物品ID", "物品名称", "价格", "状态"};
        myOrdersTableModel = new DefaultTableModel(columnNames, 0);
        myOrdersTable = new JTable(myOrdersTableModel);
        JScrollPane scrollPane = new JScrollPane(myOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton payButton = new JButton("付款");

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = myOrdersTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MainFrame.this, "请先选择要付款的订单！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String status = (String) myOrdersTableModel.getValueAt(selectedRow, 4);
                if (!"待付款".equals(status)) {
                    JOptionPane.showMessageDialog(MainFrame.this, "只有待付款状态的订单可以付款！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int orderId = (int) myOrdersTableModel.getValueAt(selectedRow, 0);
                double price = (double) myOrdersTableModel.getValueAt(selectedRow, 3);

                int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要支付 " + price + " 元吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    if (payOrder(orderId)) {
                        JOptionPane.showMessageDialog(MainFrame.this, "付款成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadMyOrders(); // 刷新订单列表
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "付款失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        buttonPanel.add(payButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog(this, "发布物品", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加标题
        JLabel titleLabel = new JLabel("发布物品");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // 添加物品名称输入
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("物品名称:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // 添加类别输入
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("类别:"), gbc);
        gbc.gridx = 1;
        JTextField categoryField = new JTextField(20);
        panel.add(categoryField, gbc);

        // 添加价格输入
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("价格:"), gbc);
        gbc.gridx = 1;
        JTextField priceField = new JTextField(20);
        panel.add(priceField, gbc);

        // 添加描述输入
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1;
        JTextArea descriptionArea = new JTextArea(4, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane, gbc);

        // 添加图片选择按钮和路径显示
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("图片:"), gbc);
        gbc.gridx = 1;
        JButton selectImageButton = new JButton("选择图片");
        JTextField imagePathField = new JTextField(20);
        imagePathField.setEditable(false); // 设为不可编辑，只能通过按钮选择

        // 图片预览标签
        JLabel imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(100, 100));
        imagePreviewLabel.setBorder(BorderFactory.createEtchedBorder());
        imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePreviewLabel.setText("预览");

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // 设置文件过滤器，只允许选择图片文件
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "图片文件", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);
                
                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePathField.setText(selectedFile.getAbsolutePath());
                    
                    // 更新图片预览
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                        imagePreviewLabel.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "无法加载图片预览", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(selectImageButton, gbc);
        gbc.gridx = 2;
        panel.add(imagePathField, gbc);
        
        // 添加图片预览
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(imagePreviewLabel, gbc);

        // 添加按钮面板
        JPanel buttonPanel = new JPanel();
        JButton publishButton = new JButton("发布");
        JButton cancelButton = new JButton("取消");

        publishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                String priceStr = priceField.getText().trim();
                String description = descriptionArea.getText().trim();
                String imagePath = imagePathField.getText().trim();

                // 简单验证
                if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "物品名称、类别和价格不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    if (price <= 0) {
                        JOptionPane.showMessageDialog(dialog, "价格必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 创建物品对象
                    Item item = new Item();
                    item.setUserId(currentUser.getUserId());
                    item.setItemName(name);
                    item.setCategory(category);
                    item.setPrice(price);
                    item.setDescription(description);
                    item.setStatus("在售");
                    
                    // 处理图片路径
                    if (!imagePath.isEmpty()) {
                        // 确保图片目录存在
                        File imagesDir = new File("images");
                        if (!imagesDir.exists()) {
                            imagesDir.mkdir();
                        }
                        
                        // 生成唯一文件名
                        String fileName = System.currentTimeMillis() + "_" + 
                                new File(imagePath).getName();
                        String targetPath = "images/" + fileName;
                        
                        // 复制图片到项目目录
                        try {
                            File sourceFile = new File(imagePath);
                            File targetFile = new File(targetPath);
                            
                            // 复制文件
                            try (InputStream in = new FileInputStream(sourceFile);
                                 OutputStream out = new FileOutputStream(targetFile)) {
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = in.read(buffer)) > 0) {
                                    out.write(buffer, 0, length);
                                }
                            }
                            
                            // 保存相对路径到数据库
                            item.setImagePath(targetPath);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, "图片保存失败，但物品信息已保存", 
                                    "警告", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    // 发布物品
                    if (itemService.addItem(item)) {
                        JOptionPane.showMessageDialog(dialog, "物品发布成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadMyItems(); // 刷新我的物品列表
                        loadItems((String) sortComboBox.getSelectedItem()); // 刷新物品列表
                    } else {
                        JOptionPane.showMessageDialog(dialog, "物品发布失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "价格必须是数字！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(publishButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditItemDialog(Item item) {
        JDialog dialog = new JDialog(this, "编辑物品", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加标题
        JLabel titleLabel = new JLabel("编辑物品");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // 添加物品名称输入
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("物品名称:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setText(item.getItemName());
        panel.add(nameField, gbc);

        // 添加类别输入
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("类别:"), gbc);
        gbc.gridx = 1;
        JTextField categoryField = new JTextField(20);
        categoryField.setText(item.getCategory());
        panel.add(categoryField, gbc);

        // 添加价格输入
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("价格:"), gbc);
        gbc.gridx = 1;
        JTextField priceField = new JTextField(20);
        priceField.setText(String.valueOf(item.getPrice()));
        panel.add(priceField, gbc);

        // 添加描述输入
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1;
        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setText(item.getDescription());
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane, gbc);

        // 添加图片选择按钮和路径显示
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("图片:"), gbc);
        gbc.gridx = 1;
        JButton selectImageButton = new JButton("选择图片");
        JTextField imagePathField = new JTextField(20);
        imagePathField.setEditable(false); // 设为不可编辑，只能通过按钮选择
        
        // 设置当前图片路径
        if (item.getImagePath() != null) {
            imagePathField.setText(item.getImagePath());
        }

        // 图片预览标签
        JLabel imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(100, 100));
        imagePreviewLabel.setBorder(BorderFactory.createEtchedBorder());
        imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // 显示当前图片预览
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            try {
                File imageFile = new File(item.getImagePath());
                if (imageFile.exists()) {
                    BufferedImage image = ImageIO.read(imageFile);
                    Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                    imagePreviewLabel.setText("");
                } else {
                    imagePreviewLabel.setText("图片不存在");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                imagePreviewLabel.setText("无法加载图片");
            }
        } else {
            imagePreviewLabel.setText("无图片");
        }

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // 设置文件过滤器，只允许选择图片文件
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "图片文件", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);
                
                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePathField.setText(selectedFile.getAbsolutePath());
                    
                    // 更新图片预览
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                        imagePreviewLabel.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "无法加载图片预览", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(selectImageButton, gbc);
        gbc.gridx = 2;
        panel.add(imagePathField, gbc);
        
        // 添加图片预览
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(imagePreviewLabel, gbc);

        // 添加按钮面板
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                String priceStr = priceField.getText().trim();
                String description = descriptionArea.getText().trim();
                String imagePath = imagePathField.getText().trim();

                // 简单验证
                if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "物品名称、类别和价格不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    if (price <= 0) {
                        JOptionPane.showMessageDialog(dialog, "价格必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 更新物品信息
                    item.setItemName(name);
                    item.setCategory(category);
                    item.setPrice(price);
                    item.setDescription(description);
                    
                    // 处理图片路径
                    if (!imagePath.isEmpty()) {
                        // 确保图片目录存在
                        File imagesDir = new File("images");
                        if (!imagesDir.exists()) {
                            imagesDir.mkdir();
                        }
                        
                        // 生成唯一文件名
                        String fileName = System.currentTimeMillis() + "_" + 
                                new File(imagePath).getName();
                        String targetPath = "images/" + fileName;
                        
                        // 复制图片到项目目录
                        try {
                            File sourceFile = new File(imagePath);
                            File targetFile = new File(targetPath);
                            
                            // 复制文件
                            try (InputStream in = new FileInputStream(sourceFile);
                                 OutputStream out = new FileOutputStream(targetFile)) {
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = in.read(buffer)) > 0) {
                                    out.write(buffer, 0, length);
                                }
                            }
                            
                            // 保存相对路径到数据库
                            item.setImagePath(targetPath);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, "图片保存失败，但物品信息已保存", 
                                    "警告", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    // 保存修改
                    if (updateItem(item)) {
                        JOptionPane.showMessageDialog(dialog, "物品信息更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadMyItems(); // 刷新我的物品列表
                        loadItems((String) sortComboBox.getSelectedItem()); // 刷新物品列表
                    } else {
                        JOptionPane.showMessageDialog(dialog, "物品信息更新失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "价格必须是数字！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadItems() {
        loadItems("默认排序");
        itemTable.setRowHeight(80);
    }

    private void loadItems(String sortOption) {
        // 清空表格
        itemTableModel.setRowCount(0);

        // 获取物品列表
        List<Item> items = itemService.getAllItems();

        int maxImageWidth = 80; // 默认最小宽度

        // 第一次遍历获取最大图片宽度
        for (Item item : items) {
            ImageIcon icon = getImageIcon(item.getImagePath());
            if (icon != null && icon.getIconWidth() > maxImageWidth) {
                maxImageWidth = icon.getIconWidth();
            }
        }

        // 设置图片列宽度为最大图片宽度
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(maxImageWidth + 20); // 加上一点边距

        // 添加数据到表格
        for (Item item : items) {
            ImageIcon icon = getImageIcon(item.getImagePath());
            Object[] row = {
                    icon,                  // 图片列
                    item.getItemId(),      // ID列
                    item.getItemName(),    // 名称列
                    item.getCategory(),    // 类别列
                    item.getPrice(),       // 价格列
                    item.getDescription()  // 描述列
            };
            itemTableModel.addRow(row);
        }
        itemTable.setRowHeight(80);
    }

    private void searchItems(String keyword) {
        searchItems(keyword, "默认排序");
    }

    private void searchItems(String keyword, String sortOption) {
        // 清空表格
        itemTableModel.setRowCount(0);

        // 从数据库搜索物品数据
        List<Item> items = searchItemsByKeyword(keyword, sortOption);

        // 添加数据到表格
        for (Item item : items) {
            ImageIcon icon = getImageIcon(item.getImagePath());
            Object[] row = {
                icon,                  // 图片列
                item.getItemId(),      // ID列
                item.getItemName(),    // 名称列
                item.getCategory(),    // 类别列
                item.getPrice(),       // 价格列
                item.getDescription()  // 描述列
            };
            itemTableModel.addRow(row);
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到包含关键词 \"" + keyword + "\" 的物品", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private ImageIcon getImageIcon(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    BufferedImage image = ImageIO.read(imageFile);

                    //  判断 image 是否为 null
                    if (image == null) {
                        System.err.println("无法识别图片格式：" + imagePath);
                        return new ImageIcon(getClass().getResource("/default-image.png")); // 返回默认图
                    }

                    return new ImageIcon(image.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 默认图片兜底
        return new ImageIcon(getClass().getResource("/default-image.png"));
    }

    private List<Item> searchItemsByKeyword(String keyword, String sortOption) {
        List<Item> items = new ArrayList<>();
        String orderBy = getOrderByClause(sortOption);
        String sql = "SELECT * FROM item WHERE status = '在售' AND (itemName LIKE ? OR description LIKE ? OR category LIKE ?)" + orderBy;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");

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
                    item.setImagePath(rs.getString("imagePath"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private String getOrderByClause(String sortOption) {
        switch (sortOption) {
            case "价格升序":
                return " ORDER BY price ASC";
            case "价格降序":
                return " ORDER BY price DESC";
            default:
                return "";
        }
    }

    private void loadMyItems() {
        // 清空表格
        myItemsTableModel.setRowCount(0);

        // 从数据库加载我的物品数据
        List<Item> myItems = getItemByUserId(currentUser.getUserId());

        // 添加数据到表格
        for (Item item : myItems) {
            ImageIcon icon = getImageIcon(item.getImagePath());
            Object[] row = {
                icon,                  // 图片列
                item.getItemId(),      // ID列
                item.getItemName(),    // 名称列
                item.getCategory(),    // 类别列
                item.getPrice(),       // 价格列
                item.getDescription(), // 描述列
                item.getStatus()       // 状态列
            };
            myItemsTableModel.addRow(row);
        }
        myItemsTable.setRowHeight(80);
    }

    private String getItemImagePath(int itemId) {
        String imagePath = null;
        String sql = "SELECT imagePath AS imagePath FROM item WHERE itemId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    imagePath = rs.getString("imagePath");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imagePath;
    }

    private List<Item> getItemByUserId(int userId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE userId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

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
                    item.setImagePath(rs.getString("imagePath"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void loadMyOrders() {
        // 清空表格
        myOrdersTableModel.setRowCount(0);

        // 从数据库加载我的订单数据
        List<Order> myOrders = getOrdersByUserId(currentUser.getUserId());

        // 添加数据到表格
        for (Order order : myOrders) {
            // 获取物品名称
            String itemName = getItemNameById(order.getItemId());

            Object[] row = {
                    order.getOrderId(),
                    order.getItemId(),
                    itemName,
                    order.getTransactionPrice(),
                    order.getOrderStatus()
            };
            myOrdersTableModel.addRow(row);
        }
    }

    private List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `order` WHERE userId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("orderId"));
                    order.setUserId(rs.getInt("userId"));
                    order.setItemId(rs.getInt("itemId"));
                    order.setOrderStatus(rs.getString("orderstatus"));
                    order.setTransactionPrice(rs.getDouble("transactionprice"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private String getItemNameById(int itemId) {
        String itemName = "未知物品";
        String sql = "SELECT itemName FROM item WHERE itemId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    itemName = rs.getString("itemName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return itemName;
    }

    private boolean updateItem(Item item) {
        String sql = "UPDATE item SET itemName = ?, category = ?, price = ?, description = ?, imagePath = ? WHERE itemId = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getCategory());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setString(4, item.getDescription());
            pstmt.setString(5, item.getImagePath());
            pstmt.setInt(6, item.getItemId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeItem(int itemId) {
        String sql = "UPDATE item SET status = '已下架' WHERE itemId = ? AND status = '在售'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean payOrder(int orderId) {
        // 开始事务
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 更新订单状态为已完成
            String sql1 = "UPDATE `order` SET orderstatus = '已完成' WHERE orderId = ? AND orderstatus = '待付款'";
            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
                pstmt1.setInt(1, orderId);
                int rowsAffected = pstmt1.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 提交事务
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
