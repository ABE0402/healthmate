package com.example.healthmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthmate.model.AIChallenge;
import com.example.healthmate.model.ChallengeProgress;
import com.example.healthmate.model.Meal;
import com.example.healthmate.model.MealPlan;
import com.example.healthmate.model.Recipe;
import com.example.healthmate.model.UserProfile;
import com.example.healthmate.service.ChallengeService;
import com.example.healthmate.service.GeminiService;
import com.example.healthmate.utils.CalculationUtils; // CalculationUtils import 추가

import java.util.List;

// React의 AnalysisPage.tsx의 상태 관리
public class AnalysisViewModel extends ViewModel {

    private final GeminiService geminiService = new GeminiService();

    // (Repository에서 전체 Meal 리스트와 UserProfile을 가져왔다고 가정)
    private List<Meal> allMeals;
    private UserProfile userProfile;

    // --- LiveData ---
    // React의 workoutPlan, isFetchingWorkout
    private final MutableLiveData<String> _workoutPlan = new MutableLiveData<>();
    public LiveData<String> getWorkoutPlan() { return _workoutPlan; }

    // React의 recipe, isFetchingRecipe
    private final MutableLiveData<Recipe> _recipe = new MutableLiveData<>();
    public LiveData<Recipe> getRecipe() { return _recipe; }

    // --- 13단계: AI 플래너 LiveData ---
    private final MutableLiveData<Boolean> _isGeneratingPlan = new MutableLiveData<>(false);
    public LiveData<Boolean> isGeneratingPlan() { return _isGeneratingPlan; }

    private final MutableLiveData<MealPlan> _mealPlan = new MutableLiveData<>();
    public LiveData<MealPlan> getMealPlan() { return _mealPlan; }

    // ... (기타 LiveData) ...

    // --- 14단계: 챌린지 LiveData ---
    private final MutableLiveData<Boolean> _isFetchingAiChallenge = new MutableLiveData<>(false);
    public LiveData<Boolean> isFetchingAiChallenge() { return _isFetchingAiChallenge; }

    private final MutableLiveData<AIChallenge> _aiChallenge = new MutableLiveData<>();
    public LiveData<AIChallenge> getAiChallenge() { return _aiChallenge; }

    private final MutableLiveData<List<ChallengeProgress>> _longTermChallenges = new MutableLiveData<>();
    public LiveData<List<ChallengeProgress>> getLongTermChallenges() { return _longTermChallenges; }


    public AnalysisViewModel() {
        // ViewModel 생성 시 챌린지 데이터를 로드
        loadChallengeData();
    }

    private void loadAllAnalysisData() {
        // TODO: Repository에서 allMeals, userProfile 로드

        // 데이터 로드 후 AI 함수들 호출
        fetchAIWorkoutPlan();
        fetchAIRecipe();
    }

    public void fetchAIWorkoutPlan() {
        geminiService.getAIWorkoutPlan(allMeals, userProfile, new GeminiService.TextCallback() {
            @Override
            public void onSuccess(String textResponse) {
                _workoutPlan.postValue(textResponse);
            }
            @Override
            public void onError(Exception e) {
                _workoutPlan.postValue("운동 계획 로딩 실패");
            }
        });
    }

    public void fetchAIRecipe() {
        geminiService.getAIRecipe(allMeals, new GeminiService.RecipeCallback() {
            @Override
            public void onSuccess(Recipe recipe) {
                _recipe.postValue(recipe);
            }
            @Override
            public void onError(Exception e) {
                _recipe.postValue(null);
            }
        });
    }

    public void generateMealPlan(String preferences) {
        _isGeneratingPlan.setValue(true);
        _mealPlan.setValue(null);

        geminiService.getAIMealPlan(userProfile, preferences, new GeminiService.MealPlanCallback() {
            @Override
            public void onSuccess(MealPlan mealPlan) {
                _mealPlan.postValue(mealPlan);
                _isGeneratingPlan.postValue(false);
            }
            @Override
            public void onError(Exception e) {
                // TODO: 에러 처리 LiveData 추가
                _isGeneratingPlan.postValue(false);
            }
        });
    }

    private void loadChallengeData() {
        // TODO: Repository를 통해 allMeals와 userProfile을 실제로 로드해야 합니다.
        // 현재는 null 상태이므로 아래 코드가 실행되지 않거나 오류를 발생시킬 수 있습니다.
        if (userProfile == null || allMeals == null) {
            // 적절한 오류 처리 또는 초기화 로직이 필요합니다.
            return;
        }

        // 1. AI 챌린지 가져오기
        fetchAIChallenge();

        // 2. 장기 챌린지 계산
        // CalculationUtils의 정적 메서드 호출로 수정
        int dailyGoal = CalculationUtils.calculateTDEE(userProfile);
        List<ChallengeProgress> challenges = ChallengeService.calculateChallenges(allMeals, dailyGoal);
        _longTermChallenges.postValue(challenges);
    }

    public void fetchAIChallenge() {
        _isFetchingAiChallenge.setValue(true);
        geminiService.getAIChallenge(allMeals, new GeminiService.AIChallengeCallback() {
            @Override
            public void onSuccess(AIChallenge challenge) {
                _aiChallenge.postValue(challenge);
                _isFetchingAiChallenge.postValue(false);
            }
            @Override
            public void onError(Exception e) {
                _isFetchingAiChallenge.postValue(false);
            }
        });
    }
}