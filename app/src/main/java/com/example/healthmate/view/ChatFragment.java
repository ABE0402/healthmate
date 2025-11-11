package com.example.healthmate.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.adapter.ChatAdapter;
import com.example.healthmate.model.ChatMessage;
import com.example.healthmate.viewmodel.ChatViewModel;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private ChatViewModel viewModel;
    private RecyclerView recyclerViewChat;
    private ChatAdapter adapter;
    private EditText etMessage;
    private Button btnSend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 15.4에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel 초기화 (Activity와 공유)
        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        // 뷰 ID 연결
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        // 어댑터 및 RecyclerView 설정
        adapter = new ChatAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // 새 메시지가 하단에 오도록
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(adapter);

        // 전송 버튼 리스너 (React의 onSubmit)
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                etMessage.setText(""); // 입력창 비우기
            }
        });

        // ViewModel 관찰 (메시지 리스트 업데이트)
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.submitList(new ArrayList<>(messages)); // (방어적 복사)

            // React의 scrollToBottom
            recyclerViewChat.scrollToPosition(messages.size() - 1);
        });
    }
}