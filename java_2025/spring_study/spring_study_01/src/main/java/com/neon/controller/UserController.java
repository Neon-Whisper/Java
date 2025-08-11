package com.neon.controller;

import cn.hutool.core.io.IoUtil;
import com.neon.Pojo.User;
import com.neon.service.UserService;
import com.neon.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

//    @RequestMapping("/list")
//    public List<User> list() throws Exception {
//        // 加载，读取文件
////      InputStream in = new FileInputStream(new File("user.txt"));
//
//        InputStream in = this.getClass().getClassLoader().getResourceAsStream("user.txt");
//        ArrayList<String> lines = IoUtil.readLines(in, StandardCharsets.UTF_8, new ArrayList<>());
//
//        // 解析，封装成对象
//        List<User> userlist = lines.stream().map(line -> {
//            String[] parts = line.split(",");
//            Integer id = Integer.parseInt(parts[0]);
//            String username = parts[1];
//            String password = parts[2];
//            String name = parts[3];
//            Integer age = Integer.parseInt(parts[4]);
//            LocalDateTime updateTime = LocalDateTime.parse(parts[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            return new User(id, username, password, name, age, updateTime);
//        }).toList();
//
//        // 响应
//        return userlist;
//    }

    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    public List<User> list() throws Exception {

        List<User> userList = userService.getUserList();
        return userList;

    }

}
