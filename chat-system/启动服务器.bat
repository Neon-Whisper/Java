@echo off
title 跨网络通讯系统 - 服务器
color 0A

rem 获取JAR包绝对路径
set JAR_PATH=target\chat-system-shaded.jar

rem 检查JAR包是否存在
if not exist %JAR_PATH% (
    echo JAR包不存在，请先编译项目！
    pause
    exit
)

rem 启动服务器（参数：IP 端口）
java -cp %JAR_PATH% server.ChatServer 127.0.0.1 8888

pause