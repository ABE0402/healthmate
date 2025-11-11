package com.example.healthmate.service;

import android.graphics.Bitmap;

import com.google.ai.client.generativeai.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.Schema;
import com.google.ai.client.generativeai.type.Type;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.healthmate.BuildConfig;
import com.example.healthmate.model.*; // 1단계와 이 단계에서 만든 모든 모델 클래스

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GeminiService {

    private final GenerativeModelFutures generativeModel;
    private ChatFutures chatSession; // AI 채팅 세션
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    // --- 4.3에서 구현한 콜백 ---
    public interface AnalysisCallback {
        void onSuccess(List<AnalysisResult> results);
        void onError(Exception e);
    }

    // --- 새로운 콜백 인터페이스들 ---
    public interface TextAnalysisCallback {
        void onSuccess(AnalysisResult result);
        void onError(Exception e);
    }

    public interface TextCallback {
        void onSuccess(String textResponse);
        void onError(Exception e);
    }

    public interface MealPlanCallback {
        void onSuccess(MealPlan mealPlan);
        void onError(Exception e);
    }

    public interface AIChallengeCallback {
        void onSuccess(AIChallenge challenge);
        void onError(Exception e);
    }

    public interface RecipeCallback {
        void onSuccess(Recipe recipe);
        void onError(Exception e);
    }

    public interface ProteinSuggestionCallback {
        void onSuccess(List<ProteinFoodSuggestion> suggestions);
        void onError(Exception e);
    }

    public interface StringListCallback {
        void onSuccess(List<String> results);
        void onError(Exception e);
    }


    public GeminiService() {
        // 'gemini-2.5-flash' 모델 사용
        GenerativeModel model = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.GEMINI_API_KEY,
                GenerationConfig.builder().temperature(0.1f).build(),
                null // safetySettings
        );
        generativeModel = GenerativeModelFutures.from(model, executor);
    }

    /**
     * React의 analyzeImageWithGemini (4.3에서 구현)
     */
    public void analyzeImageWithGemini(Bitmap image, AnalysisCallback callback) {
        // ... (4.3에서 구현한 코드) ...
        // 이 함수는 AI의 응답이 JSON 스키마를 따르지 않고
        // 순수 JSON 텍스트로 온다고 가정하고 Gson으로 직접 파싱합니다.
        // ...
    }

    /**
     * React의 analyzeTextWithGemini 구현
     */
    public void analyzeTextWithGemini(String foodItem, TextAnalysisCallback callback) {
        String prompt = String.format("음식 '%s'에 대한 일반적인 1인분 기준 영양 정보를 알려줘. 음식 이름, 1인분 기준 양(g), 칼로리(kcal), 그리고 탄수화물, 단백질, 지방 함량(g)을 포함해서 아래 JSON 스키마에 맞춰서 정확하게 응답해줘. 만약 분석이 불가능하면, foodItem에 '분석 불가'라고 응답해줘.", foodItem);

        // analysisItemSchema 정의
        Schema<AnalysisResult> schema = Schema.fromJson(AnalysisResult.class); // Gson 기반 POJO로 자동 스키마 생성

        GenerationConfig config = GenerationConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(
                new Content.Builder().addPart(prompt).build(), config
        );

        addJsonCallback(future, AnalysisResult.class, callback::onSuccess, callback::onError);
    }

    /**
     * React의 getAIMealPlan 구현
     */
    public void getAIMealPlan(UserProfile userProfile, String preferences, MealPlanCallback callback) {
        // UserProfile에서 목표 칼로리 계산 (1단계 UserProfile.java에 추가 필요)
        int dailyGoal = calculateTDEE(userProfile);

        String prompt = String.format(
                "사용자의 프로필 정보는 다음과 같습니다:\n" +
                        "- 나이: %d세\n" +
                        "- 성별: %s\n" +
                        "- 일일 목표 칼로리: 약 %dkcal\n" +
                        "사용자의 추가적인 식단 요구사항: \"%s\"\n" +
                        "위 정보를 바탕으로, 사용자를 위한 건강하고 균형 잡힌 하루 식단(아침, 점심, 저녁, 그리고 선택적으로 간식 포함)을 계획해주세요.\n" +
                        "각 식사는 구체적인 음식 이름, 예상 칼로리, 간단한 설명을 포함해야 합니다.\n" +
                        "총 칼로리는 사용자의 일일 목표 칼로리에 근접해야 합니다.\n" +
                        "아래 JSON 스키마에 맞춰 정확하게 응답해주세요.",
                userProfile.getAge(), userProfile.getGender().name(), dailyGoal, (preferences.isEmpty() ? "특별한 요구사항 없음" : preferences)
        );

        Schema<MealPlan> schema = Schema.fromJson(MealPlan.class); // mealPlanSchema
        GenerationConfig config = GenerationConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(
                new Content.Builder().addPart(prompt).build(), config
        );

        addJsonCallback(future, MealPlan.class, callback::onSuccess, callback::onError);
    }

    // --- 단순 텍스트 입/출력 함수들 ---

    /**
     * React의 getExerciseSuggestion 구현
     */
    public void getExerciseSuggestion(int surplusKcal, int userWeight, TextCallback callback) {
        String prompt = String.format(
                "사용자의 현재 체중은 %dkg이고, 오늘 목표보다 %dkcal를 초과하여 섭취했습니다.\n" +
                        "이 초과 칼로리를 소모할 수 있는 효과적인 운동 루틴을 추천해주세요.\n" +
                        "- 구체적인 운동 종류와 시간을 포함해주세요. (예: 30분 빠르게 걷기 + 15분 스쿼트)\n" +
                        "- 전문적이면서도 동기부여가 되는 친근한 말투로 작성해주세요.\n" +
                        "- 답변은 1-2 문장으로 간결하게 요약해주세요.",
                userWeight, surplusKcal
        );
        generateText(prompt, callback);
    }

    /**
     * React의 getAIFeedback 구현
     */
    public void getAIFeedback(List<Meal> meals, TextCallback callback) {
        String mealHistory = meals.stream().limit(15)
                .map(m -> String.format("- %s %s: %dkcal (단백질 %.1fg)", m.getTime().getDisplayName(), m.getFoodItem(), m.getKcal(), m.getMacro().getProtein()))
                .collect(Collectors.joining("\n"));

        String prompt = "다음은 사용자의 최근 식단 기록입니다.\n" + mealHistory +
                "\n이 기록을 바탕으로 전문 영양사의 관점에서 사용자의 식습관을 분석하고, 칭찬할 점과 개선할 점을 찾아 구체적인 조언을 해주세요.\n" +
                "답변은 친근하고 이해하기 쉬운 말투로, 2-3문장으로 요약해서 제공해주세요.";

        generateText(prompt, callback);
    }

    // ... getAIWeeklyReport, getAIPatternAnalysis, getAIWorkoutPlan 등도 위와 동일한 패턴 ...

    // --- 다른 JSON 입/출력 함수들 ---

    /**
     * React의 getAIChallenge 구현
     */
    public void getAIChallenge(List<Meal> meals, AIChallengeCallback callback) {
        String mealHistory = meals.stream().limit(10)
                .map(m -> String.format("- %s %s: %dkcal (단백질 %.1fg)", m.getTime().getDisplayName(), m.getFoodItem(), m.getKcal(), m.getMacro().getProtein()))
                .collect(Collectors.joining("\n"));

        String prompt = "당신은 사용자가 건강한 습관을 만들도록 돕는 유능한 헬스 코치입니다.\n" +
                "아래는 사용자의 최근 식단 기록입니다.\n" + mealHistory +
                "\n이 기록을 분석하여, 사용자가 재미있게 시도해볼 만한 '단기적이고 구체적인' 건강 챌린지(퀘스트)를 하나 제안해주세요.\n" +
                "아래 JSON 스키마에 맞춰 응답해주세요.";

        Schema<AIChallenge> schema = Schema.fromJson(AIChallenge.class); // aiChallengeSchema
        GenerationConfig config = GenerationConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(
                new Content.Builder().addPart(prompt).build(), config
        );

        addJsonCallback(future, AIChallenge.class, callback::onSuccess, callback::onError);
    }

    /**
     * React의 getAIRecipe 구현
     */
    public void getAIRecipe(List<Meal> meals, RecipeCallback callback) {
        String mealHistory = meals.stream().limit(5).map(Meal::getFoodItem).collect(Collectors.joining(", "));
        String prompt = String.format("사용자의 최근 식단은 다음과 같습니다: %s. 이 식단을 바탕으로, 사용자가 좋아할 만한 건강하고 맛있는 레시피를 하나 추천해주세요. 간단하고 따라하기 쉬워야 합니다. JSON 스키마에 맞춰 응답해주세요.", mealHistory);

        Schema<Recipe> schema = Schema.fromJson(Recipe.class); // recipeSchema
        GenerationConfig config = GenerationConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(
                new Content.Builder().addPart(prompt).build(), config
        );

        addJsonCallback(future, Recipe.class, callback::onSuccess, callback::onError);
    }

    /**
     * React의 getProteinFoodSuggestions 구현
     */
    public void getProteinFoodSuggestions(List<Meal> meals, ProteinSuggestionCallback callback) {
        String mealHistory = meals.stream().map(Meal::getFoodItem).collect(Collectors.joining(", "));
        String prompt = String.format("사용자의 오늘 식단은 다음과 같습니다: %s. 현재 단백질 섭취가 부족한 상황입니다. 사용자가 식단에 간단하게 추가할 수 있는, 단백질이 풍부한 음식이나 간식 4가지를 추천해주세요. JSON 스키마에 맞춰 응답해주세요.", (mealHistory.isEmpty() ? "아직 기록 없음" : mealHistory));

        // proteinSuggestionSchema (배열)
        Schema<List<ProteinFoodSuggestion>> schema = Schema.fromType(new TypeToken<List<ProteinFoodSuggestion>>(){});
        GenerationConfig config = GenerationConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(
                new Content.Builder().addPart(prompt).build(), config
        );

        addJsonCallback(future, new TypeToken<List<ProteinFoodSuggestion>>(){}.getType(), callback::onSuccess, callback::onError);
    }

    // ... getAIGroupChallengeSuggestions, getFridgeRecipe 등도 유사하게 구현 ...

    /**
     * React의 createChatSession 구현
     */
    public void startChatSession(List<Meal> meals) {
        String mealHistory = meals.stream().limit(20)
                .map(m -> String.format("- %s %s: %dkcal", m.getDate().toString(), m.getTime().getDisplayName(), m.getKcal()))
                .collect(Collectors.joining("\n"));

        String systemInstruction = "당신은 사용자의 건강 데이터를 잘 아는 전문 영양사 'HealthMate'입니다...\n" +
                "아래는 사용자의 최근 식단 기록입니다.\n---\n" +
                (mealHistory.isEmpty() ? "아직 식단 기록이 없습니다." : mealHistory) +
                "\n---";

        chatSession = generativeModel.startChat(
                new Content.Builder().addPart(systemInstruction).build()
        );
    }

    /**
     * AI 채팅 메시지 전송
     */
    public void sendMessageToChat(String message, TextCallback callback) {
        if (chatSession == null) {
            callback.onError(new IllegalStateException("채팅 세션이 시작되지 않았습니다."));
            return;
        }

        ListenableFuture<GenerateContentResponse> future = chatSession.sendMessage(
                new Content.Builder().addPart(message).build()
        );
        addTextCallback(future, callback);
    }


    // --- 유틸리티 헬퍼 함수 ---

    // 단순 텍스트 응답 처리
    private void generateText(String prompt, TextCallback callback) {
        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(
                new Content.Builder().addPart(prompt).build()
        );
        addTextCallback(future, callback);
    }

    // 비동기 텍스트 콜백 추가
    private void addTextCallback(ListenableFuture<GenerateContentResponse> future, TextCallback callback) {
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                callback.onSuccess(result.getText());
            }
            @Override
            public void onFailure(Throwable t) {
                callback.onError((Exception) t);
            }
        }, executor);
    }

    // 비동기 JSON 콜백 추가 (Gson 파싱)
    private <T> void addJsonCallback(ListenableFuture<GenerateContentResponse> future, java.lang.reflect.Type type, java.util.function.Consumer<T> onSuccess, java.util.function.Consumer<Exception> onError) {
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                try {
                    String jsonText = result.getText();
                    T parsedResult = gson.fromJson(jsonText, type);
                    onSuccess.accept(parsedResult);
                } catch (Exception e) {
                    onError.accept(e); // JSON 파싱 실패
                }
            }
            @Override
            public void onFailure(Throwable t) {
                onError.accept((Exception) t);
            }
        }, executor);
    }

    // Overload for simple classes
    private <T> void addJsonCallback(ListenableFuture<GenerateContentResponse> future, Class<T> clazz, java.util.function.Consumer<T> onSuccess, java.util.function.Consumer<Exception> onError) {
        addJsonCallback(future, (java.lang.reflect.Type) clazz, onSuccess, onError);
    }

    // TDEE 계산 로직 (UserProfile.java로 이동하는 것이 좋음)
    private int calculateTDEE(UserProfile profile) {
        // ... (Mifflin-St Jeor Equation 구현)
        return 2000; // 예시
    }
}