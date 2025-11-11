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
import com.example.healthmate.model.DashboardData;
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

public class HomeFragment extends Fragment implements MealAdapter.OnDeleteClickListener, ManualAddDialogFragment.OnMealAddListener, AnalysisDialogFragment.OnConfirmListener {

    private HomeViewModel viewModel;

    private TextView tvTotalKcal;
    private TextView tvGoalKcal;
    private ProgressBar progressBarKcal;
    private PieChart pieChartMacros;
    private RecyclerView recyclerViewMeals;
    private TextView tvEmptyMessage;
    private Button btnManualAdd;
    private MealAdapter mealAdapter;
    private TextView tvWaterIntake, tvWaterGoal;
    private ImageButton btnWaterMinus, btnWaterPlus;
    private LinearLayout layoutWaterCups;
    private final List<View> waterCupViews = new ArrayList<>();
    private ProgressBar progressSuggestion;
    private CardView cardIconBg;
    private ImageView ivSuggestionIcon;
    private LinearLayout layoutSuggestionText;
    private TextView tvSuggestionTitle, tvSuggestionDesc, btnSuggestionCta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTotalKcal = view.findViewById(R.id.tvTotalKcal);
        tvGoalKcal = view.findViewById(R.id.tvGoalKcal);
        progressBarKcal = view.findViewById(R.id.progressBarKcal);
        pieChartMacros = view.findViewById(R.id.pieChartMacros);
        recyclerViewMeals = view.findViewById(R.id.recyclerViewMeals);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        btnManualAdd = view.findViewById(R.id.btnManualAdd);
        tvWaterIntake = view.findViewById(R.id.tvWaterIntake);
        tvWaterGoal = view.findViewById(R.id.tvWaterGoal);
        btnWaterMinus = view.findViewById(R.id.btnWaterMinus);
        btnWaterPlus = view.findViewById(R.id.btnWaterPlus);
        layoutWaterCups = view.findViewById(R.id.layoutWaterCups);
        progressSuggestion = view.findViewById(R.id.progressSuggestion);
        cardIconBg = view.findViewById(R.id.cardIconBg);
        ivSuggestionIcon = view.findViewById(R.id.ivSuggestionIcon);
        layoutSuggestionText = view.findViewById(R.id.layoutSuggestionText);
        tvSuggestionTitle = view.findViewById(R.id.tvSuggestionTitle);
        tvSuggestionDesc = view.findViewById(R.id.tvSuggestionDesc);
        btnSuggestionCta = view.findViewById(R.id.btnSuggestionCta);

        setupWaterCups();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mealAdapter = new MealAdapter(this);
        recyclerViewMeals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMeals.setAdapter(mealAdapter);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        btnWaterMinus.setOnClickListener(v -> viewModel.updateWaterIntake(-1));
        btnWaterPlus.setOnClickListener(v -> viewModel.updateWaterIntake(1));

        btnManualAdd.setOnClickListener(v -> {
            ManualAddDialogFragment dialog = ManualAddDialogFragment.newInstance();
            dialog.show(getChildFragmentManager(), "ManualAddDialog");
        });

