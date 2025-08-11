import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CountdownTimerUI extends JFrame {
    private JTextField hoursField, minutesField, secondsField;
    private JLabel countdownLabel;
    private Timer timer;
    private int totalSeconds;
    private int remainingSeconds;
    private JButton startButton, pauseButton, resetButton;
    private boolean isPaused = false;
    private Clip currentClip = null;


    public CountdownTimerUI() {
        setTitle("简易倒计时提醒器");
        setSize(450, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 输入面板
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        hoursField = new JTextField("0", 3);
        minutesField = new JTextField("0", 3);
        secondsField = new JTextField("10", 3);
        startButton = new JButton("开始");
        pauseButton = new JButton("暂停");
        resetButton = new JButton("重置");

        startButton.addActionListener(this::startCountdown);
        pauseButton.addActionListener(this::togglePause);
        resetButton.addActionListener(this::resetTimer);

        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);

        inputPanel.add(new JLabel("时:"));
        inputPanel.add(hoursField);
        inputPanel.add(new JLabel("分:"));
        inputPanel.add(minutesField);
        inputPanel.add(new JLabel("秒:"));
        inputPanel.add(secondsField);
        inputPanel.add(startButton);
        inputPanel.add(pauseButton);
        inputPanel.add(resetButton);

        // 倒计时显示面板
        countdownLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));

        add(inputPanel, BorderLayout.NORTH);
        add(countdownLabel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void startCountdown(ActionEvent e) {
        if (timer != null && timer.isRunning() && !isPaused) {
            return; // 正在运行中，不重复启动
        }

        if (!isPaused) { // 不是恢复暂停，而是新倒计时
            try {
                int hours = Integer.parseInt(hoursField.getText());
                int minutes = Integer.parseInt(minutesField.getText());
                int seconds = Integer.parseInt(secondsField.getText());

                if (hours < 0 || minutes < 0 || seconds < 0 || (hours == 0 && minutes == 0 && seconds == 0)) {
                    JOptionPane.showMessageDialog(this, "请输入有效的时间");
                    return;
                }

                totalSeconds = hours * 3600 + minutes * 60 + seconds;
                remainingSeconds = totalSeconds;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入数字！");
                return;
            }
        }

        updateDisplay();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);
        isPaused = false;

        // 启动/恢复倒计时
        timer = new Timer(1000, this::updateCountdown);
        timer.start();
    }

    private void togglePause(ActionEvent e) {
        if (timer == null) return;

        if (isPaused) {
            // 恢复倒计时
            timer.start();
            pauseButton.setText("暂停");
        } else {
            // 暂停倒计时
            timer.stop();
            pauseButton.setText("继续");
        }
        isPaused = !isPaused;
    }

    private void resetTimer(ActionEvent e) {
        if (timer != null) {
            timer.stop();
        }
        remainingSeconds = totalSeconds;
        updateDisplay();
        startButton.setEnabled(true);
        pauseButton.setText("暂停");
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
        isPaused = false;
    }

    private void updateCountdown(ActionEvent e) {
        remainingSeconds--;
        updateDisplay();

        if (remainingSeconds <= 0) {
            timer.stop();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            resetButton.setEnabled(false);
            showReminder();
        }
    }

    private void updateDisplay() {
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;

        countdownLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void showReminder() {
        playSound("ding.wav");

        JOptionPane.showMessageDialog(this,
                "倒计时结束！",
                "提醒",
                JOptionPane.INFORMATION_MESSAGE);

        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop(); // 停止播放
        }

        if (currentClip != null) {
            currentClip.close(); // 关闭资源
            currentClip = null;  // 清空引用
        }
    }


    private void playSound(String path) {
        new Thread(() -> {
            try {
                File file = new File("static/" + path);
                if (!file.exists()) {
                    System.err.println("找不到音效文件: " + file.getAbsolutePath());
                    return;
                }

                currentClip = AudioSystem.getClip(); // 赋值给成员变量
                currentClip.open(AudioSystem.getAudioInputStream(file));
                currentClip.start();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        new CountdownTimerUI();
    }
}