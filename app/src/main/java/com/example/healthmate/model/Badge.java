package com.example.healthmate.model;

public class Badge {
    private String id;
    private String name;
    private String description;
    private int iconResId; // 아이콘 Drawable ID

    public Badge(String id, String name, String description, int iconResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
    }
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
}