package com.example.healthmate;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthmate.view.ChatFragment;
import com.example.healthmate.view.GroupsFragment;
import com.example.healthmate.view.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.healthmate.view.AnalysisFragment;
import com.example.healthmate.view.HomeFragment;
// (ChatFragment, GroupsFragment, ProfileFragment 임포트)

// React의 App.tsx의 onNavigate 로직
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 10.2 XML 연결

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // React의 onNavigate(page)에 해당
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            // React의 switch(activePage)
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment(); // 3~9단계에서 만든 홈
            } else if (itemId == R.id.nav_analysis) {
                selectedFragment = new AnalysisFragment(); // 10.3에서 만든 임시 분석
            } else if (itemId == R.id.nav_chat) {
                 selectedFragment = new ChatFragment();
            } else if (itemId == R.id.nav_groups) {
                 selectedFragment = new GroupsFragment();
            } else if (itemId == R.id.nav_profile) {
                 selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment); // 프래그먼트 교체
            }
            return true;
        });

        // 앱 시작 시 기본 프래그먼트(홈) 로드
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * React의 renderPage()와 유사한 기능
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}