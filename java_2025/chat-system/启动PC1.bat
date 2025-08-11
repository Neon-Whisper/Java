@echo off
title 跨网络通讯系统 - NET1分公司(PC1)
color 0B

set JAR_PATH=target\chat-system-shaded.jar

if not exist %JAR_PATH% (
    echo JAR包不存在，请先编译项目！
    pause
    exit
)

rem 启动客户端（参数：-id PC1 -ip 127.0.0.1 -port 8888）
java -cp %JAR_PATH% client.ChatClient -id PC1 -ip 127.0.0.1 -port 8888

pause