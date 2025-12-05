package com.neon.knowledge.test;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MCPTest {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ToolCallbackProvider tools;

    @Test
    public void test_tool() {
        String userInput = "有哪些工具可以使用";
        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OllamaOptions.builder()
                        .model("qwen2.5:7b")
                        .build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void test() {
//        String userInput = "在 D:\\BaiduSyncdisk\\code\\java\\java_2025\\ai-mcp-knowledge\\mcp-test 文件夹下创建 电脑.txt";

        String userInput = "在 D:\\BaiduSyncdisk\\code\\java\\java_2025\\ai-mcp-knowledge\\mcp-test 文件夹下创建 电脑.txt，在 电脑.txt 写入“笔记本电脑”。如果在创建文件夹时遇到了错误，请告诉我是什么错误";
//        String userInput = "请用中文回答并按以下步骤执行：" +
//                "1. 获取电脑配置" +
//                "2. 在 D:\\BaiduSyncdisk\\code\\java\\java_2025\\ai-mcp-knowledge\\mcp-test 文件夹下创建 电脑.txt" +
//                "3. 将第1步获取的电脑配置信息简洁写入 电脑.txt" +
//                "请确保按顺序完整执行所有步骤";

        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OllamaOptions.builder()
                        .model("qwen2.5:7b")
                        .build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void testStepByStep() {
        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OllamaOptions.builder()
                        .model("qwen2.5:7b")
                        .build())
                .build();

        // 第一步：获取简化系统信息
        String step1 = "获取电脑的基本配置信息（只需要CPU、内存、操作系统版本）";
        System.out.println("\n>>> STEP 1 - QUESTION: " + step1);
        String systemInfo = chatClient.prompt(step1).call().content();
        System.out.println(">>> STEP 1 - RESULT: " + systemInfo);

        // 第二步：创建文件
        String step2 = "确保 D:\\BaiduSyncdisk\\code\\java\\java_2025\\ai-mcp-knowledge\\mcp-test 目录存在，并在该目录下创建 电脑.txt";
        System.out.println("\n>>> STEP 2 - QUESTION: " + step2);
        String createResult = chatClient.prompt(step2).call().content();
        System.out.println(">>> STEP 2 - RESULT: " + createResult);

        // 第三步：写入内容
        String step3 = "将以下内容写入 D:\\BaiduSyncdisk\\code\\java\\java_2025\\ai-mcp-knowledge\\mcp-test\\电脑.txt 文件：" + systemInfo;
        System.out.println("\n>>> STEP 3 - QUESTION: " + step3);
        String writeResult = chatClient.prompt(step3).call().content();
        System.out.println(">>> STEP 3 - RESULT: " + writeResult);

        System.out.println("\n=== 分步执行完成 ===");
    }


}
