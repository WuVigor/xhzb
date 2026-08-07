package com.xhzb.nursing.config;

import com.xhzb.nursing.constants.SystemConstants;
import com.xhzb.nursing.service.impl.RedisChatMemoryService;
import com.xhzb.nursing.tool.NursingProjectTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SpringAIConfig {

    /**
     * 创建并返回一个ChatClient的Spring Bean实例。
     * @param openAiChatModel
     * @return
     */
    @Bean
    @Primary
    public ChatClient chatClient(ChatModel chatModel, VectorStore vectorStore, NursingProjectTool nursingProjectTool, RedisChatMemoryService redisChatMemoryService) {

        // 检索rag的数据
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.7d)
                        .topK(5)
                        .build())
                .build();


        return ChatClient
                .builder(chatModel)
                .defaultTools(nursingProjectTool)
                .defaultSystem(SystemConstants.prompt)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(redisChatMemoryService).build(),
                        questionAnswerAdvisor)
                .build();
    }

    @Bean
    public TextSplitter textSplitter() {
        return TokenTextSplitter.builder()
                .withChunkSize(500)  //目标块大小  token数
                .withMinChunkSizeChars(200) // 最小块的字符数
                .withMinChunkLengthToEmbed(10) // 最小的文本字符长度
                .withMaxNumChunks(10000)  //文档最大块数
                .withKeepSeparator(false)   //不保留换行符
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

    @Bean("chatClientByAssessment")
    public ChatClient chatClientByAssessment(ChatModel chatModel) {
        return ChatClient
                .builder(chatModel)
                .defaultSystem("你是一个健康评估专家，专门用来评估老人的健康情况")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
