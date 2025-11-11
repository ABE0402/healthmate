package com.example.healthmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthmate.model.ChatMessage;
import com.example.healthmate.model.Meal;
import com.example.healthmate.service.GeminiService; // 4단계에서 만든 서비스

import java.util.ArrayList;
import java.util.List;

// React의 ChatPage.tsx 로직
public class ChatViewModel extends ViewModel {

    private final GeminiService geminiService = new GeminiService();

    private final MutableLiveData<List<ChatMessage>> _messages = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<ChatMessage>> getMessages() { return _messages; }

    private List<Meal> allMeals; // (Activity/Repository로부터 받아야 함)

    public ChatViewModel() {
        // (allMeals는 Repository에서 로드해야 함)
        this.allMeals = new ArrayList<>();

        // 4단계에서 만든 GeminiService 함수 호출
        geminiService.startChatSession(allMeals);

        // React의 초기 메시지
        addMessage(new ChatMessage(
                "안녕하세요! AI 영양사 HealthMate입니다. 식단, 운동 등 건강에 대해 무엇이든 물어보세요.",
                ChatMessage.Role.MODEL
        ));
    }

    /**
     * React의 handleSendMessage
     */
    public void sendMessage(String text) {
        // 1. 사용자 메시지 추가
        addMessage(new ChatMessage(text, ChatMessage.Role.USER));

        // 2. 로딩 인디케이터 추가
        addMessage(new ChatMessage(ChatMessage.Role.MODEL, true));

        // 3. AI에 메시지 전송
        geminiService.sendMessageToChat(text, new GeminiService.TextCallback() {
            @Override
            public void onSuccess(String textResponse) {
                // 4. 로딩 인디케이터 제거 및 AI 응답 추가
                replaceLoadingMessage(new ChatMessage(textResponse, ChatMessage.Role.MODEL));
            }

            @Override
            public void onError(Exception e) {
                // 4. 로딩 인디케이터 제거 및 에러 메시지 추가
                replaceLoadingMessage(new ChatMessage(
                        "죄송합니다, 답변을 생성하는 중 오류가 발생했어요.",
                        ChatMessage.Role.MODEL
                ));
            }
        });
    }

    // --- LiveData 헬퍼 함수 ---
    private void addMessage(ChatMessage message) {
        List<ChatMessage> current = _messages.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(message);
        _messages.setValue(current); // LiveData 업데이트
    }

    private void replaceLoadingMessage(ChatMessage message) {
        List<ChatMessage> current = _messages.getValue();
        if (current == null || current.isEmpty()) return;

        // 마지막 메시지(로딩) 제거
        current.remove(current.size() - 1);
        // AI 응답 추가
        current.add(message);
        _messages.setValue(current); // LiveData 업데이트
    }
}