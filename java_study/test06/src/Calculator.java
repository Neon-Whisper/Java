import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JFrame {
    private JTextField display;
    private String[] buttonLabels = {
            "←", "CE", "C", "+",
            "7", "8", "9", "-",
            "4", "5", "6", "*",
            "1", "2", "3", "/",
            "+/-", "0", ".", "="
    };
    private double num1 = 0, num2 = 0;
    private String operator = "";

    public Calculator() {
        // 设置窗口属性
        setTitle("计算器");
        setSize(200, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建显示器
        display = new JTextField();
        display.setEditable(false);
        add(display, BorderLayout.NORTH);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4));
        add(buttonPanel, BorderLayout.CENTER);

        // 创建按钮并添加到面板
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "←":
                    backspace();
                    break;
                case "CE":
                    clearEntry();
                    break;
                case "C":
                    clearAll();
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    setOperator(command);
                    break;
                case "=":
                    calculate();
                    break;
                case "+/-":
                    changeSign();
                    break;
                default:
                    if (Character.isDigit(command.charAt(0)) || command.equals(".")) {
                        appendToDisplay(command);
                    }
            }
        }
    }

    private void backspace() {
        String text = display.getText();
        if (!text.isEmpty()) {
            display.setText(text.substring(0, text.length() - 1));
        }
    }

    private void clearEntry() {
        display.setText("");
    }

    private void clearAll() {
        display.setText("");
        num1 = 0;
        num2 = 0;
        operator = "";
    }

    private void setOperator(String op) {
        if (!display.getText().isEmpty()) {
            num1 = Double.parseDouble(display.getText());
            operator = op;
            display.setText("");
        }
    }

    private void calculate() {
        if (!display.getText().isEmpty()) {
            num2 = Double.parseDouble(display.getText());
            double result = 0;
            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 != 0) {
                        result = num1 / num2;
                    } else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }
            display.setText(String.valueOf(result));
        }
    }

    private void changeSign() {
        String text = display.getText();
        if (!text.isEmpty()) {
            double value = Double.parseDouble(text);
            display.setText(String.valueOf(-value));
        }
    }

    private void appendToDisplay(String text) {
        display.setText(display.getText() + text);
    }

}
