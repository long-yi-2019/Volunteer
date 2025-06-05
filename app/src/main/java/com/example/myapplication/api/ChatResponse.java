package com.example.myapplication.api;

import java.util.List;

public class ChatResponse {
    private List<Choice> choices;

    public String getAnswer() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).message.content.trim();
        }
        return "抱歉，我无法回答这个问题。";
    }

    public static class Choice {
        private Message message;
    }

    public static class Message {
        private String role;
        private String content;
    }
}