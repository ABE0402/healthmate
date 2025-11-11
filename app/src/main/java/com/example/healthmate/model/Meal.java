package com.example.healthmate.model;

import java.util.Date;

public class Meal {

    // MealTime 'enum'으로 MealTime 타입 구현
    public enum MealTime {
        BREAKFAST("아침"),
        LUNCH("점심"),
        DINNER("저녁"),
        SNACK("간식"),
        LATE_NIGHT("야식");

        private final String displayName;

        MealTime(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private long id;
    private String foodItem;
    private int servingSize;
    private int kcal;
    private Nutrients macro;
    private MealTime time;
    private Date date;
    private String imageBase64; // 웹의 'image' 필드는 Base64 문자열로 처리

    // 생성자, Getter, Setter ...

    // Getter 예시
    public long getId() { return id; }
    public String getFoodItem() { return foodItem; }
    public int getKcal() { return kcal; }
    public Nutrients getMacro() { return macro; }
    public MealTime getTime() { return time; }
    public Date getDate() { return date; }
}
