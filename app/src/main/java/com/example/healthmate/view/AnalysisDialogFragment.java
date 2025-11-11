package com.example.healthmate.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.adapter.AnalysisAdapter;
import com.example.healthmate.model.AnalysisResult;
import com.example.healthmate.model.Meal;
import com.example.healthmate.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnalysisDialogFragment extends DialogFragment {

    // React의 onConfirm prop
    public interface OnConfirmListener {
        void onConfirm(List<AnalysisResult> results, Meal.MealTime time);
    }
    private OnConfirmListener listener;

    private HomeViewModel viewModel;
    private AnalysisAdapter adapter;
    private List<AnalysisResult> analysisResults = new ArrayList<>();

    // 7.2 XML 뷰
    private TextView tvDialogTitle, tvTotalKcal;
    private ProgressBar progressBar;
    private LinearLayout layoutResult;
    private ImageView ivFoodImage;
    private RecyclerView recyclerViewAnalysis;
    private Spinner spinnerMealTime;
    private Button btnConfirm;
    private ImageButton btnClose;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 부모 Fragment(HomeFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 리스너 설정
        try {
            listener = (OnConfirmListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement OnConfirmListener");
        }

        // 뷰 ID 바인딩
        tvDialogTitle = view.findViewById(R.id.tvDialogTitle);
        tvTotalKcal = view.findViewById(R.id.tvTotalKcal);
        progressBar = view.findViewById(R.id.progressBar);
        layoutResult = view.findViewById(R.id.layoutResult);
        ivFoodImage = view.findViewById(R.id.ivFoodImage);
        recyclerViewAnalysis = view.findViewById(R.id.recyclerViewAnalysis);
        spinnerMealTime = view.findViewById(R.id.spinnerMealTime);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnClose = view.findViewById(R.id.btnClose);

        setupSpinner();

        btnClose.setOnClickListener(v -> {
            viewModel.clearAnalysisData(); // ViewModel 데이터 초기화
            dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            // React의 onConfirm()
            List<AnalysisResult> finalResults = adapter.getUpdatedResults();
            Meal.MealTime selectedTime = (Meal.MealTime) spinnerMealTime.getSelectedItem();
            listener.onConfirm(finalResults, selectedTime);
            dismiss();
        });

        // --- ViewModel 관찰 (React의 useEffect) ---

        // 1. 분석 중 상태 (isAnalyzing)
        viewModel.getIsAnalyzingImage().observe(getViewLifecycleOwner(), isAnalyzing -> {
            progressBar.setVisibility(isAnalyzing ? View.VISIBLE : View.GONE);
            layoutResult.setVisibility(isAnalyzing ? View.GONE : View.VISIBLE);
            btnConfirm.setVisibility(isAnalyzing ? View.GONE : View.VISIBLE);

            tvDialogTitle.setText(isAnalyzing ? "음식 사진 분석 중..." : "분석 완료!");
        });

        // 2. 분석 결과 (result)
        viewModel.getImageAnalysisResult().observe(getViewLifecycleOwner(), results -> {
            if (results != null) {
                this.analysisResults.clear();
                this.analysisResults.addAll(results);
                setupRecyclerView();
                updateTotalKcal();
            }
        });

        // 3. 분석된 이미지 (image)
        viewModel.getSelectedImage().observe(getViewLifecycleOwner(), imageBitmap -> {
            if (imageBitmap != null) {
                ivFoodImage.setImageBitmap(imageBitmap);
            }
        });

        // 4. 에러 (error)
        viewModel.getImageAnalysisError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                dismiss(); // 에러 발생 시 모달 닫기
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new AnalysisAdapter(this.analysisResults, new AnalysisAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged() {
                updateTotalKcal(); // 아이템의 칼로리가 수정되면 총합 갱신
            }
            @Override
            public void onItemRemoved(int position) {
                analysisResults.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, analysisResults.size());
                updateTotalKcal(); // 아이템 삭제 시 총합 갱신
            }
        });
        recyclerViewAnalysis.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAnalysis.setAdapter(adapter);
    }

    // React의 useMemo totalKcal
    private void updateTotalKcal() {
        int total = 0;
        for (AnalysisResult res : adapter.getUpdatedResults()) {
            total += res.getKcal();
        }
        tvTotalKcal.setText(String.format(Locale.getDefault(), "%,d kcal", total));
    }

    private void setupSpinner() {
        // (6단계와 동일한 스피너 설정)
        ArrayAdapter<Meal.MealTime> adapter = new ArrayAdapter<>(...);
        spinnerMealTime.setAdapter(adapter);
        spinnerMealTime.setSelection(getDefaultMealTimeIndex());
    }

    private int getDefaultMealTimeIndex() {
        // (6단계와 동일한 시간 계산 로직)
        return 1; // "점심"
    }
}