package com.example.healthmate.model;

public class ChatMessage {

    // React의 (role: 'user' | 'model')
    public enum Role {
        USER,
        MODEL
    }

    private final String text;
    private final Role role;
    private boolean isLoading = false; // AI 응답 대기 중 (로딩 닷)

    public ChatMessage(String text, Role role) {
        this.text = text;
        this.role = role;
    }

    // 로딩 메시지용 생성자
    public ChatMessage(Role role, boolean isLoading) {
        this.text = "";
        this.role = role;
        this.isLoading = isLoading;
    }

    // Getters
    public String getText() { return text; }
    public Role getRole() { return role; }
    public boolean isLoading() { return isLoading; }
}