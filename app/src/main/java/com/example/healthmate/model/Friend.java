package com.example.healthmate.model;

public class Friend {
    private long id;
    private String name;
    private String avatarUrl; // React의 avatar

    public Friend(long id, String name, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
}