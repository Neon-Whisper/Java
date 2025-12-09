package ui;

import entity.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox; // 添加角色选择框
    private UserService userService = new UserService();

    public RegisterFrame() {
        setTitle("大学生二手货市场管理系统 - 注册");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建面板
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加标题
        JLabel titleLabel = new JLabel("用户注册");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // 添加用户名输入
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        // 添加密码输入
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // 添加确认密码输入
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("确认密码:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);

        // 添加手机号输入
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("手机号:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        panel.add(phoneField, gbc);

        // 添加角色选择
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("角色:"), gbc);
        gbc.gridx = 1;
        String[] roles = {"普通用户", "管理员"};
        roleComboBox = new JComboBox<>(roles);
        panel.add(roleComboBox, gbc);

        // 添加按钮面板
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("注册");
        JButton cancelButton = new JButton("取消");

        // 注册按钮事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String phone = phoneField.getText();
                String role = (String) roleComboBox.getSelectedItem(); // 获取选择的角色

                // 简单验证
                if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "所有字段都必须填写！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 创建用户对象
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setPhone(phone);
                user.setRole(role);

                // 注册用户
                if (userService.register(user)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "注册成功！请登录");
                    dispose(); // 关闭注册窗口
                    new LoginFrame().setVisible(true); // 打开登录窗口
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "注册失败，用户名可能已存在！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 取消按钮事件
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭注册窗口
                new LoginFrame().setVisible(true); // 打开登录窗口
            }
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }
}