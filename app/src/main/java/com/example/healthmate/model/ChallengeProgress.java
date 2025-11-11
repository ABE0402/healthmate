package com.example.healthmate.model;

public class ChallengeProgress {

    public enum ChallengeIcon { ZAP, TARGET, AWARD } // React의 아이콘 타입
    public enum Status { IN_PROGRESS, COMPLETED }

    private final String id;
    private final String title;
    private final String description;
    private final ChallengeIcon icon;
    private final int goal;
    private int current;
    private int progress; // 0-100
    private Status status;

    public ChallengeProgress(String id, String title, String description, ChallengeIcon icon, int goal) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.goal = goal;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return String.format("%s (%d/%d일)", description, current, goal); }
    public int getProgress() { return progress; }
    public Status getStatus() { return status; }
    public ChallengeIcon getIcon() { return icon; }

    // React의 calculateChallengesProgress 로직
    public void updateProgress(int current) {
        this.current = current;
        this.progress = Math.min((int) (((double) current / goal) * 100), 100);
        this.status = (this.progress >= 100) ? Status.COMPLETED : Status.IN_PROGRESS;
    }
}