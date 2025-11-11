package com.example.healthmate.model;

public class Nutrients {
    private double carbs;
    private double protein;
    private double fat;

    // 생성자
    public Nutrients(double carbs, double protein, double fat) {
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    // Getter
    public double getCarbs() { return carbs; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }

    // Setter (필요에 따라 추가)
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setFat(double fat) { this.fat = fat; }
}