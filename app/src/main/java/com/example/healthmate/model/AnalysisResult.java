package com.example.healthmate.model;

import com.google.gson.annotations.SerializedName;

// geminiService.ts의 analysisItemSchema
public class AnalysisResult {

    @SerializedName("foodItem")
    private String foodItem;

    @SerializedName("servingSize")
    private int servingSize;

    @SerializedName("kcal")
    private int kcal;

    @SerializedName("macro")
    private Nutrients macro; // 1단계에서 만든 Nutrients.java 재사용

    public String getFoodItem() { return foodItem; }
    public int getKcal() { return kcal; }
    public Nutrients getMacro() { return macro; }
    public int getServingSize() { return servingSize; }
}