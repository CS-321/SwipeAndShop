package com.example.swipeandshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat {
    String chatId = "";
    String chatId2 = "";
    String chatName = "";
    String otherPersonId = "";
    HashMap<String,ChatMessage> messages;

    public Chat(){
        messages = new HashMap<>();
    }

    public Chat(String chatId, String chatId2, String otherPersonId,String chatName, HashMap<String ,ChatMessage> messages){
        this.chatId = chatId;
        this.messages = messages;
        this.chatName = chatName;
        this.chatId2 = chatId2;
        this.otherPersonId = otherPersonId;
    }

    public HashMap<String,ChatMessage> getMessages() {
        return messages;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getChatId2() {
        return chatId2;
    }

    public String getOtherPersonId() {
        return otherPersonId;
    }
}
