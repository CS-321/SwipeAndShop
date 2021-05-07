package com.example.swipeandshop;

import java.util.List;

public class Chat {
    String chatId = "";
    List<ChatMessage> messages;

    public Chat(String chatId, List<ChatMessage> messages){
        this.chatId = chatId;
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getChatId() {
        return chatId;
    }
}
