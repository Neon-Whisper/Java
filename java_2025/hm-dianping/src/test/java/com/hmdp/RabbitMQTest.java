package com.hmdp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@EnableRabbit// 添加运行器注解
public class RabbitMQTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage(){
        assertNotNull("RabbitTemplate should not be null", rabbitTemplate);
        rabbitTemplate.convertAndSend("hmdianping.direct","seckill.order","测试发送消息");
    }
}

