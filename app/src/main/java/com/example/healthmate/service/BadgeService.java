package com.example.healthmate.service;

import com.example.healthmate.R;
import com.example.healthmate.model.Badge;
import java.util.ArrayList;
import java.util.List;

// React의 badgeService.ts
public class BadgeService {

    // React의 allBadges 배열
    public static List<Badge> getAllBadges() {
        List<Badge> badges = new ArrayList<>();

        // Lucide 아이콘을 Android Drawable로 매핑
        badges.add(new Badge("first_meal", "첫 걸음", "첫 식단을 기록했습니다.", R.drawable.ic_utensils));
        badges.add(new Badge("first_challenge", "챌린지 완료!", "첫 챌린지를 완료했습니다.", R.drawable.ic_star));
        badges.add(new Badge("water_master", "수분 마스터", "하루 수분 섭취 목표를 처음으로 달성했습니다.", R.drawable.ic_droplets));

        return badges;
    }

    // (checkNewBadges 로직도 여기에 Java로 포팅...)
}