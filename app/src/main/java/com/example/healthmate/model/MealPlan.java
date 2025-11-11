package com.example.healthmate.model;

public class MealPlan {
    private PlannedMeal breakfast;

    public PlannedMeal getLunch() {
        return lunch;
    }

    public void setLunch(PlannedMeal lunch) {
        this.lunch = lunch;
    }

    public PlannedMeal getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(PlannedMeal breakfast) {
        this.breakfast = breakfast;
    }

    public int getTotalKcal() {
        return totalKcal;
    }

    public void setTotalKcal(int totalKcal) {
        this.totalKcal = totalKcal;
    }

    public PlannedMeal getSnacks() {
        return snacks;
    }

    public void setSnacks(PlannedMeal snacks) {
        this.snacks = snacks;
    }

    public PlannedMeal getDinner() {
        return dinner;
    }

    public void setDinner(PlannedMeal dinner) {
        this.dinner = dinner;
    }

    private PlannedMeal lunch;
    private PlannedMeal dinner;
    private PlannedMeal snacks;
    private int totalKcal;
}
