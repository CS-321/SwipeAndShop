package com.example.swipeandshop;

public class ChatMessage {
    private String message = "";
    private String user = "";

    public ChatMessage(){

    }

    public ChatMessage(String message, String user){
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }
}