        setupObservers();
    }

    private void setupObservers() {
        // Dashboard 데이터 관찰 (UI 업데이트 및 AI 제안 업데이트)
        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            // 칼로리 및 프로그레스 바 업데이트
            tvTotalKcal.setText(String.format(Locale.getDefault(), "%,d", data.getTotalKcal()));
            tvGoalKcal.setText(String.format(Locale.getDefault(), "/ %,d kcal", data.getGoalKcal()));
            int progress = (data.getGoalKcal() > 0) ? (int) ((double) data.getTotalKcal() / data.getGoalKcal() * 100) : 0;
            progressBarKcal.setProgress(Math.min(progress, 100));

            // 파이 차트 업데이트
            setupPieChart(data.getMacros());

            // AI 제안 업데이트
            int userWeight = 75; // 예시 체중
            viewModel.updateSuggestion(data, userWeight);
        });

        // 오늘 섭취한 식단 리스트 관찰
        viewModel.getTodayMeals().observe(getViewLifecycleOwner(), meals -> {
            boolean isEmpty = meals == null || meals.isEmpty();
            recyclerViewMeals.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            tvEmptyMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            if (!isEmpty) {
                mealAdapter.submitList(meals);
            }
        });

        // 이미지 분석 상태 관찰
        viewModel.getIsAnalyzingImage().observe(getViewLifecycleOwner(), isAnalyzing -> {
            if (isAnalyzing != null && isAnalyzing) {
                if (getChildFragmentManager().findFragmentByTag("AnalysisDialog") == null) {
                    AnalysisDialogFragment dialog = new AnalysisDialogFragment();
                    dialog.show(getChildFragmentManager(), "AnalysisDialog");
                }
            }
        });

        // 수분 섭취량 관찰
        viewModel.getWaterIntake().observe(getViewLifecycleOwner(), this::updateWaterUI);

        // AI 제안 내용 관찰
        viewModel.getSuggestion().observe(getViewLifecycleOwner(), suggestion -> {
            if (suggestion == null) return;
            boolean isLoading = suggestion.getIconType() == SuggestionData.IconType.LOADING;
            progressSuggestion.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            layoutSuggestionText.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            cardIconBg.setVisibility(isLoading ? View.GONE : View.VISIBLE);

            if (!isLoading) {
                tvSuggestionTitle.setText(suggestion.getTitle());
                tvSuggestionDesc.setText(suggestion.getDescription());
                boolean hasCta = suggestion.getCtaText() != null && !suggestion.getCtaText().isEmpty();
                btnSuggestionCta.setVisibility(hasCta ? View.VISIBLE : View.GONE);
                if(hasCta) {
                    btnSuggestionCta.setText(suggestion.getCtaText());
                }
                updateSuggestionIcon(suggestion.getIconType());
            }
        });
    }

    @Override
    public void onConfirm(List<AnalysisResult> results, Meal.MealTime time) {
        viewModel.addAnalyzedMeals(results, time);
        Toast.makeText(getContext(), results.size() + "개의 항목이 식단에 추가되었습니다!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Meal meal) {
        viewModel.deleteMeal(meal);
        Toast.makeText(getContext(), "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMealAdd(String foodItem, Meal.MealTime time) {
        viewModel.addManualMeal(foodItem, time);
    }

    private void setupWaterCups() {
        layoutWaterCups.removeAllViews();
        waterCupViews.clear();
        int cupSize = (int) (getResources().getDisplayMetrics().density * 32);
        int margin = (int) (getResources().getDisplayMetrics().density * 4);

        for (int i = 0; i < HomeViewModel.DAILY_WATER_GOAL; i++) {
            View cupView = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, cupSize, 1.0f);
            params.setMargins(margin, margin, margin, margin);
            cupView.setLayoutParams(params);
            layoutWaterCups.addView(cupView);
            waterCupViews.add(cupView);
        }
    }

    private void updateWaterUI(int intake) {
        tvWaterIntake.setText(String.valueOf(intake));
        if (intake >= HomeViewModel.DAILY_WATER_GOAL) {
            tvWaterGoal.setText("목표 달성! 훌륭해요! 🎉");
        } else {
            tvWaterGoal.setText(String.format(Locale.getDefault(), "목표: %d잔", HomeViewModel.DAILY_WATER_GOAL));
        }
        btnWaterMinus.setEnabled(intake > 0);
        btnWaterMinus.setAlpha(intake > 0 ? 1.0f : 0.5f);

        for (int i = 0; i < waterCupViews.size(); i++) {
            View cup = waterCupViews.get(i);
            cup.setBackgroundResource(i < intake ? R.drawable.bg_water_cup_filled : R.drawable.bg_water_cup_empty);
        }
    }

    private void updateSuggestionIcon(SuggestionData.IconType iconType) {
        Context context = getContext();
        if (context == null) return;
        int iconRes;
        int iconColor;
        int bgColor;

        switch (iconType) {
            case DUMBBELL:
                iconRes = R.drawable.ic_dumbbell;
                iconColor = ContextCompat.getColor(context, R.color.chart_fat);
                bgColor = Color.parseColor("#FFE9D6");
                break;
            case TARGET:
                iconRes = R.drawable.ic_target;
                iconColor = ContextCompat.getColor(context, R.color.primary_blue);
                bgColor = Color.parseColor("#D6EFFF");
                break;
            case ZAP:
            default:
                iconRes = R.drawable.ic_zap;
                iconColor = ContextCompat.getColor(context, R.color.chart_protein);
                bgColor = Color.parseColor("#D6F5DD");
                break;
        }
        ivSuggestionIcon.setImageResource(iconRes);
        ivSuggestionIcon.setColorFilter(iconColor);
        cardIconBg.setCardBackgroundColor(bgColor);
    }

    private void setupPieChart(Nutrients macros) {
        if (macros == null) return;
        List<PieEntry> entries = new ArrayList<>();
        double totalMacros = macros.getCarbs() + macros.getProtein() + macros.getFat();

        if (totalMacros == 0) {
            entries.add(new PieEntry(1, ""));
            entries.add(new PieEntry(1, ""));
            entries.add(new PieEntry(1, ""));
        } else {
            entries.add(new PieEntry((float) macros.getCarbs(), "탄수화물"));
            entries.add(new PieEntry((float) macros.getProtein(), "단백질"));
            entries.add(new PieEntry((float) macros.getFat(), "지방"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        final int[] CHART_COLORS = {
                ContextCompat.getColor(requireContext(), R.color.chart_carbs),
                ContextCompat.getColor(requireContext(), R.color.chart_protein),
                ContextCompat.getColor(requireContext(), R.color.chart_fat)
        };
        dataSet.setColors(CHART_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChartMacros));

        PieData pieData = new PieData(dataSet);
        pieChartMacros.setData(pieData);
        pieChartMacros.setUsePercentValues(true);
        pieChartMacros.setDrawHoleEnabled(true);
        pieChartMacros.setHoleRadius(58f);
        pieChartMacros.setTransparentCircleRadius(61f);
        pieChartMacros.setDescription(null);
        pieChartMacros.getLegend().setEnabled(true);
        pieChartMacros.setDrawEntryLabels(false);
        pieChartMacros.animateY(1000);
        pieChartMacros.invalidate();
    }
}