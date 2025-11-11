package com.example.healthmate.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthmate.R;
import com.example.healthmate.model.UserProfile;
import com.example.healthmate.utils.CalculationUtils; // 18.2에서 만든 유틸
import com.example.healthmate.viewmodel.ProfileViewModel; // 17.4에서 만든 ViewModel

import java.util.Locale;

public class ProfileInfoTabFragment extends Fragment {

    private ProfileViewModel viewModel;

    // 18.1 XML 뷰
    private TextView tvGender, tvAge, tvHeight, tvWeight;
    private TextView tvBmr, tvDailyGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 18.1에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_tab_profile_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모 Fragment(ProfileFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        // 뷰 ID 연결
        tvGender = view.findViewById(R.id.tvGender);
        tvAge = view.findViewById(R.id.tvAge);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvBmr = view.findViewById(R.id.tvBmr);
        tvDailyGoal = view.findViewById(R.id.tvDailyGoal);

        // ViewModel 관찰 (React의 useEffect)
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                updateUI(profile);
            }
        });
    }

    /**
     * LiveData 변경 시 UI 텍스트 업데이트
     */
    private void updateUI(UserProfile profile) {
        // 1. 기본 정보 카드
        tvGender.setText(profile.getGender() == UserProfile.Gender.MALE ? "남성" : "여성");
        tvAge.setText(String.format(Locale.getDefault(), "%d 세", profile.getAge()));
        tvHeight.setText(String.format(Locale.getDefault(), "%d cm", profile.getHeight()));
        tvWeight.setText(String.format(Locale.getDefault(), "%d kg", profile.getWeight()));

        // 2. 나의 목표 카드 (18.2 계산 로직 사용)
        int bmr = CalculationUtils.calculateBMR(profile);
        int dailyGoal = CalculationUtils.calculateDailyGoal(profile);

        tvBmr.setText(String.format(Locale.getDefault(), "%,d kcal", bmr));
        tvDailyGoal.setText(String.format(Locale.getDefault(), "%,d kcal", dailyGoal));
    }
}