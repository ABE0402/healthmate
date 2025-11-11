
package com.example.healthmate.viewmodel;

import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.healthmate.model.AnalysisResult;
import com.example.healthmate.model.DashboardData;
import com.example.healthmate.model.Meal;
import com.example.healthmate.model.Nutrients;
import com.example.healthmate.model.SuggestionData;
import com.example.healthmate.service.GeminiService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeViewModel extends ViewModel {

    // In-memory data source instead of a repository
    private final List<Meal> allMeals = new ArrayList<>();
    private static final AtomicInteger mealIdCounter = new AtomicInteger(0);

    private final MutableLiveData<DashboardData> _dashboardData = new MutableLiveData<>();
    public LiveData<DashboardData> getDashboardData() { return _dashboardData; }

    private final MutableLiveData<List<Meal>> _todayMeals = new MutableLiveData<>();
    public LiveData<List<Meal>> getTodayMeals() { return _todayMeals; }

    private final MutableLiveData<Bitmap> _selectedImage = new MutableLiveData<>();
    public LiveData<Bitmap> getSelectedImage() { return _selectedImage; }

    private final MutableLiveData<List<AnalysisResult>> _imageAnalysisResult = new MutableLiveData<>();
    public LiveData<List<AnalysisResult>> getImageAnalysisResult() { return _imageAnalysisResult; }

    private final MutableLiveData<String> _imageAnalysisError = new MutableLiveData<>();
    public LiveData<String> getImageAnalysisError() { return _imageAnalysisError; }

    private final MutableLiveData<Boolean> _isAnalyzingImage = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsAnalyzingImage() { return _isAnalyzingImage; }

    private final MutableLiveData<Boolean> _isAnalyzingText = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsAnalyzingText() { return _isAnalyzingText; }

    private final MutableLiveData<String> _textAnalysisError = new MutableLiveData<>();
    public LiveData<String> getTextAnalysisError() { return _textAnalysisError; }

    private final MutableLiveData<Boolean> _mealAddedSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> getMealAddedSuccess() { return _mealAddedSuccess; }

    private final GeminiService geminiService = new GeminiService();

    private final MutableLiveData<Integer> _waterIntake = new MutableLiveData<>(0);
    public LiveData<Integer> getWaterIntake() { return _waterIntake; }
    public static final int DAILY_WATER_GOAL = 8;

    private final MutableLiveData<SuggestionData> _suggestion = new MutableLiveData<>();
    public LiveData<SuggestionData> getSuggestion() { return _suggestion; }
    public static final int DAILY_PROTEIN_GOAL = 50;

    public HomeViewModel() {
        loadDashboardData(); // Initial load
        _suggestion.setValue(new SuggestionData(
                SuggestionData.IconType.LOADING, "로드 중...", "AI가 맞춤 제안을 생성하고 있습니다.", ""
        ));
    }

    public void addAnalyzedMeals(List<AnalysisResult> results, Meal.MealTime mealTime) {
        for (AnalysisResult result : results) {
            Meal newMeal = new Meal(
                    mealIdCounter.getAndIncrement(),
                    result.getFoodName(),
                    mealTime.name(),
                    (int) result.getCalories(),
                    new Date(),
                    (int) result.getProtein(),
                    (int) result.getFat(),
                    (int) result.getCarbohydrates()
            );
            allMeals.add(newMeal);
        }
        loadDashboardData(); // Recalculate and update LiveData
    }

    public void addManualMeal(String foodItem, Meal.MealTime mealTime) {
        _isAnalyzingText.setValue(true);
        _textAnalysisError.setValue(null);

        geminiService.analyzeTextWithGemini(foodItem, new GeminiService.AnalysisCallback() {
            @Override
            public void onSuccess(List<AnalysisResult> results) {
                if (results != null && !results.isEmpty()) {
                    addAnalyzedMeals(results, mealTime); // Reuse the same logic
                    _mealAddedSuccess.postValue(true);
                } else {
                    _textAnalysisError.postValue("음식 정보를 찾을 수 없습니다.");
                }
                _isAnalyzingText.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                _textAnalysisError.postValue("AI 분석 실패: " + e.getMessage());
                _isAnalyzingText.postValue(false);
            }
        });
    }

    public void deleteMeal(Meal mealToDelete) {
        allMeals.removeIf(meal -> meal.getId() == mealToDelete.getId());
        loadDashboardData();
    }

    private void loadDashboardData() {
        int totalKcal = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        double totalFat = 0;

        for (Meal meal : allMeals) {
            totalKcal += meal.getCalories();
            totalCarbs += meal.getCarbohydrates();
            totalProtein += meal.getProtein();
            totalFat += meal.getFat();
        }

        Nutrients macros = new Nutrients(totalCarbs, totalProtein, totalFat);
        int goalKcal = 2000; // Mock goal
        DashboardData data = new DashboardData(totalKcal, goalKcal, macros);

        _dashboardData.setValue(data);
        _todayMeals.setValue(new ArrayList<>(allMeals)); // Post a new list
    }

    public void onMealAddSuccessShown() {
        _mealAddedSuccess.setValue(false);
    }

    public void updateWaterIntake(int amount) {
        int current = (_waterIntake.getValue() != null) ? _waterIntake.getValue() : 0;
        int newAmount = Math.max(0, current + amount);
        _waterIntake.setValue(newAmount);
    }

    public void updateSuggestion(DashboardData data, int userWeight) {
        if (data == null) return;

        int surplusKcal = data.getTotalKcal() - data.getGoalKcal();
        Nutrients macros = data.getMacros();

        if (surplusKcal > 100) {
            _suggestion.postValue(new SuggestionData(
                    SuggestionData.IconType.DUMBBELL, "AI 운동 추천", "AI가 운동 계획을 생성 중입니다...", ""
            ));
            geminiService.getExerciseSuggestion(surplusKcal, userWeight, new GeminiService.TextCallback() {
                @Override
                public void onSuccess(String exerciseDesc) {
                    _suggestion.postValue(new SuggestionData(
                            SuggestionData.IconType.DUMBBELL, "AI 운동 추천", exerciseDesc, "운동 기록하기"
                    ));
                }
                @Override
                public void onError(Exception e) {
                    _suggestion.postValue(new SuggestionData(
                            SuggestionData.IconType.DUMBBELL, "오늘의 운동 추천",
                            String.format(Locale.getDefault(), "섭취 칼로리가 목표를 %d kcal 초과했어요.", surplusKcal),
                            "운동 기록하기"
                    ));
                }
            });
        } else if (macros != null && macros.getProtein() < DAILY_PROTEIN_GOAL && data.getTotalKcal() > 0) {
            _suggestion.postValue(new SuggestionData(
                    SuggestionData.IconType.TARGET, "단백질 보충 제안",
                    "단백질 섭취가 부족해요. 닭가슴살, 두부, 계란 등으로 보충해 건강한 근육을 만드세요.",
                    "식단팁 보기"
            ));
        } else {
            _suggestion.postValue(new SuggestionData(
                    SuggestionData.IconType.ZAP, "잘하고 있어요!",
                    "균형 잡힌 식단을 유지하고 있습니다. 이대로 꾸준히 관리해 보세요.",
                    "건강 리포트 보기"
            ));
        }
    }

    public void clearAnalysisData() {
        _selectedImage.setValue(null);
        _imageAnalysisResult.setValue(null);
        _imageAnalysisError.setValue(null);
        _isAnalyzingImage.setValue(false);
    }
}
