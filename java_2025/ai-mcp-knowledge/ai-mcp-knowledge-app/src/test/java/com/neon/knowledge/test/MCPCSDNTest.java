package com.neon.knowledge.test;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MCPCSDNTest {
    @Resource
    private ChatClient chatClient;

    @Test
    public void exec() {
        try {
            // 第一阶段：生成完整面试对话
            log.info("#######生成面试对话");
            String interviewPrompt = """
        请生成一个互联网大厂Java面试场景：
        角色：严肃的面试官 vs 搞笑程序员"谢飞机"
        技术栈：Spring Boot, MyBatis, Redis, Kafka等
        业务场景：电商平台订单处理系统
        要求：3轮问答，每轮2个问题，问题要有递进性
        回答特点：谢飞机简单问题能答对，复杂问题含糊其辞
        """;

            String interviewResult = chatClient.prompt(interviewPrompt).call().content();
            log.info("#######面试内容生成结果: {}", interviewResult);

            // 第二阶段：格式化为文章
            String formatPrompt = """
        基于以下面试内容，整理成一篇技术博客文章，先别着急发送到csdn：
        1. 文章标题（包含技术点）
        2. 文章正文（包含3轮问答+答案解析）
        3. 文章标签（英文逗号分隔）
        4. 文章简介（100字内）
        
        面试内容：
        """ + interviewResult;

            log.info("#########格式化为文章");
            String articleResult = chatClient.prompt(formatPrompt).call().content();
            log.info("#######文章生成结果: {}", articleResult);

            // 第三阶段：发布文章到CSDN
            String publishPrompt = """
        请将以下文章发布到CSDN平台：
        
        """ + articleResult + """
        
        发布文章到CSDN
        """;

            log.info("#########发布文章到CSDN");
            String finalResult = chatClient.prompt(publishPrompt).call().content();
            log.info("#######发布结果: {}", finalResult);

        } catch (Exception e) {
            log.error("#######执行失败", e);
        }
    }

}
