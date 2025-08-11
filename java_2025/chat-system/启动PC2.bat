@echo off
title 跨网络通讯系统 - NET2分公司(IPv6)
color 0C

set JAR_PATH=target\chat-system-shaded.jar

if not exist %JAR_PATH% (
    echo JAR包不存在，请先编译项目！
    pause
    exit
)

rem 启动客户端（添加IPv6参数）
java "-Djava.net.preferIPv6Addresses=true" "-Djava.net.preferIPv4Stack=false" -cp %JAR_PATH% client.ChatClient -id PC2 -ip ::1 -port 8888

pause