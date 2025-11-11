package com.example.healthmate.model;

import java.util.Date;

public class Meal {

    // MealTime 'enum'은 유용하므로 그대로 둡니다.
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

        @Override
        public String toString() {
            return displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private int id;
    private String foodName;
    private String mealType; // Enum 대신 String 타입으로 변경
    private int calories;    // kcal -> calories로 변경
    private Date date;
    private int protein;
    private int fat;
    private int carbohydrates;

    // HomeViewModel에서 사용하는 생성자
    public Meal(int id, String foodName, String mealType, int calories, Date date, int protein, int fat, int carbohydrates) {
        this.id = id;
        this.foodName = foodName;
        this.mealType = mealType;
        this.calories = calories;
        this.date = date;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    // Getters
    public int getId() { return id; }
    public String getFoodName() { return foodName; }
    public String getMealType() { return mealType; }
    public int getCalories() { return calories; } // getKcal -> getCalories
    public Date getDate() { return date; }
    public int getProtein() { return protein; }
    public int getFat() { return fat; }
    public int getCarbohydrates() { return carbohydrates; }
}
