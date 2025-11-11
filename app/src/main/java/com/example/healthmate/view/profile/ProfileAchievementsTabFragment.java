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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.adapter.BadgeAdapter;
import com.example.healthmate.model.Badge;
import com.example.healthmate.model.UserProfile;
import com.example.healthmate.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileAchievementsTabFragment extends Fragment {

    private ProfileViewModel viewModel;
    private RecyclerView recyclerViewBadges;
    private BadgeAdapter adapter;
    private TextView tvAchievementTitle;

    private List<Badge> allBadges = new ArrayList<>();
    private List<String> unlockedBadgeIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 19.3에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_tab_profile_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모 Fragment(ProfileFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        // 뷰 ID 연결
        tvAchievementTitle = view.findViewById(R.id.tvAchievementTitle);
        recyclerViewBadges = view.findViewById(R.id.recyclerViewBadges);

        // 어댑터 및 레이아웃 매니저 설정
        // React의 (grid-cols-3)
        adapter = new BadgeAdapter(allBadges, unlockedBadgeIds);
        recyclerViewBadges.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewBadges.setAdapter(adapter);

        // ViewModel 관찰
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                unlockedBadgeIds = profile.getUnlockedBadgeIds();
                updateTitle();
                adapter.setData(allBadges, unlockedBadgeIds);
            }
        });

        viewModel.getAllBadges().observe(getViewLifecycleOwner(), badges -> {
            if (badges != null) {
                allBadges = badges;
                updateTitle();
                adapter.setData(allBadges, unlockedBadgeIds);
            }
        });
    }

    // React의 (나의 업적 ({unlockedBadgeIds.length}/{allBadges.length}))
    //
    private void updateTitle() {
        tvAchievementTitle.setText(String.format(Locale.getDefault(),
                "나의 업적 (%d/%d)", unlockedBadgeIds.size(), allBadges.size()
        ));
    }
}