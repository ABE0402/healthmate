package com.example.healthmate.model;

// React의 SuggestionState 타입
public class SuggestionData {

    // React의 IconName 타입 ('Dumbbell' | 'Target' | 'Zap')
    public enum IconType {
        DUMBBELL, // 운동
        TARGET,   // 단백질
        ZAP,      // 잘함
        LOADING   // 로딩 중
    }

    private final IconType iconType;
    private final String title;
    private final String description;
    private final String ctaText; // "Call to Action" 텍스트

    public SuggestionData(IconType iconType, String title, String description, String ctaText) {
        this.iconType = iconType;
        this.title = title;
        this.description = description;
        this.ctaText = ctaText;
    }

    // Getters
    public IconType getIconType() { return iconType; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCtaText() { return ctaText; }
}