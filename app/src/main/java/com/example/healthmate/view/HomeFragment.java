package com.example.healthmate.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.adapter.MealAdapter;
import com.example.healthmate.model.AnalysisResult;
import com.example.healthmate.model.Meal;
import com.example.healthmate.model.Nutrients;
import com.example.healthmate.model.SuggestionData;
import com.example.healthmate.viewmodel.HomeViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements MealAdapter.OnDeleteClickListener, ManualAddDialogFragment.OnMealAddListener, AnalysisDialogFragment.OnConfirmListener{

    private HomeViewModel viewModel;

    // 2단계에서 만든 XML의 뷰들
    private TextView tvTotalKcal;
    private TextView tvGoalKcal;
    private ProgressBar progressBarKcal;
    private PieChart pieChartMacros;

    // 5단계에서 추가된 뷰
    private RecyclerView recyclerViewMeals;
    private TextView tvEmptyMessage;
    private Button btnManualAdd;
    private MealAdapter mealAdapter;

    // --- 8단계: 수분 섭취 뷰 ---
    private TextView tvWaterIntake, tvWaterGoal;
    private ImageButton btnWaterMinus, btnWaterPlus;
    private LinearLayout layoutWaterCups;
    private List<View> waterCupViews = new ArrayList<>(); // 물컵 뷰들을 저장할 리스트

    // --- 9단계: AI 제안 카드 뷰 ---
    private ProgressBar progressSuggestion;
    private CardView cardIconBg;
    private ImageView ivSuggestionIcon;
    private LinearLayout layoutSuggestionText;
    private TextView tvSuggestionTitle, tvSuggestionDesc, btnSuggestionCta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 2단계에서 만든 XML 레이아웃을 화면으로 inflate(변환)
        // 여기서는 component_dashboard.xml이 Fragment 전체 화면이라고 가정
        View view = inflater.inflate(R.layout.component_dashboard, container, false);

        // XML 뷰 ID로 Java 객체 연결
        tvTotalKcal = view.findViewById(R.id.tvTotalKcal);
        tvGoalKcal = view.findViewById(R.id.tvGoalKcal);
        progressBarKcal = view.findViewById(R.id.progressBarKcal);
        pieChartMacros = view.findViewById(R.id.pieChartMacros);


        recyclerViewMeals = view.findViewById(R.id.recyclerViewMeals);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        btnManualAdd = view.findViewById(R.id.btnManualAdd);

        // --- 8단계 뷰 findViewById ---
        tvWaterIntake = view.findViewById(R.id.tvWaterIntake);
        tvWaterGoal = view.findViewById(R.id.tvWaterGoal);
        btnWaterMinus = view.findViewById(R.id.btnWaterMinus);
        btnWaterPlus = view.findViewById(R.id.btnWaterPlus);
        layoutWaterCups = view.findViewById(R.id.layoutWaterCups);

        // --- 9단계 뷰 findViewById ---
        progressSuggestion = view.findViewById(R.id.progressSuggestion);
        cardIconBg = view.findViewById(R.id.cardIconBg);
        ivSuggestionIcon = view.findViewById(R.id.ivSuggestionIcon);
        layoutSuggestionText = view.findViewById(R.id.layoutSuggestionText);
        tvSuggestionTitle = view.findViewById(R.id.tvSuggestionTitle);
        tvSuggestionDesc = view.findViewById(R.id.tvSuggestionDesc);
        btnSuggestionCta = view.findViewById(R.id.btnSuggestionCta);


        // 8개의 물컵 뷰를 동적으로 생성
        setupWaterCups(inflater);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // 어댑터 초기화 (삭제 리스너로 'this' Fragment 전달)
        mealAdapter = new MealAdapter(this);
        recyclerViewMeals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMeals.setAdapter(mealAdapter);

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // --- 8단계: 리스너 및 관찰자 설정 ---
        btnWaterMinus.setOnClickListener(v -> viewModel.updateWaterIntake(-1));
        btnWaterPlus.setOnClickListener(v -> viewModel.updateWaterIntake(1));

        // ViewModel의 dashboardData를 관찰(observe)
        // 데이터가 변경되면 중괄호 안의 코드가 실행됨
        viewModel.dashboardData.observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            // --- React의 렌더링 로직을 Java로 구현 ---

            // 1. 칼로리 텍스트 업데이트
            tvTotalKcal.setText(String.format(Locale.getDefault(), "%,d", data.totalKcal));
            tvGoalKcal.setText(String.format(Locale.getDefault(), "/ %,d kcal", data.goalKcal));

            // 2. 프로그레스 바 업데이트
            int progress = (data.goalKcal > 0) ? (int)((double)data.totalKcal / data.goalKcal * 100) : 0;
            progressBarKcal.setProgress(Math.min(progress, 100));
            // (isOverGoal일 때 색상 변경 로직은 progress_bar_custom.xml에서 처리)

            // 3. 파이 차트 업데이트
            setupPieChart(data.macros);
        });

        // 5단계: 식단 리스트 데이터 관찰
        // (HomeViewModel에 todayMeals LiveData가 추가되었다고 가정)
        viewModel.todayMeals.observe(getViewLifecycleOwner(), meals -> {
            if (meals == null || meals.isEmpty()) {
                // React의 (meals.length === 0)
                recyclerViewMeals.setVisibility(View.GONE);
                tvEmptyMessage.setVisibility(View.VISIBLE);
            } else {
                recyclerViewMeals.setVisibility(View.VISIBLE);
                tvEmptyMessage.setVisibility(View.GONE);
                mealAdapter.submitList(meals);
            }
        });

        // 분석이 시작되면(isAnalyzing=true) 모달을 띄움
        viewModel.getIsAnalyzingImage().observe(getViewLifecycleOwner(), isAnalyzing -> {
            if (isAnalyzing) {
                AnalysisDialogFragment dialog = new AnalysisDialogFragment();
                dialog.show(getChildFragmentManager(), "AnalysisDialog");
            }
        });

        // 수동 추가 버튼 리스너 (React의 onOpenManualAddModal)
        btnManualAdd.setOnClickListener(v -> {
            // React의 onOpenManualAddModal()
            ManualAddDialogFragment dialog = ManualAddDialogFragment.newInstance();
            // HomeFragment를 부모로 설정 (리스너 연결용)
            dialog.show(getChildFragmentManager(), "ManualAddDialog");
        });

        // 수분 섭취량 LiveData 관찰
        viewModel.getWaterIntake().observe(getViewLifecycleOwner(), intake -> {
            updateWaterUI(intake);
        });

        // --- 9단계: AI 제안 관찰자 ---
        viewModel.getSuggestion().observe(getViewLifecycleOwner(), suggestion -> {
            if (suggestion == null) return;

            // React의 isLoading 상태 처리
            if (suggestion.getIconType() == SuggestionData.IconType.LOADING) {
                progressSuggestion.setVisibility(View.VISIBLE);
                layoutSuggestionText.setVisibility(View.GONE);
                btnSuggestionCta.setVisibility(View.GONE);
                cardIconBg.setVisibility(View.GONE);
            } else {
                progressSuggestion.setVisibility(View.GONE);
                layoutSuggestionText.setVisibility(View.VISIBLE);
                btnSuggestionCta.setVisibility(View.VISIBLE);
                cardIconBg.setVisibility(View.VISIBLE);

                tvSuggestionTitle.setText(suggestion.getTitle());
                tvSuggestionDesc.setText(suggestion.getDescription());
                btnSuggestionCta.setText(suggestion.getCtaText());

                // React의 iconStyles 로직
                updateSuggestionIcon(suggestion.getIconType());
            }
        });

        // 3단계의 dashboardData 관찰자 수정
        viewModel.dashboardData.observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;
            // ... (기존 3단계 코드: tvTotalKcal.setText 등) ...

            // --- 9단계 추가 ---
            // 데이터 변경 시 AI 제안 갱신
            int userWeight = 75; // 예시
            viewModel.updateSuggestion(data, userWeight);
        });
    }

    @Override
    public void onConfirm(List<AnalysisResult> results, Meal.MealTime time) {
        // React의 onConfirm() -> handleAddToLog()
        // ViewModel에 식단 추가 로직 호출
        viewModel.addAnalyzedMeals(results, time);

        Toast.makeText(getContext(), results.size() + "개의 항목이 식단에 추가되었습니다!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDeleteClick(Meal meal) {
        // React의 handleDeleteMeal
        // ViewModel에 삭제 요청
        viewModel.deleteMeal(meal);
        // (Toast 띄우기)
        Toast.makeText(getContext(), "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onMealAdd(String foodItem, Meal.MealTime time) {
        // React의 handleAddManualMeal prop
        // ViewModel에 텍스트 분석 및 식단 추가 요청
        // (HomeViewModel에 addManualMeal 함수가 구현되어 있다고 가정)
        viewModel.addManualMeal(foodItem, time);
    }

    // --- 8단계: 헬퍼 함수 ---

    /**
     * 8개의 물컵 뷰를 layoutWaterCups에 동적으로 추가
     */
    private void setupWaterCups(LayoutInflater inflater) {
        layoutWaterCups.removeAllViews(); // 기존 뷰 초기화
        waterCupViews.clear();

        int cupSize = (int) (getResources().getDisplayMetrics().density * 32); // 32dp
        int margin = (int) (getResources().getDisplayMetrics().density * 4); // 4dp

        for (int i = 0; i < HomeViewModel.DAILY_WATER_GOAL; i++) {
            // React의 Array.from({ length: goal })
            View cupView = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, cupSize, 1.0f); // weight=1
            params.setMargins(margin, margin, margin, margin);
            cupView.setLayoutParams(params);

            layoutWaterCups.addView(cupView);
            waterCupViews.add(cupView); // 리스트에 추가
        }
    }

    /**
     * LiveData 변경 시 수분 섭취 UI 업데이트
     */
    private void updateWaterUI(int intake) {
        tvWaterIntake.setText(String.valueOf(intake));

        // React의 isGoalReached 로직
        if (intake >= HomeViewModel.DAILY_WATER_GOAL) {
            tvWaterGoal.setText("목표 달성! 훌륭해요! 🎉");
        } else {
            tvWaterGoal.setText(String.format(Locale.getDefault(), "목표: %d잔", HomeViewModel.DAILY_WATER_GOAL));
        }

        // + / - 버튼 활성화/비활성화
        btnWaterMinus.setEnabled(intake > 0);
        btnWaterMinus.setAlpha(intake > 0 ? 1.0f : 0.5f);

        // 물컵 채우기
        for (int i = 0; i < waterCupViews.size(); i++) {
            View cup = waterCupViews.get(i);
            if (i < intake) {
                // React의 (index < currentIntake)
                cup.setBackgroundResource(R.drawable.bg_water_cup_filled);
            } else {
                cup.setBackgroundResource(R.drawable.bg_water_cup_empty);
            }
        }
    }

    /**
     * 9단계 헬퍼: 아이콘과 배경색 변경
     */
    private void updateSuggestionIcon(SuggestionData.IconType iconType) {
        Context context = getContext();
        if (context == null) return;

        switch (iconType) {
            case DUMBBELL: // 주황색
                ivSuggestionIcon.setImageResource(R.drawable.ic_dumbbell);
                ivSuggestionIcon.setColorFilter(ContextCompat.getColor(context, R.color.chart_fat));
                cardIconBg.setCardBackgroundColor(Color.parseColor("#FFE9D6")); // bg-orange-100
                break;
            case TARGET: // 파란색
                ivSuggestionIcon.setImageResource(R.drawable.ic_target);
                ivSuggestionIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_blue));
                cardIconBg.setCardBackgroundColor(Color.parseColor("#D6EFFF")); // bg-blue-100
                break;
            case ZAP: // 초록색
            default:
                ivSuggestionIcon.setImageResource(R.drawable.ic_zap);
                ivSuggestionIcon.setColorFilter(ContextCompat.getColor(context, R.color.chart_protein));
                cardIconBg.setCardBackgroundColor(Color.parseColor("#D6F5DD")); // bg-green-100
                break;
        }
    }

    // Dashboard.tsx의 macroData, COLORS 로직 구현
    private void setupPieChart(Nutrients macros) {
        List<PieEntry> entries = new ArrayList<>();
        double totalMacros = macros.getCarbs() + macros.getProtein() + macros.getFat();

        if (totalMacros == 0) {
            // 데이터가 없을 때 (React 로직과 동일)
            entries.add(new PieEntry(1, "탄수화물"));
            entries.add(new PieEntry(1, "단백질"));
            entries.add(new PieEntry(1, "지방"));
        } else {
            entries.add(new PieEntry((float) macros.getCarbs(), "탄수화물"));
            entries.add(new PieEntry((float) macros.getProtein(), "단백질"));
            entries.add(new PieEntry((float) macros.getFat(), "지방"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        // Dashboard.tsx의 COLORS 배열 로직 구현
        final int[] CHART_COLORS = {
                ContextCompat.getColor(getContext(), R.color.chart_carbs),
                ContextCompat.getColor(getContext(), R.color.chart_protein),
                ContextCompat.getColor(getContext(), R.color.chart_fat)
        };
        dataSet.setColors(CHART_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChartMacros)); // 값 대신 % 표시

        PieData pieData = new PieData(dataSet);
        pieChartMacros.setData(pieData);

        // 차트 스타일 설정 (React의 innerRadius, paddingAngle 등)
        pieChartMacros.setUsePercentValues(true);
        pieChartMacros.setDrawHoleEnabled(true);
        pieChartMacros.setHoleRadius(50f); // innerRadius
        pieChartMacros.setTransparentCircleRadius(55f);
        pieChartMacros.setDescription(null);
        pieChartMacros.getLegend().setEnabled(true); // 범례 활성화
        pieChartMacros.animateY(1000); // 애니메이션
        pieChartMacros.invalidate(); // 차트 새로고침
    }
}