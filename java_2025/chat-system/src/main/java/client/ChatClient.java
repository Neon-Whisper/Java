package client;

import common.Constants;
import common.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * 聊天客户端主类
 * 功能：图形界面、消息发送接收、文件传输（支持IPv4/IPv6）
 */
public class ChatClient {
    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());
    private final String clientId;
    private final String serverIp;
    private final int serverPort;
    private final String clientCompany;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean isConnected;

    // 界面组件
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton fileButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public ChatClient(String clientId, String serverIp, int serverPort) {
        this.clientId = clientId;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        // 根据IP类型判断分公司（IPv6为NET2）
        this.clientCompany = isIPv6Address(serverIp) ? Constants.COMPANY_NET2 : Constants.COMPANY_NET1;
        this.isConnected = false;
    }

    // 判断是否为IPv6地址
    private boolean isIPv6Address(String ip) {
        return ip.contains(":") && !ip.contains(".");
    }

    // 初始化并连接服务器
    public void connect() {
        try {
            InetAddress serverAddress = InetAddress.getByName(serverIp);
            // 强制使用IPv6（如果目标是IPv6地址）
            if (serverAddress instanceof Inet6Address) {
                System.setProperty("java.net.preferIPv6Addresses", "true");
                System.setProperty("java.net.preferIPv4Stack", "false");
                logger.info("启用IPv6优先模式");
            }

            socket = new Socket(serverAddress, serverPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            logger.info(clientId + "(" + clientCompany + ") 连接到服务器: " + serverIp + ":" + serverPort +
                    ", 本地IP: " + InetAddress.getLocalHost().getHostAddress());

            // 获取本地IP地址并输出
            InetAddress localAddress = InetAddress.getLocalHost();
            String ipType = localAddress instanceof Inet6Address ? "IPv6" : "IPv4";
            logger.info("本地IP: " + localAddress.getHostAddress() + ", 类型: " + ipType);

            // 发送初始化消息
            Message initMsg = new Message(
                    Constants.TYPE_NOTIFICATION,
                    clientId,
                    clientCompany,
                    "已连接到服务器 (" + (isIPv6Address(serverIp) ? "IPv6" : "IPv4") + ")"
            );
            sendMessage(initMsg);

            // 启动消息接收线程
            new Thread(this::receiveMessages).start();

            // 初始化界面
            initUI();

        } catch (UnknownHostException e) {
            showMessageDialog("服务器IP地址错误: " + e.getMessage());
        } catch (IOException e) {
            showMessageDialog("连接服务器失败: " + e.getMessage());
        }
    }

    /**
     * 接收服务器消息线程
     */
    private void receiveMessages() {
        try {
            while (isConnected) {
                Message message = (Message) ois.readObject();
                SwingUtilities.invokeLater(() -> displayMessage(message));

                // 如果是文件消息，保存文件
                if (message.getType() == Constants.TYPE_FILE) {
                    saveReceivedFile(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (isConnected) {
                SwingUtilities.invokeLater(() -> {
                    showMessageDialog("与服务器断开连接: " + e.getMessage());
                    disconnect();
                });
            }
        }
    }

    /**
     * 显示消息到聊天区域
     */
    private void displayMessage(Message message) {
        String senderPrefix = message.getSenderCompany().equals(clientCompany) ?
                "【本地分公司】" : "【远程分公司】";
        String sender = senderPrefix + message.getSenderId();

        String content;
        if (message.getType() == Constants.TYPE_FILE) {
            content = "接收文件: " + message.getFileName() +
                    " (" + formatFileSize(message.getFileSize()) + ")";
        } else {
            content = message.getContent();
        }

        chatArea.append(sender + ": " + content + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /**
     * 初始化用户界面
     */
    private void initUI() {
        frame = new JFrame(clientCompany + " - " + clientId);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // 设置界面背景色（区分分公司）
        Color bgColor = clientCompany.equals(Constants.COMPANY_NET1) ?
                new Color(227, 242, 253) : new Color(232, 245, 233);
        frame.getContentPane().setBackground(bgColor);

        // 聊天区域
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // 消息输入区域
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        messageField = new JTextField();
        sendButton = new JButton("发送");
        fileButton = new JButton("发送文件");

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(fileButton, BorderLayout.WEST);
        inputPanel.setBackground(bgColor);

        // 进度条和状态标签
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        statusLabel = new JLabel("状态: 连接中...");
        statusLabel.setForeground(clientCompany.equals(Constants.COMPANY_NET1) ?
                Color.BLUE : Color.GREEN);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.EAST);
        statusPanel.setBackground(bgColor);

        // 组装界面
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                chatScrollPane,
                inputPanel
        );
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(5);

        frame.setLayout(new BorderLayout());
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // 按钮事件监听
        sendButton.addActionListener(e -> sendTextMessage());
        fileButton.addActionListener(e -> sendFile());

        // 窗口关闭事件
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                frame.dispose();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;

        Message msg = new Message(
                Constants.TYPE_TEXT,
                clientId,
                clientCompany,
                message
        );
        sendMessage(msg);
        messageField.setText("");
    }

    /**
     * 发送文件
     */
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file != null && file.exists()) {
                new Thread(() -> sendFileAsync(file)).start();
            }
        }
    }

    /**
     * 异步发送文件（避免UI卡顿）
     */
    private void sendFileAsync(File file) {
        try {
            // 显示发送进度
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(0);
                statusLabel.setText("状态: 发送文件 " + file.getName() + "...");
            });

            // 发送文件头信息
            Message fileHeader = new Message(
                    Constants.TYPE_FILE,
                    clientId,
                    clientCompany,
                    file.getAbsolutePath(),
                    file.length(),
                    file.getName()
            );
            sendMessage(fileHeader);

            // 分块发送文件数据
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] buffer = new byte[Constants.FILE_BUFFER_SIZE];
                int bytesRead;
                long totalBytes = 0;
                long fileSize = file.length();

                while ((bytesRead = bis.read(buffer)) != -1) {
                    socket.getOutputStream().write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;

                    // 更新进度条
                    final int progress = (int) (totalBytes * 100 / fileSize);
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                }
                socket.getOutputStream().flush(); // 确保数据全部发送
            }


            // 发送文件传输结束消息
            Message fileEndMessage = new Message(
                    Constants.TYPE_NOTIFICATION,
                    clientId,
                    clientCompany,
                    "文件传输结束: " + file.getName()
            );
            sendMessage(fileEndMessage);

            // 发送完成
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                statusLabel.setText("状态: 文件发送完成");
                showMessageDialog("文件发送成功: " + file.getName());
            });

        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                showMessageDialog("文件发送失败: " + e.getMessage());
                statusLabel.setText("状态: 文件发送失败");
            });
            logger.severe("文件发送失败: " + e.getMessage());
            // 不在这里断开连接，避免异常导致连接中断
        }
    }
    /**
     * 保存接收到的文件
     */
    private void saveReceivedFile(Message message) {
        try {
            String fileName = message.getFileName();
            long fileSize = message.getFileSize();
            String savePath = "received_files/" + fileName;

            // 创建保存目录
            File dir = new File("received_files");
            if (!dir.exists()) {
                dir.mkdir();
            }

            // 保存文件
            try (FileOutputStream fos = new FileOutputStream(savePath);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                byte[] buffer = new byte[Constants.FILE_BUFFER_SIZE];
                int bytesRead;
                long totalBytes = 0;

                while (totalBytes < fileSize && (bytesRead = ois.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                bos.flush(); // 确保数据全部写入文件
            }

            SwingUtilities.invokeLater(() -> {
                showMessageDialog("文件保存成功: " + savePath);
                statusLabel.setText("状态: 已接收文件 " + fileName);
            });

        } catch (IOException e) {
            SwingUtilities.invokeLater(() ->
                    showMessageDialog("文件保存失败: " + e.getMessage())
            );
            logger.severe("文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 发送消息到服务器
     */
    private void sendMessage(Message message) {
        if (isConnected && oos != null) {
            try {
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                logger.severe("发送消息失败: " + e.getMessage());
            }
        }
    }

    /**
     * 断开连接
     */
    private void disconnect() {
        if (isConnected) {
            try {
                isConnected = false;
                if (oos != null) oos.close();
                if (ois != null) ois.close();
                if (socket != null) socket.close();
                logger.info(clientId + "(" + clientCompany + ") 断开连接");

                // 发送离线通知
                Message offlineMsg = new Message(
                        Constants.TYPE_NOTIFICATION,
                        clientId,
                        clientCompany,
                        "已断开连接"
                );
                sendMessage(offlineMsg);
            } catch (IOException e) {
                logger.severe("断开连接失败: " + e.getMessage());
            }
        }
    }

    /**
     * 显示提示对话框
     */
    private void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "系统提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * 客户端主方法
     */
    public static void main(String[] args) {
        String clientId = "PC1";
        String serverIp = "127.0.0.1";
        int serverPort = Constants.DEFAULT_SERVER_PORT;

        // 解析命令行参数
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                if (args[i].equals("-id")) clientId = args[i+1];
                if (args[i].equals("-ip")) serverIp = args[i+1];
                if (args[i].equals("-port")) serverPort = Integer.parseInt(args[i+1]);
            }
        }

        ChatClient client = new ChatClient(clientId, serverIp, serverPort);
        client.connect();
    }
}