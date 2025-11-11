package com.example.healthmate.view.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthmate.R;
import com.example.healthmate.viewmodel.AnalysisViewModel; // 11.1에서 만든 ViewModel

public class AnalysisTabFragment extends Fragment {

    private AnalysisViewModel viewModel;

    // 12.2 XML의 뷰들
    private View cardAiExercise;
    private TextView tvExerciseTitle;
    private TextView tvExerciseContent;
    private ProgressBar progressExercise;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 12.2에서 만든 실제 레이아웃으로 교체
        return inflater.inflate(R.layout.fragment_tab_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모 Fragment(AnalysisFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(AnalysisViewModel.class);

        // --- 1. AI 운동 카드 뷰 연결 ---
        // <include> 태그로 가져온 뷰는 ID로 찾아야 함
        cardAiExercise = view.findViewById(R.id.cardAiExercise);

        // include된 레이아웃 내부의 뷰들
        tvExerciseTitle = cardAiExercise.findViewById(R.id.tvCardTitle);
        tvExerciseContent = cardAiExercise.findViewById(R.id.tvCardContent);
        progressExercise = cardAiExercise.findViewById(R.id.progressBar);

        // --- 2. AI 운동 카드 데이터 관찰 ---
        // React의 (isLoading)
        viewModel.getWorkoutPlan().observe(getViewLifecycleOwner(), workoutPlan -> {
            if (workoutPlan == null || workoutPlan.contains("실패")) {
                tvExerciseTitle.setText("AI 주간 운동 플랜");
                tvExerciseContent.setText(workoutPlan != null ? workoutPlan : "로딩 중...");
                progressExercise.setVisibility(workoutPlan == null ? View.VISIBLE : View.GONE);
            } else {
                tvExerciseTitle.setText("AI 주간 운동 플랜");
                tvExerciseContent.setText(workoutPlan); // AI가 생성한 텍스트
                progressExercise.setVisibility(View.GONE);
            }
        });

        // --- 3. AI 레시피 카드 데이터 관찰 ---
        // (cardAiRecipe 뷰들을 찾고, viewModel.getRecipe()를 observe)
        // ...

        // --- 4. 칼로리 트렌드 차트 설정 ---
        // (chartCalorieTrend 뷰를 찾고, viewModel의 차트 데이터를 observe하여 MPAndroidChart 설정)
        // ...
    }
}