package server;

import common.Constants;
import common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * 聊天服务器主类
 * 功能：监听客户端连接，处理消息转发和文件传输
 */
public class ChatServer {
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());
    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Set<ClientHandler> clientHandlers;
    private InetAddress serverAddress;

    public ChatServer(int port, String serverIp) {
        this.port = port;
        try {
            this.serverAddress = InetAddress.getByName(serverIp);
            this.clientHandlers = Collections.synchronizedSet(new CopyOnWriteArraySet<>());
            this.executorService = Executors.newCachedThreadPool();
            logger.info("服务器初始化，IP: " + serverIp + ", 端口: " + port);
        } catch (IOException e) {
            logger.severe("服务器初始化失败: " + e.getMessage());
        }
    }

    /**
     * 启动服务器
     */
// ChatServer.java 修改启动方法
    public void start() {
        try {
            // 使用Inet6Address绑定IPv6地址
            Inet6Address ipv6Address = (Inet6Address) InetAddress.getByName("::");
            serverSocket = new ServerSocket(port, 50, ipv6Address);
            logger.info("服务器启动成功，监听IPv6地址 :::" + port);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                InetAddress clientAddress = clientSocket.getInetAddress();
                String clientIp = clientAddress.getHostAddress();

                // 判断分公司（IPv6为NET2）
                String company = clientAddress instanceof Inet6Address ?
                        Constants.COMPANY_NET2 : Constants.COMPANY_NET1;
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clientHandlers.add(handler);
                executorService.submit(handler);
            }
        } catch (IOException e) {
            logger.severe("服务器启动失败: " + e.getMessage());
        }
    }

    /**
     * 广播消息给所有客户端
     */
    public void broadcastMessage(Message message) {
        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(message);
        }
    }

    /**
     * 移除客户端处理器
     */
    public void removeHandler(ClientHandler handler) {
        clientHandlers.remove(handler);
        logger.info("客户端断开连接，剩余连接数: " + clientHandlers.size());
    }

    /**
     * 客户端处理器内部类
     */
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ChatServer server;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private String clientId;
        private String clientCompany;

        public ClientHandler(Socket socket, ChatServer server) {
            this.socket = socket;
            this.server = server;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                // 根据客户端IP地址类型初始化 clientCompany
                InetAddress clientAddress = socket.getInetAddress();
                this.clientCompany = clientAddress instanceof Inet6Address ?
                        Constants.COMPANY_NET2 : Constants.COMPANY_NET1;
            } catch (IOException e) {
                logger.severe("客户端处理器初始化失败: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                // 接收客户端标识
                Message initMsg = (Message) ois.readObject();
                this.clientId = initMsg.getSenderId();

                // 获取客户端IP地址
                InetAddress clientAddress = socket.getInetAddress();
                String clientIp = clientAddress.getHostAddress();

                // 记录客户端连接日志（包含IP类型）
                logger.info("新客户端连接: " + clientIp +
                        " (" + clientCompany + "), 地址类型: " +
                        (clientAddress instanceof Inet6Address ? "IPv6" : "IPv4"));

                // 发送连接通知给所有客户端
                Message notification = new Message(
                        Constants.TYPE_NOTIFICATION,
                        "SERVER",
                        "系统",
                        clientId + "(" + clientCompany + ") 已加入聊天"
                );
                server.broadcastMessage(notification);

                // 处理客户端消息
                while (!socket.isClosed()) {
                    Message message = (Message) ois.readObject();
                    logger.info(clientId + "(" + clientCompany + "): " + message.getContent());

                    // 转发消息给所有客户端
                    server.broadcastMessage(message);

                    // 如果是文件消息，记录文件接收
                    if (message.getType() == Constants.TYPE_FILE) {
                        logger.info("接收到文件: " + message.getFileName() +
                                ", 大小: " + message.getFileSize() + "字节");
                    } else if (message.getContent().startsWith("文件传输结束: ")) {
                        logger.info("文件传输结束: " + message.getContent().substring(6));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                // 记录详细的异常信息
                logger.severe("客户端 " + clientId + "(" + clientCompany + ") 发生异常: " + e.getMessage());
                try {
                    if (socket != null) socket.close();
                    server.removeHandler(this);
                    Message offlineMsg = new Message(
                            Constants.TYPE_NOTIFICATION,
                            "SERVER",
                            "系统",
                            clientId + "(" + clientCompany + ") 已离开聊天"
                    );
                    server.broadcastMessage(offlineMsg);
                } catch (IOException ex) {
                    logger.severe("客户端断开连接处理失败: " + ex.getMessage());
                }
            }
        }

        /**
         * 发送消息给客户端
         */
        public void sendMessage(Message message) {
            try {
                if (oos != null) {
                    oos.writeObject(message);
                    oos.flush();
                }
            } catch (IOException e) {
                logger.severe("发送消息失败: " + e.getMessage());
            }
        }
    }
    /**
     * 服务器主方法
     */
    public static void main(String[] args) {
        String serverIp = args.length > 0 ? args[0] : Constants.DEFAULT_SERVER_IP;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : Constants.DEFAULT_SERVER_PORT;

        ChatServer server = new ChatServer(port, serverIp);
        server.start();
    }
}