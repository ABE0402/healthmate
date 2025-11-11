package com.example.healthmate.service;

import android.graphics.Bitmap;
import com.example.healthmate.BuildConfig;
import com.example.healthmate.model.*;
import com.example.healthmate.utils.CalculationUtils;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.ImagePart;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.Schema;
import com.google.ai.client.generativeai.type.TextPart;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GeminiService {

    private String prompt;
    Part textPart = new TextPart(prompt);
    private Bitmap bitmap;
    Part imagePart = new ImagePart(bitmap);


    private final GenerativeModelFutures generativeModel;
    private ChatFutures chatSession;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    public interface AnalysisCallback {
        void onSuccess(List<AnalysisResult> results);
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

    public GeminiService() {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.2f;
        GenerationConfig generationConfig = configBuilder.build();

        GenerativeModel model = new GenerativeModel(
                "gemini-1.5-flash",
                BuildConfig.GEMINI_API_KEY,
                generationConfig,
                null
        );
        generativeModel = GenerativeModelFutures.from(model);
    }

    private GenerativeModelFutures getModelWithJsonConfig(Schema schema) {
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";
        configBuilder.responseSchema = schema;
        GenerationConfig config = configBuilder.build();

        GenerativeModel model = new GenerativeModel(
                "gemini-1.5-flash",
                BuildConfig.GEMINI_API_KEY,
                config,
                null
        );
        return GenerativeModelFutures.from(model);
    }

    public void analyzeImageWithGemini(Bitmap image, AnalysisCallback callback) {
        String prompt = "이 음식 사진을 분석해서 각 음식 항목의 이름, 1인분 기준 양(g), 총 칼로리(kcal), 그리고 주요 영양소(탄수화물, 단백질, 지방) 함량(g)을 알려줘. 가능한 한 자세하게, 여러 음식이 있다면 각각을 항목으로 만들어줘. JSON 배열 형식으로 응답해줘.";

        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .addPart(new ImagePart(image))
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        addJsonCallback(future, new TypeToken<List<AnalysisResult>>() {}.getType(), callback::onSuccess, callback::onError);
    }



    public void analyzeTextWithGemini(String foodItem, AnalysisCallback callback) {
        String prompt = String.format(
                "음식 '%s'에 대한 일반적인 1인분 기준 영양 정보를 알려줘. 음식 이름, 1인분 기준 양(g), 칼로리(kcal), 그리고 탄수화물, 단백질, 지방 함량(g)을 포함해서 JSON 배열 형식으로 응답해줘. 예: [{...}]",
                foodItem
        );

        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        addJsonCallback(future, new TypeToken<List<AnalysisResult>>() {}.getType(), callback::onSuccess, callback::onError);
    }


    public void getAIMealPlan(UserProfile userProfile, String preferences, MealPlanCallback callback) {
        int dailyGoal = CalculationUtils.calculateTDEE(userProfile);

        String prompt = String.format(
                "사용자의 프로필 정보는 다음과 같습니다:\n" +
                        "- 나이: %d세\n- 성별: %s\n- 일일 목표 칼로리: 약 %dkcal\n" +
                        "사용자의 추가적인 식단 요구사항: \"%s\"\n\n" +
                        "위 정보를 바탕으로 건강한 하루 식단(아침/점심/저녁/간식)을 JSON 형식으로 만들어주세요.\n" +
                        "예시: {\"breakfast\": {...}, \"lunch\": {...}, \"dinner\": {...}, \"snacks\": [...] }",
                userProfile.getAge(),
                userProfile.getGender().name(),
                dailyGoal,
                preferences.isEmpty() ? "특별한 요구사항 없음" : preferences
        );

        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        addJsonCallback(future, MealPlan.class, callback::onSuccess, callback::onError);
    }


    public void getExerciseSuggestion(int surplusKcal, int userWeight, TextCallback callback) {
        String prompt = String.format("사용자의 현재 체중은 %dkg이고, 오늘 목표보다 %dkcal를 초과하여 섭취했습니다.\n이 초과 칼로리를 소모할 수 있는 효과적인 운동 루틴을 추천해주세요.\n- 구체적인 운동 종류와 시간을 포함해주세요. (예: 30분 빠르게 걷기 + 15분 스쿼트)\n- 전문적이면서도 동기부여가 되는 친근한 말투로 작성해주세요.\n- 답변은 1-2 문장으로 간결하게 요약해주세요.", userWeight, surplusKcal);
        generateText(prompt, callback);
    }

    public void getAIFeedback(List<Meal> meals, TextCallback callback) {
        String mealHistory = meals.stream().limit(15).map(m -> String.format("- %s %s: %dkcal", m.getMealType(), m.getFoodName(), m.getCalories())).collect(Collectors.joining("\n"));
        String prompt = "다음은 사용자의 최근 식단 기록입니다.\n" + mealHistory + "\n이 기록을 바탕으로 전문 영양사의 관점에서 사용자의 식습관을 분석하고, 칭찬할 점과 개선할 점을 찾아 구체적인 조언을 해주세요.\n답변은 친근하고 이해하기 쉬운 말투로, 2-3문장으로 요약해서 제공해주세요.";
        generateText(prompt, callback);
    }

    public void getAIWorkoutPlan(List<Meal> allMeals, UserProfile userProfile, TextCallback callback) {
        if (userProfile == null) {
            callback.onError(new IllegalArgumentException("사용자 프로필이 필요합니다."));
            return;
        }
        String mealHistory = (allMeals == null || allMeals.isEmpty()) ? "기록 없음" : allMeals.stream().limit(10).map(m -> String.format("- %s %s: %dkcal", m.getDate().toString(), m.getFoodName(), m.getCalories())).collect(Collectors.joining("\n"));
        String profileInfo = String.format("나이: %d, 성별: %s, 키: %.0fcm, 몸무게: %dkg, 활동 수준: %s", userProfile.getAge(), userProfile.getGender().name(), userProfile.getHeight(), userProfile.getWeight(), userProfile.getActivityLevel().name());
        String prompt = "당신은 사용자의 데이터를 분석하여 맞춤형 운동 계획을 제공하는 전문 AI 피트니스 코치입니다.\n\n## 사용자 정보\n" + profileInfo + "\n\n## 최근 식단 기록 (최대 10개)\n" + mealHistory + "\n\n## 요청사항\n위 사용자의 프로필과 최근 식단을 바탕으로, 건강 증진과 체중 관리를 목표로 하는 **주간 운동 계획**을 세워주세요.\n아래 가이드라인을 따라 답변을 생성해주세요.\n1.  **구체적인 계획**: 요일별(월요일-일요일)로 다른 운동 루틴을 추천해주세요.\n2.  **운동 상세**: 각 루틴에 포함될 운동 종류, 세트, 횟수 또는 시간을 명시해주세요. (예: 스쿼트 3세트 x 15회, 30분 달리기)\n3.  **균형**: 유산소 운동과 근력 운동을 적절히 조합해주세요.\n4.  **가독성**: 답변은 마크다운(Markdown)을 사용하여 명확하고 읽기 쉽게 작성해주세요.\n5.  **격려 메시지**: 계획 마지막에 사용자에게 동기를 부여하는 짧은 응원 메시지를 추가해주세요.";
        generateText(prompt, callback);
    }

    public void getAIChallenge(List<Meal> meals, AIChallengeCallback callback) {
        String mealHistory = meals.stream()
                .limit(10)
                .map(m -> String.format("- %s %s: %dkcal",
                        m.getMealType(), m.getFoodName(), m.getCalories()))
                .collect(Collectors.joining("\n"));

        String prompt =
                "당신은 건강 코치입니다.\n" +
                        "최근 식단 기록:\n" + mealHistory + "\n" +
                        "이를 분석하여 사용자가 해볼만한 구체적 건강 챌린지를 1개 만들어주세요.\n" +
                        "JSON 예: {\"title\": \"물 많이 마시기\", \"description\": \"하루 2L 물 마시기\"}";

        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        addJsonCallback(future, AIChallenge.class, callback::onSuccess, callback::onError);
    }

    public void getAIRecipe(List<Meal> meals, RecipeCallback callback) {
        String mealHistory = meals.stream()
                .limit(5)
                .map(Meal::getFoodName)
                .collect(Collectors.joining(", "));

        String prompt = String.format(
                "최근 식단: %s. 이를 기반으로 쉬운 건강 레시피 1개를 JSON 형태로 만들어주세요.\n" +
                        "예: {\"name\":\"샐러드\", \"ingredients\":[...], \"instructions\":\"...\"}",
                mealHistory
        );

        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        addJsonCallback(future, Recipe.class, callback::onSuccess, callback::onError);
    }


    public void getProteinFoodSuggestions(List<Meal> meals, ProteinSuggestionCallback callback) {
        String mealHistory = meals.stream()
                .map(Meal::getFoodName)
                .collect(Collectors.joining(", "));

        String prompt = String.format(
                "오늘 식단: %s. 단백질 추가 추천 음식 4개를 JSON 배열로 만들어주세요.\n" +
                        "예: [{\"food\":\"닭가슴살\", \"protein\":27, \"calories\":130}]",
                mealHistory.isEmpty() ? "기록 없음" : mealHistory
        );

        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        addJsonCallback(future, new TypeToken<List<ProteinFoodSuggestion>>() {}.getType(), callback::onSuccess, callback::onError);
    }


    public void startChatSession(List<Meal> meals) {
        // 최근 식단 20개 가져오기
        String mealHistory = meals.stream()
                .limit(20)
                .map(m -> String.format("- %s %s: %dkcal",
                        m.getDate().toString(), m.getMealType(), m.getCalories()))
                .collect(Collectors.joining("\n"));

        // 시스템 메시지 (사용자 역할)
        String systemInstruction = "당신은 사용자의 건강 데이터를 잘 아는 전문 영양사 'HealthMate'입니다. "
                + "사용자의 질문에 친절하고 상세하게 답변해주세요. "
                + "사용자의 식단 기록을 참고하여 개인화된 조언을 제공할 수 있습니다.\n"
                + "아래는 사용자의 식단 기록입니다.\n---\n"
                + (mealHistory.isEmpty() ? "아직 식단 기록이 없습니다." : mealHistory)
                + "\n---";

        // history 리스트 생성
        List<Content> history = new ArrayList<>();

        // 사용자 메시지 (역할: user)
        List<Part> userParts = new ArrayList<>();
        userParts.add(new TextPart(systemInstruction));
        Content userContent = new Content("user", userParts);
        history.add(userContent);

        // 모델 응답 (역할: model)
        List<Part> modelParts = new ArrayList<>();
        modelParts.add(new TextPart("네, 안녕하세요! HealthMate입니다. 무엇을 도와드릴까요?"));
        Content modelContent = new Content("model", modelParts);
        history.add(modelContent);

        // Gemini와 채팅 세션 시작
        chatSession = generativeModel.startChat(history);
    }



    public void sendMessageToChat(String message, TextCallback callback) {
        if (chatSession == null) {
            callback.onError(new IllegalStateException("채팅 세션이 시작되지 않았습니다. startChatSession()을 먼저 호출해주세요."));
            return;
        }

        // 사용자가 입력한 message를 TextPart로 감싸기
        List<Part> parts = new ArrayList<>();
        parts.add(new TextPart(message));

        // Content(role="user", parts=[message])
        Content content = new Content("user", parts);

        // 이제 Content를 전송
        ListenableFuture<GenerateContentResponse> future = chatSession.sendMessage(content);

        addTextCallback(future, callback);
    }


    private void generateText(String prompt, TextCallback callback) {
        Content content = new Content.Builder()
                .addPart(new TextPart(prompt))
                .build();

        ListenableFuture<GenerateContentResponse> future = generativeModel.generateContent(content);
        addTextCallback(future, callback);
    }


    private void addTextCallback(ListenableFuture<GenerateContentResponse> future, TextCallback callback) {
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                try {
                    String text = result.getText();
                    if (text == null) {
                        throw new Exception("Received empty or invalid response from Gemini.");
                    }
                    callback.onSuccess(text);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                callback.onError((Exception) t);
            }
        }, executor);
    }

    private <T> void addJsonCallback(ListenableFuture<GenerateContentResponse> future, java.lang.reflect.Type type, Consumer<T> onSuccess, Consumer<Exception> onError) {
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                try {
                    String jsonText = extractTextFromResponse(result);
                    if (jsonText == null || jsonText.trim().isEmpty()) {
                        throw new Exception("Gemini 응답에서 JSON 텍스트를 찾을 수 없습니다.");
                    }

                    T parsed = gson.fromJson(jsonText, type);
                    onSuccess.accept(parsed);
                } catch (JsonSyntaxException e) {
                    onError.accept(new Exception("JSON 파싱 실패: " + e.getMessage(), e));
                } catch (Exception e) {
                    onError.accept(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onError.accept((Exception) t);
            }
        }, executor);
    }




    private String extractTextFromResponse(GenerateContentResponse result) throws Exception {
        // Content 필드가 접근 안 되는 경우를 대비해 우회 방식 사용
        try {
            // Kotlin SDK에서 content 필드에 접근 가능할 경우
            java.lang.reflect.Method getContentMethod = result.getClass().getMethod("getContent");
            Object contentObj = getContentMethod.invoke(result);

            if (contentObj != null) {
                java.lang.reflect.Method getPartsMethod = contentObj.getClass().getMethod("getParts");
                List<?> parts = (List<?>) getPartsMethod.invoke(contentObj);

                for (Object part : parts) {
                    if (part instanceof com.google.ai.client.generativeai.type.TextPart) {
                        return ((com.google.ai.client.generativeai.type.TextPart) part).getText();
                    }
                }
            }
        } catch (Exception e) {
            // 메서드 접근 불가 시 fallback
            System.err.println("getContent() 우회 접근 실패: " + e.getMessage());
        }

        // 최후 수단: 전체 객체 문자열 출력
        return result.toString(); // JSON 파싱은 실패할 수 있음
    }

}