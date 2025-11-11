package com.example.healthmate.view.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.example.healthmate.R;
import com.example.healthmate.adapter.PlannedMealAdapter;
import com.example.healthmate.viewmodel.AnalysisViewModel;
import com.example.healthmate.viewmodel.HomeViewModel; // HomeViewModel (식단 기록용)

public class PlannerTabFragment extends Fragment
        implements PlannedMealAdapter.OnLogMealListener {

    private AnalysisViewModel analysisViewModel;
    private HomeViewModel homeViewModel; // 식단 기록은 HomeViewModel이 담당

    // 13.2 XML 뷰
    private TextInputEditText etPreferences;
    private Button btnGeneratePlan;
    private ProgressBar progressBarPlan;
    private RecyclerView recyclerViewPlan;

    private PlannedMealAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 13.2에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_tab_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel 연결 (AnalysisViewModel은 부모와, HomeViewModel은 Activity와 공유)
        analysisViewModel = new ViewModelProvider(requireParentFragment()).get(AnalysisViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // 뷰 ID 연결
        etPreferences = view.findViewById(R.id.etPreferences);
        btnGeneratePlan = view.findViewById(R.id.btnGeneratePlan);
        progressBarPlan = view.findViewById(R.id.progressBarPlan);
        recyclerViewPlan = view.findViewById(R.id.recyclerViewPlan);

        // 어댑터 설정
        adapter = new PlannedMealAdapter(this);
        recyclerViewPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPlan.setAdapter(adapter);

        // "AI 플랜 생성하기" 버튼 리스너
        btnGeneratePlan.setOnClickListener(v -> {
            String prefs = etPreferences.getText().toString().trim();
            analysisViewModel.generateMealPlan(prefs);
        });

        // --- ViewModel 관찰 ---

        // 로딩 상태 관찰 (isLoading)
        analysisViewModel.isGeneratingPlan().observe(getViewLifecycleOwner(), isLoading -> {
            progressBarPlan.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnGeneratePlan.setEnabled(!isLoading);
            btnGeneratePlan.setText(isLoading ? "AI가 식단을 짜는 중..." : "AI 플랜 생성하기");
        });

        // 식단 계획 결과 관찰 (mealPlan)
        analysisViewModel.getMealPlan().observe(getViewLifecycleOwner(), mealPlan -> {
            adapter.setMealPlan(mealPlan); // 어댑터에 데이터 전달
        });
    }

    /**
     * "기록" 버튼 클릭 시 호출 (onLogMeal)
     *
     */
    @Override
    public void onLogMealClick(String foodItem) {
        // 6단계에서 만든 HomeFragment의 onMealAdd와 동일한 로직 호출
        // (단, 여기서는 기본 '점심'으로 설정하거나, 시간 선택 다이얼로그를 띄워야 함)
        homeViewModel.addManualMeal(foodItem, com.healthmate.model.Meal.MealTime.LUNCH);

        Toast.makeText(getContext(), foodItem + "을(를) 기록했습니다.", Toast.LENGTH_SHORT).show();
    }
}