package com.example.healthmate.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.healthmate.view.analysis.AnalysisTabFragment;
import com.example.healthmate.view.analysis.ChallengesTabFragment;
import com.example.healthmate.view.analysis.PlannerTabFragment;

// 3개의 하위 탭 프래그먼트 (임시 생성 필요)


// React의 activeTab 상태 로직
public class AnalysisPagerAdapter extends FragmentStateAdapter {

    public AnalysisPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AnalysisTabFragment(); // '식단 분석' 탭
            case 1:
                return new PlannerTabFragment(); // 'AI 식단 플래너' 탭
            case 2:
                return new ChallengesTabFragment(); // '챌린지 및 업적' 탭
            default:
                return new AnalysisTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 탭 3개
    }
}