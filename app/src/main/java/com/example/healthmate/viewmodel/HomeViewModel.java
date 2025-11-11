package com.example.healthmate.viewmodel;

import android.graphics.Bitmap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.healthmate.model.AnalysisResult;
import com.example.healthmate.model.SuggestionData;
import com.example.healthmate.service.GeminiService; // 4.3에서 만든 서비스
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Bitmap> _selectedImage = new MutableLiveData<>();
    public LiveData<Object> dashboardData;

    public LiveData<Bitmap> getSelectedImage() { return _selectedImage; }

    private final MutableLiveData<Boolean> _isAnalyzingImage = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsAnalyzingImage() { return _isAnalyzingImage; }

    private final MutableLiveData<List<AnalysisResult>> _imageAnalysisResult = new MutableLiveData<>();
    public LiveData<List<AnalysisResult>> getImageAnalysisResult() { return _imageAnalysisResult; }

    private final MutableLiveData<String> _imageAnalysisError = new MutableLiveData<>();
    public LiveData<String> getImageAnalysisError() { return _imageAnalysisError; }
    private final GeminiService geminiService = new GeminiService();

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<List<AnalysisResult>> _analysisResult = new MutableLiveData<>();
    public LiveData<List<AnalysisResult>> analysisResult = _analysisResult;

    // --- 8단계: 수분 섭취 LiveData ---
    private final MutableLiveData<Integer> _waterIntake = new MutableLiveData<>(0);
    public LiveData<Integer> getWaterIntake() { return _waterIntake; }

    // (DAILY_WATER_GOAL은 8로 가정)
    public static final int DAILY_WATER_GOAL = 8;

    // --- 9단계: AI 제안 LiveData ---
    private final MutableLiveData<SuggestionData> _suggestion = new MutableLiveData<>();
    public LiveData<SuggestionData> getSuggestion() { return _suggestion; }

    // (DAILY_PROTEIN_GOAL은 50으로 가정)
    public static final int DAILY_PROTEIN_GOAL = 50; //

    public HomeViewModel() {
        // ... (기존 생성자 코드) ...
        loadWaterIntake(); // (오늘 날짜의 수분 섭취량 로드)
        // ViewModel이 생성될 때 초기 제안 설정
        _suggestion.setValue(new SuggestionData(
                SuggestionData.IconType.LOADING, "로드 중...", "AI가 맞춤 제안을 생성하고 있습니다.", ""
        ));
    }

    private void loadWaterIntake() {
        // TODO: Repository에서 오늘 날짜의 수분 섭취량 불러오기
        _waterIntake.setValue(0); // 임시
    }


    /**
     * 카메라로 사진을 찍은 후 호출되는 함수
     * @param image 사용자가 찍은 사진
     */
    public void onPhotoTaken(Bitmap image) {
        _selectedImage.setValue(image);
        _isAnalyzingImage.setValue(true);
        _imageAnalysisResult.setValue(null);
        _imageAnalysisError.setValue(null);

        geminiService.analyzeImageWithGemini(image, new GeminiService.AnalysisCallback() {
            @Override
            public void onSuccess(List<AnalysisResult> results) {
                _imageAnalysisResult.postValue(results);
                _isAnalyzingImage.postValue(false);
            }
            @Override
            public void onError(Exception e) {
                _imageAnalysisError.postValue("AI 분석 실패: " + e.getMessage());
                _isAnalyzingImage.postValue(false);
            }
        });
    }

    public void clearAnalysisData() {
        _selectedImage.setValue(null);
        _imageAnalysisResult.setValue(null);
        _imageAnalysisError.setValue(null);
    }

    public void updateWaterIntake(int amount) {
        int current = (_waterIntake.getValue() != null) ? _waterIntake.getValue() : 0;
        int newAmount = Math.max(0, current + amount); // 0 미만으로 내려가지 않음

        // TODO: Repository를 통해 DB에 newAmount 저장
        _waterIntake.setValue(newAmount);
    }

    public void updateSuggestion(DashboardData data, int userWeight) {
        if (data == null) return;

        int surplusKcal = data.totalKcal - data.goalKcal;

        // 1. 칼로리 초과 시 (운동 추천)
        if (surplusKcal > 100) {
            // "AI 운동 추천" 로딩 상태
            _suggestion.postValue(new SuggestionData(
                    SuggestionData.IconType.DUMBBELL, "AI 운동 추천", "AI가 운동 계획을 생성 중입니다...", ""
            ));

            // React의 getExerciseSuggestion 호출
            geminiService.getExerciseSuggestion(surplusKcal, userWeight, new GeminiService.TextCallback() {
                @Override
                public void onSuccess(String exerciseDesc) {
                    _suggestion.postValue(new SuggestionData(
                            SuggestionData.IconType.DUMBBELL, "AI 운동 추천", exerciseDesc, "운동 기록하기"
                    ));
                }
                @Override
                public void onError(Exception e) {
                    // AI 실패 시 Fallback 텍스트
                    _suggestion.postValue(new SuggestionData(
                            SuggestionData.IconType.DUMBBELL, "오늘의 운동 추천",
                            String.format(Locale.getDefault(), "섭취 칼로리가 목표를 %d kcal 초과했어요.", surplusKcal),
                            "운동 기록하기"
                    ));
                }
            });

            // 2. 단백질 부족 시 (단백질 제안)
        } else if (data.macros.getProtein() < DAILY_PROTEIN_GOAL && data.totalKcal > 0) {
            _suggestion.postValue(new SuggestionData(
                    SuggestionData.IconType.TARGET, "단백질 보충 제안",
                    "단백질 섭취가 부족해요. 닭가슴살, 두부, 계란 등으로 보충해 건강한 근육을 만드세요.",
                    "식단팁 보기"
            ));

            // 3. 기본 상태 (잘함)
        } else {
            _suggestion.postValue(new SuggestionData(
                    SuggestionData.IconType.ZAP, "잘하고 있어요!",
                    "균형 잡힌 식단을 유지하고 있습니다. 이대로 꾸준히 관리해 보세요.",
                    "건강 리포트 보기"
            ));
        }
    }
    // 3단계의 loadDashboardData() 함수 수정
    public void loadDashboardData() {
        // ... (기존 DB에서 데이터 로드 로직) ...
        DashboardData data = ... ; // DB에서 로드한 데이터
        _dashboardData.setValue(data);

        // --- 9단계 추가 ---
        // 데이터 로드 후 AI 제안 업데이트
        // (userWeight는 UserProfile에서 가져와야 함)
        int userWeight = 75; // 예시
        updateSuggestion(data, userWeight);
        // --- ---
    }

    // ... (3단계의 loadDashboardData 함수 등) ...
}