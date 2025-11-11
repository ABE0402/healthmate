package com.example.healthmate.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthmate.view.profile.ProfileEditDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.healthmate.R;
import com.example.healthmate.adapter.ProfilePagerAdapter;
import com.example.healthmate.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfilePagerAdapter pagerAdapter;
    private ImageButton btnSettings, btnEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 17.2에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Activity와 ViewModel 공유
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // 뷰 ID 연결
        tabLayout = view.findViewById(R.id.tab_layout_profile);
        viewPager = view.findViewById(R.id.view_pager_profile);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // 어댑터 설정
        pagerAdapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // TabLayout과 ViewPager2 연결
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // 탭 이름 설정
            switch (position) {
                case 0:
                    tab.setText("내 정보");
                    break;
                case 1:
                    tab.setText("업적");
                    break;
                case 2:
                    tab.setText("친구 (" + viewModel.getFriends().getValue().size() + ")");
                    break;
            }
        }).attach();

        // 버튼 리스너
        btnSettings.setOnClickListener(v -> {
            // React의 onEdit
            ProfileEditDialogFragment dialog = ProfileEditDialogFragment.newInstance();
            // 자식 FragmentManager 사용
            dialog.show(getChildFragmentManager(), "ProfileEditDialog");
        });

        btnEditProfile.setOnClickListener(v -> {
            // React의 onEdit
            // (ProfileEditDialogFragment 띄우기)
        });

        // (ViewModel 관찰하여 프로필 이미지, 이름 등 업데이트...)
    }
}