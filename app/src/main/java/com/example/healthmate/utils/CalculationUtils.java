package com.example.healthmate.utils;

import com.example.healthmate.model.UserProfile;
import java.util.HashMap;
import java.util.Map;

public class CalculationUtils {

    // React의 ACTIVITY_LEVELS
    private static final Map<UserProfile.ActivityLevel, Double> ACTIVITY_MULTIPLIERS = new HashMap<>();
    static {
        ACTIVITY_MULTIPLIERS.put(UserProfile.ActivityLevel.SEDENTARY, 1.2);
        ACTIVITY_MULTIPLIERS.put(UserProfile.ActivityLevel.LIGHT, 1.375);
        ACTIVITY_MULTIPLIERS.put(UserProfile.ActivityLevel.MODERATE, 1.55);
        ACTIVITY_MULTIPLIERS.put(UserProfile.ActivityLevel.ACTIVE, 1.725);
        ACTIVITY_MULTIPLIERS.put(UserProfile.ActivityLevel.VERY_ACTIVE, 1.9);
    }

    /**
     * React의 bmr useMemo
     */
    public static int calculateBMR(UserProfile profile) {
        if (profile == null) return 0;

        // Mifflin-St Jeor Equation
        double bmr = (10 * profile.getWeight()) + (6.25 * profile.getHeight()) - (5 * profile.getAge());
        if (profile.getGender() == UserProfile.Gender.MALE) {
            bmr += 5;
        } else {
            bmr -= 161;
        }
        return (int) Math.round(bmr);
    }

    /**
     * React의 dailyGoal
     */
    public static int calculateDailyGoal(UserProfile profile) {
        if (profile == null) return 0;
        int bmr = calculateBMR(profile);
        double multiplier = ACTIVITY_MULTIPLIERS.getOrDefault(profile.getActivityLevel(), 1.55);
        return (int) Math.round(bmr * multiplier);
    }
}