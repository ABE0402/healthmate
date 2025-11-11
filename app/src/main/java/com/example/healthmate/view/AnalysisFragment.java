package com.example.healthmate.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // ViewModel
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.healthmate.R;
import com.example.healthmate.adapter.AnalysisPagerAdapter;
import com.example.healthmate.viewmodel.AnalysisViewModel; // 11.1의 ViewModel

public class AnalysisFragment extends Fragment {

    private AnalysisViewModel viewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AnalysisPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 11.2에서 만든 탭 레이아웃
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel 초기화
        // requireActivity()를 사용해 MainActivity와 ViewModel을 공유할 수도 있음
        viewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);

        // 뷰 ID 연결
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // 어댑터 설정
        pagerAdapter = new AnalysisPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // TabLayout과 ViewPager2 연결
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // 탭 이름 설정
            switch (position) {
                case 0:
                    tab.setText("식단 분석"); //
                    break;
                case 1:
                    tab.setText("AI 식단 플래너"); //
                    break;
                case 2:
                    tab.setText("챌린지 및 업적"); //
                    break;
            }
        }).attach();
    }
}