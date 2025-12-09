package com.neon.knowledge.trigger.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MCPServerCSDNJob {

    @Resource
    private ChatClient chatClient;

    @Scheduled(cron = "0 0 * * * ?")
    public void exec() {
        // 检查当前时间是否在允许执行的时间范围内（8点到23点之间）
        int currentHour = java.time.LocalDateTime.now().getHour();
        if (currentHour >= 23 || currentHour < 8) {
            log.info("当前时间 {}点 不在任务执行时间范围内，跳过执行", currentHour);
            return;
        }

        try {
            // 第一次调用：生成面试场景和角色设定
            String scenePrompt = """
            生成一个互联网大厂Java求职者面试场景，包含：
            1. 一位严肃的面试官
            2. 一位搞笑但技术基础薄弱的程序员"谢飞机"
            3. 设定为3轮面试，每轮1-2个问题
            """;
            String sceneResult = chatClient.prompt(scenePrompt).call().content();
            log.info("场景生成结果: {}", sceneResult);

            // 第二次调用：指定技术栈范围
            String techStackPrompt = """
            面试涉及的技术栈包括：
            - Web框架: Spring Boot, Spring MVC, Spring WebFlux等
            - 数据库与ORM: Hibernate, MyBatis, JPA等
            """;
            String techStackResult = chatClient.prompt(techStackPrompt).call().content();
            log.info("技术栈生成结果: {}", techStackResult);

            // 第三次调用：指定业务场景
            String businessScenarioPrompt = """
            面试业务场景包括：电商、在线教育、支付金融、智慧城市、物联网等。
            要求问题有业务连贯性和技术递进性。
            """;
            String businessScenarioResult = chatClient.prompt(businessScenarioPrompt).call().content();
            log.info("业务场景生成结果: {}", businessScenarioResult);

            // 第四次调用：指定输出格式要求
            String formatPrompt = """
            将上面的面试整理成一篇文章
            输出格式要求：
            - 文章标题（需包含技术点）
            - 文章内容（含3轮问答及答案解析）
            - 文章标签（多个用英文逗号隔开）
            - 文章简述（100字以内）
            """;
            String formatResult = chatClient.prompt(formatPrompt).call().content();
            log.info("格式要求生成结果: {}", formatResult);

            // TODO: 调用MCP发布文章
            String userInput = "将下面的文章发布文章到CSDN：";
            userInput += formatResult;
            log.info("执行结果:{} {}", userInput, chatClient.prompt(userInput).call().content());
        } catch (Exception e) {
            log.error("定时任务执行失败", e);
        }
    }


}
