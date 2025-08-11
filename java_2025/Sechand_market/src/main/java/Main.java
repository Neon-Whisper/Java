import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // 设置界面风格为系统风格
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 启动登录窗口
        new LoginFrame().setVisible(true);
    }
}
