package com.example.myapplication;

public class ChatMessage {
    public String sender;
    public String message;
    public boolean isMe; // true = 用户自己，false = 机器人

    public ChatMessage(String sender, String message, boolean isMe) {
        this.sender = sender;
        this.message = message;
        this.isMe = isMe;
    }
}
