package common;


/**
 * 系统常量定义
 */
public class Constants {
    // 消息类型
    public static final int TYPE_TEXT = 1;     // 文本消息
    public static final int TYPE_FILE = 2;     // 文件消息
    public static final int TYPE_NOTIFICATION = 3; // 通知消息

    // 系统默认参数
    public static final String DEFAULT_SERVER_IP = "202.112.20.132"; // 公网IP（模拟）
    public static final int DEFAULT_SERVER_PORT = 8888;
    public static final int FILE_BUFFER_SIZE = 1024; // 文件传输缓冲区大小

    // 分公司标识
    public static final String COMPANY_NET1 = "NET1";
    public static final String COMPANY_NET2 = "NET2";
}