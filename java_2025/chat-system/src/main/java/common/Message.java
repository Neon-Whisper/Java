package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通讯消息协议类（可序列化）
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private int type;         // 消息类型（文本/文件/通知）
    private String senderId;  // 发送者ID
    private String senderCompany; // 发送者所属分公司
    private String content;   // 消息内容（文本或文件路径）
    private Map<String, Object> fileMeta = new HashMap<>(); // 文件元数据

    // 文本消息构造
    public Message(int type, String senderId, String senderCompany, String content) {
        this.type = type;
        this.senderId = senderId;
        this.senderCompany = senderCompany;
        this.content = content;
    }

    // 文件消息构造
    public Message(int type, String senderId, String senderCompany, String filePath,
                   long fileSize, String fileName) {
        this.type = type;
        this.senderId = senderId;
        this.senderCompany = senderCompany;
        this.content = filePath;
        fileMeta.put("size", fileSize);
        fileMeta.put("name", fileName);
    }

    // Getter & Setter
    public int getType() { return type; }
    public String getSenderId() { return senderId; }
    public String getSenderCompany() { return senderCompany; }
    public String getContent() { return content; }
    public Map<String, Object> getFileMeta() { return fileMeta; }
    public long getFileSize() { return (long) fileMeta.get("size"); }
    public String getFileName() { return (String) fileMeta.get("name"); }
}
