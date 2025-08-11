package ui;

import entity.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService = new UserService();

    public LoginFrame() {
        setTitle("大学生二手货市场管理系统 - 登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建面板
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加标题
        JLabel titleLabel = new JLabel("大学生二手货市场管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
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

        // 添加按钮面板
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");

        // 登录按钮事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                User user = userService.login(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "登录成功！");
                    dispose(); // 关闭登录窗口
                    if ("管理员".equals(user.getRole())) {
                        new AdminFrame(user).setVisible(true); // 打开管理员窗口
                    } else {
                        new MainFrame(user).setVisible(true); // 打开普通用户窗口
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 注册按钮事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭登录窗口
                new RegisterFrame().setVisible(true); // 打开注册窗口
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }
}
