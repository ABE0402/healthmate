package com.example.healthmate.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// 3개의 하위 탭 프래그먼트 (임시 생성 필요)
import com.example.healthmate.view.profile.ProfileInfoTabFragment;
import com.example.healthmate.view.profile.ProfileAchievementsTabFragment;
import com.example.healthmate.view.profile.ProfileFriendsTabFragment;

public class ProfilePagerAdapter extends FragmentStateAdapter {

    public ProfilePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileInfoTabFragment(); // '내 정보'
            case 1:
                return new ProfileAchievementsTabFragment(); // '업적'
            case 2:
                return new ProfileFriendsTabFragment(); // '친구'
            default:
                return new ProfileInfoTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 탭 3개
    }
}