package com.example.healthmate.service;

import com.example.healthmate.model.ChallengeProgress;
import com.example.healthmate.model.Meal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ChallengeService {

    private static final int DAILY_PROTEIN_GOAL = 50; //

    // React의 challenges 배열
    private static List<ChallengeProgress> getBaseChallenges() {
        List<ChallengeProgress> challenges = new ArrayList<>();
        challenges.add(new ChallengeProgress("protein", "단백질 섭취 챌린지", "최근 식사 기록 3일간 단백질 목표 달성", ChallengeProgress.ChallengeIcon.TARGET, 3));
        challenges.add(new ChallengeProgress("breakfast", "아침 식사 챙기기", "최근 7일간 아침 식사 기록", ChallengeProgress.ChallengeIcon.ZAP, 7));
        challenges.add(new ChallengeProgress("calorie", "주간 칼로리 목표", "최근 7일간 목표 칼로리 범위 유지", ChallengeProgress.ChallengeIcon.AWARD, 7));
        return challenges;
    }

    // React의 calculateChallengesProgress 함수
    public static List<ChallengeProgress> calculateChallenges(List<Meal> allMeals, int dailyCalorieGoal) {
        List<ChallengeProgress> challenges = getBaseChallenges();

        // --- 1. 단백질 챌린지 (proteinProgress) ---
        Map<String, Integer> dailyProtein = new HashMap<>();
        for (Meal meal : allMeals) {
            String day = getDayString(meal.getDate());
            dailyProtein.put(day, dailyProtein.getOrDefault(day, 0) + meal.getProtein());
        }
        int proteinSuccessDays = (int) dailyProtein.values().stream()
                .filter(protein -> protein >= DAILY_PROTEIN_GOAL)
                .count(); // (간략화된 로직, 원본은 최근 3일)
        challenges.get(0).updateProgress(proteinSuccessDays);

        // --- 2. 아침 챌린지 (breakfastProgress) ---
        Set<String> breakfastDays = new HashSet<>();
        Calendar cal7DaysAgo = Calendar.getInstance();
        cal7DaysAgo.add(Calendar.DATE, -6);

        for (Meal meal : allMeals) {
            if (Meal.MealTime.BREAKFAST.getDisplayName().equals(meal.getMealType()) && meal.getDate().after(cal7DaysAgo.getTime())) {
                breakfastDays.add(getDayString(meal.getDate()));
            }
        }
        challenges.get(1).updateProgress(breakfastDays.size());

        // --- 3. 칼로리 챌린지 (calorieProgress) ---
        Map<String, Integer> dailyKcal = new HashMap<>();
        for (Meal meal : allMeals) {
            if (meal.getDate().after(cal7DaysAgo.getTime())) {
                String day = getDayString(meal.getDate());
                dailyKcal.put(day, dailyKcal.getOrDefault(day, 0) + meal.getCalories());
            }
        }
        int calorieSuccessDays = (int) dailyKcal.values().stream()
                .filter(kcal -> kcal >= dailyCalorieGoal * 0.9 && kcal <= dailyCalorieGoal * 1.1)
                .count();
        challenges.get(2).updateProgress(calorieSuccessDays);

        return challenges;
    }

    private static String getDayString(java.util.Date date) {
        // 날짜를 YYYY-MM-DD 형식의 문자열로 변환
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
