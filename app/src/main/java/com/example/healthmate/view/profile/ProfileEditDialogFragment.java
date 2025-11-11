package com.example.healthmate.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.example.healthmate.R;
import com.example.healthmate.model.UserProfile;
import com.example.healthmate.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class ProfileEditDialogFragment extends DialogFragment {

    private ProfileViewModel viewModel;

    // 21.1 XML 뷰
    private Spinner spinnerGender, spinnerActivityLevel;
    private TextInputEditText etAge, etHeight, etWeight;
    private Button btnSave;
    private ImageButton btnClose;

    public static ProfileEditDialogFragment newInstance() {
        return new ProfileEditDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모(ProfileFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        // 뷰 ID 연결
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerActivityLevel = view.findViewById(R.id.spinnerActivityLevel);
        etAge = view.findViewById(R.id.etAge);
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        btnSave = view.findViewById(R.id.btnSave);
        btnClose = view.findViewById(R.id.btnClose);

        setupSpinners();

        // ViewModel의 현재 프로필 데이터로 UI 채우기
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                // React의 useEffect
                etAge.setText(String.valueOf(profile.getAge()));
                etHeight.setText(String.valueOf(profile.getHeight()));
                etWeight.setText(String.valueOf(profile.getWeight()));
                spinnerGender.setSelection(profile.getGender().ordinal());
                spinnerActivityLevel.setSelection(profile.getActivityLevel().ordinal());
            }
        });

        btnClose.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            // React의 handleSubmit
            saveProfile();
        });
    }

    private void setupSpinners() {
        // 성별 스피너
        ArrayAdapter<UserProfile.Gender> genderAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, UserProfile.Gender.values()
        );
        spinnerGender.setAdapter(genderAdapter);

        // 활동량 스피너 (activityLabels)
        // (UserProfile.ActivityLevel enum에 displayName을 추가하는 것이 좋음)
        ArrayAdapter<UserProfile.ActivityLevel> activityAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, UserProfile.ActivityLevel.values()
        );
        spinnerActivityLevel.setAdapter(activityAdapter);
    }

    private void saveProfile() {
        try {
            // 현재 프로필 객체 복사 (새 객체 생성)
            UserProfile currentProfile = viewModel.getUserProfile().getValue();
            if (currentProfile == null) return;

            // (간단한 구현, 실제로는 new UserProfile(...) 사용)
            UserProfile updatedProfile = new UserProfile(
                    (UserProfile.Gender) spinnerGender.getSelectedItem(),
                    Integer.parseInt(etAge.getText().toString()),
                    (int) Double.parseDouble(etHeight.getText().toString()),
                    (int) Double.parseDouble(etWeight.getText().toString()),
                    (UserProfile.ActivityLevel) spinnerActivityLevel.getSelectedItem(),
                    currentProfile.getUnlockedBadgeIds() // 배지 목록은 유지
            );

            // ViewModel에 저장 요청
            viewModel.saveUserProfile(updatedProfile);

            Toast.makeText(getContext(), "프로필이 업데이트되었습니다!", Toast.LENGTH_SHORT).show();
            dismiss(); // 모달 닫기

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "숫자 입력란을 올바르게 채워주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}