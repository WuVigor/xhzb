package com.xhzb.nursing.config;

import com.xhzb.nursing.constants.SystemConstants;
import com.xhzb.nursing.service.impl.RedisChatMemoryService;
import com.xhzb.nursing.tool.NursingProjectTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    /**
     * 创建并返回一个ChatClient的Spring Bean实例。
     * @param openAiChatModel
     * @return
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, NursingProjectTool nursingProjectTool, RedisChatMemoryService redisChatMemoryService) {
        return ChatClient
                .builder(openAiChatModel)
                .defaultTools(nursingProjectTool)
                .defaultSystem(SystemConstants.prompt)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(redisChatMemoryService).build())
                .build();
    }

  /*  @Bean
    public ChatClient chatClient(OllamaChatModel openAiChatModel) {
        return ChatClient
                .builder(openAiChatModel)
//                .defaultSystem("您是一家名为“星海智伴”的职业养老机构的客户聊天助手，你的名字叫小智。请以友好、乐于助人和愉快的方式解答用户的各种问题。")
                .build();
    }*/

   /* @Bean
    public ChatClient deepSeekChatClient(DeepSeekChatModel deepSeekChatModel) {
        return ChatClient
                .builder(deepSeekChatModel)
                .build();
    }*/
}