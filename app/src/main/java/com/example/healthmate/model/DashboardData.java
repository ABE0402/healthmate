package com.example.healthmate.model;

public class DashboardData {
    private final int totalKcal;
    private final int goalKcal;
    private final Nutrients macros;

    public DashboardData(int totalKcal, int goalKcal, Nutrients macros) {
        this.totalKcal = totalKcal;
        this.goalKcal = goalKcal;
        this.macros = macros;
    }

    public int getTotalKcal() {
        return totalKcal;
    }

    public int getGoalKcal() {
        return goalKcal;
    }

    public Nutrients getMacros() {
        return macros;
    }
}