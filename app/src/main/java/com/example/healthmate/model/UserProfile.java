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

    private String id;
    private String name;
    private String email;

    private Gender gender;
    private int age;
    private double height;
    private int weight;
    private ActivityLevel activityLevel;
    private List<String> unlockedBadgeIds;

    public UserProfile(String id, String name, String email, Gender gender, int age, double height, int weight, ActivityLevel activityLevel, List<String> unlockedBadgeIds) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.unlockedBadgeIds = unlockedBadgeIds;
    }

    // Getter 예시
    public Gender getGender() { return gender; }
    public int getAge() { return age; }
    public int getWeight() { return weight; }
    public double getHeight() { return height; }

    public ActivityLevel getActivityLevel() { return activityLevel; }
    public List<String> getUnlockedBadgeIds() { return unlockedBadgeIds; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }


}