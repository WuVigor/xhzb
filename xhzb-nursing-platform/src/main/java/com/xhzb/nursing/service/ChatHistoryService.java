package com.xhzb.nursing.service;

import java.util.List;

public interface ChatHistoryService {

    public void save(String userId,String chatId);


    public List<String> getChatIds();


    public void delChatHistory(String chatId);
}
