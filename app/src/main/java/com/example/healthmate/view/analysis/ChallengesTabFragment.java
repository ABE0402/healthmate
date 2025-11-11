package com.example.healthmate.view.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.adapter.ChallengeAdapter;
import com.example.healthmate.model.AIChallenge;
import com.example.healthmate.viewmodel.AnalysisViewModel;

import java.util.ArrayList;

public class ChallengesTabFragment extends Fragment {

    private AnalysisViewModel viewModel;

    // AI 챌린지 카드 뷰
    private Button btnRefreshChallenge;
    private ProgressBar progressAiChallenge;
    private LinearLayout layoutAiChallenge;
    private ImageView ivAiChallengeIcon;
    private TextView tvAiChallengeTitle, tvAiChallengeDesc;

    // 장기 목표 리스트 뷰
    private RecyclerView recyclerViewChallenges;
    private ChallengeAdapter challengeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 14.5에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_tab_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모 Fragment(AnalysisFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(AnalysisViewModel.class);

        // --- 뷰 ID 연결 ---
        btnRefreshChallenge = view.findViewById(R.id.btnRefreshChallenge);
        progressAiChallenge = view.findViewById(R.id.progressAiChallenge);
        layoutAiChallenge = view.findViewById(R.id.layoutAiChallenge);
        ivAiChallengeIcon = view.findViewById(R.id.ivAiChallengeIcon);
        tvAiChallengeTitle = view.findViewById(R.id.tvAiChallengeTitle);
        tvAiChallengeDesc = view.findViewById(R.id.tvAiChallengeDesc);
        recyclerViewChallenges = view.findViewById(R.id.recyclerViewChallenges);

        // --- 어댑터 설정 ---
        challengeAdapter = new ChallengeAdapter(new ArrayList<>());
        recyclerViewChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChallenges.setAdapter(challengeAdapter);

        // --- 리스너 설정 ---
        btnRefreshChallenge.setOnClickListener(v -> {
            // React의 onGenerateNew
            viewModel.fetchAIChallenge();
        });

        // --- ViewModel 관찰 ---

        // 1. AI 챌린지 로딩 상태
        viewModel.isFetchingAiChallenge().observe(getViewLifecycleOwner(), isLoading -> {
            progressAiChallenge.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            layoutAiChallenge.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            btnRefreshChallenge.setEnabled(!isLoading);
        });

        // 2. AI 챌린지 데이터
        viewModel.getAiChallenge().observe(getViewLifecycleOwner(), aiChallenge -> {
            if (aiChallenge != null) {
                tvAiChallengeTitle.setText(aiChallenge.getTitle());
                tvAiChallengeDesc.setText(aiChallenge.getDescription());
                // (아이콘 설정 로직 추가...)
            }
        });

        // 3. 장기 챌린지 리스트
        viewModel.getLongTermChallenges().observe(getViewLifecycleOwner(), challenges -> {
            if (challenges != null) {
                challengeAdapter.submitList(challenges);
            }
        });
    }
}