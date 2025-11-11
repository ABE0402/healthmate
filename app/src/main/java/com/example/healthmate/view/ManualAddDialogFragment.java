package com.example.healthmate.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.example.healthmate.R;
import com.example.healthmate.model.Meal; // 1단계의 Meal.MealTime enum
import com.example.healthmate.viewmodel.HomeViewModel; // 3단계의 ViewModel

import java.util.Calendar;

// React의 ManualAddModal.tsx 로직
public class ManualAddDialogFragment extends DialogFragment {

    // HomeFragment(부모)와 통신하기 위한 리스너
    public interface OnMealAddListener {
        // React의 onAddMeal(mealData) prop
        void onMealAdd(String foodItem, Meal.MealTime time);
    }
    private OnMealAddListener listener;

    // 6.1 XML 뷰
    private TextInputEditText etFoodItem;
    private Spinner spinnerMealTime;
    private Button btnSubmit;
    private ImageButton btnClose;
    private ProgressBar progressBar;
    private TextView tvError;

    private HomeViewModel viewModel; // HomeFragment의 ViewModel 공유

    public static ManualAddDialogFragment newInstance() {
        return new ManualAddDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 6.1에서 만든 XML을 inflate
        return inflater.inflate(R.layout.dialog_manual_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모 Fragment를 리스너로 설정
        try {
            listener = (OnMealAddListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement OnMealAddListener");
        }

        // HomeFragment와 동일한 ViewModel 인스턴스 사용
        viewModel = new ViewModelProvider(requireParentFragment()).get(HomeViewModel.class);

        // 뷰 ID 연결
        etFoodItem = view.findViewById(R.id.etFoodItem);
        spinnerMealTime = view.findViewById(R.id.spinnerMealTime);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnClose = view.findViewById(R.id.btnClose);
        progressBar = view.findViewById(R.id.progressBar);
        tvError = view.findViewById(R.id.tvError);

        setupSpinner();

        // 닫기 버튼
        btnClose.setOnClickListener(v -> dismiss());

        // 제출 버튼 (React의 handleSubmit)
        btnSubmit.setOnClickListener(v -> {
            String foodItem = etFoodItem.getText().toString().trim();
            if (foodItem.isEmpty()) {
                etFoodItem.setError("음식 이름을 입력해주세요.");
                return;
            }
            Meal.MealTime selectedTime = (Meal.MealTime) spinnerMealTime.getSelectedItem();

            // 리스너를 통해 HomeFragment의 ViewModel에 작업 요청
            listener.onMealAdd(foodItem, selectedTime);
        });

        // ViewModel의 상태 관찰 (로딩 및 에러 처리)
        // React의 isAnalyzing, error 상태
        viewModel.getIsAnalyzingText().observe(getViewLifecycleOwner(), isAnalyzing -> {
            progressBar.setVisibility(isAnalyzing ? View.VISIBLE : View.GONE);
            btnSubmit.setEnabled(!isAnalyzing);
            btnSubmit.setText(isAnalyzing ? "AI가 분석 중..." : "기록에 추가하기");
        });

        viewModel.getTextAnalysisError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(error);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        // 식사 추가 성공 시 (ViewModel에서 처리)
        viewModel.getMealAddedSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                dismiss(); // 다이얼로그 닫기
            }
        });
    }

    // Spinner에 Meal.MealTime enum 채우기
    private void setupSpinner() {
        // React의 <select> <option> 매핑
        ArrayAdapter<Meal.MealTime> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Meal.MealTime.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealTime.setAdapter(adapter);

        // React의 useEffect에서 현재 시간으로 기본값 설정 로직
        spinnerMealTime.setSelection(getDefaultMealTimeIndex());
    }

    // React의 new Date().getHours() 로직
    private int getDefaultMealTimeIndex() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 10) return Meal.MealTime.BREAKFAST.ordinal();
        if (hour >= 10 && hour < 16) return Meal.MealTime.LUNCH.ordinal();
        if (hour >= 16 && hour < 21) return Meal.MealTime.DINNER.ordinal();
        if (hour >= 21 || hour < 2) return Meal.MealTime.LATE_NIGHT.ordinal();
        return Meal.MealTime.SNACK.ordinal();
    }
}