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
                请生成一个Java面试场景：
                角色：严肃的面试官 vs 搞笑程序员"谢飞机"
                技术栈：Spring Boot, MyBatis, Redis, Kafka
                业务场景：电商平台订单处理系统
                要求：3轮问答，每轮2个问题，问题要有递进性
                回答特点：谢飞机简单问题能答对，复杂问题含糊其辞
                只输出面试对话内容，不要其他说明
                """;

            String interviewResult = chatClient.prompt(interviewPrompt).call().content();
            log.info("#######面试内容生成结果: {}", interviewResult);

            // 第二阶段：格式化为文章
            String formatPrompt = """
                请将以下面试内容整理成技术博客文章，严格按照JSON格式输出：
                {
                  "title": "文章标题（包含具体技术点）",
                  "markdowncontent": "文章内容（包含3轮问答和答案解析，使用Markdown格式）",
                  "tags": "Java,Spring Boot,Redis,Kafka,面试",
                  "Description": "文章简述（100字内，概括技术要点）"
                }
                
                面试内容：
                """ + interviewResult;

            log.info("#########格式化为文章");
            String articleResult = chatClient.prompt(formatPrompt).call().content();
            log.info("#######文章生成结果: {}", articleResult);

            // 第三阶段：直接调用工具而非依赖AI自动调用
            String publishPrompt = """
                请调用 saveArticle 工具发布以下文章到CSDN：
                """ + articleResult;

            log.info("#########发布文章到CSDN");
            // 明确启用工具调用
            String finalResult = chatClient.prompt(publishPrompt)
                    .functions("saveArticle")
                    .call()
                    .content();
            log.info("#######发布结果: {}", finalResult);
        } catch (Exception e) {
            log.error("#######执行失败", e);
        }
    }

}
