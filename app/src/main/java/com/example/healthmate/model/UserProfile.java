package com.example.healthmate.model;

import java.util.List;

public class UserProfile {

    // ActivityLevel 'enum'으로 ActivityLevel 타입 구현
    public enum ActivityLevel {
        SEDENTARY,
        LIGHT,
        MODERATE,
        ACTIVE,
        VERY_ACTIVE
    }

    // Gender 'enum'
    public enum Gender {
        MALE,
        FEMALE
    }

    private Gender gender;
    private int age;
    private int height;
    private int weight;
    private ActivityLevel activityLevel;
    private List<String> unlockedBadgeIds;

    // 생성자, Getter, Setter ...

    // Getter 예시
    public Gender getGender() { return gender; }
    public int getAge() { return age; }
    public int getWeight() { return weight; }
    public ActivityLevel getActivityLevel() { return activityLevel; }
    public List<String> getUnlockedBadgeIds() { return unlockedBadgeIds; }
}