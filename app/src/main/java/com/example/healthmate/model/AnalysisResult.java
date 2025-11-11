package com.example.healthmate.model;

import com.google.gson.annotations.SerializedName;

public class AnalysisResult {

    @SerializedName("foodName") // API 응답에 맞게 foodName으로 변경
    private String foodName;

    @SerializedName("servingSize")
    private int servingSize;

    @SerializedName("calories") // API 응답에 맞게 calories로 변경
    private double calories; // 칼로리는 소수점일 수 있으므로 double로 변경

    @SerializedName("protein")
    private double protein;

    @SerializedName("fat")
    private double fat;

    @SerializedName("carbohydrates")
    private double carbohydrates;

    // 기본 생성자
    public AnalysisResult() {}

    // 모든 필드를 포함하는 생성자
    public AnalysisResult(String foodName, int servingSize, double calories, double protein, double fat, double carbohydrates) {
        this.foodName = foodName;
        this.servingSize = servingSize;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    // Getter 메서드
    public String getFoodName() { return foodName; }
    public int getServingSize() { return servingSize; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbohydrates() { return carbohydrates; }

    // Setter 메서드 (필요에 따라 추가)
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
}