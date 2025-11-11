package com.example.healthmate.model;

public class Group {
    private long id;
    private String name;
    private String description;
    private int members;
    private String challenge;
    private int progress; // 0-100

    // 생성자, Getters, Setters
    public Group(long id, String name, String description, int members, String challenge, int progress) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
        this.challenge = challenge;
        this.progress = progress;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getMembers() { return members; }
    public String getChallenge() { return challenge; }
    public int getProgress() { return progress; }
}