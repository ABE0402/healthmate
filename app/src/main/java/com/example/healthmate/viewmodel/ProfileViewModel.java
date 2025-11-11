package com.example.healthmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthmate.model.Badge;
import com.example.healthmate.model.Friend;
import com.example.healthmate.model.UserProfile;
import com.example.healthmate.service.BadgeService; // (BadgeService.java로 포팅 필요)

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileViewModel extends ViewModel {

    // UserProfile은 HomeViewModel 또는 Activity 범위에서 공유되는 것이 좋음
    private final MutableLiveData<UserProfile> _userProfile = new MutableLiveData<>();
    public LiveData<UserProfile> getUserProfile() { return _userProfile; }

    private final MutableLiveData<List<Friend>> _friends = new MutableLiveData<>();
    public LiveData<List<Friend>> getFriends() { return _friends; }

    private final MutableLiveData<List<Badge>> _allBadges = new MutableLiveData<>();
    public LiveData<List<Badge>> getAllBadges() { return _allBadges; }

    public ProfileViewModel() {
        loadProfileData();
    }

    // --- 22단계: 친구 검색 LiveData ---
    private final MutableLiveData<List<Friend>> _searchResults = new MutableLiveData<>();
    public LiveData<List<Friend>> getSearchResults() { return _searchResults; }

    // React의 mockSearchResults
    private final List<Friend> mockFriendsList = new ArrayList<Friend>() {{
        add(new Friend(3, "이영희", "https://i.pravatar.cc/150?u=a042581f4e29026707d"));
        add(new Friend(4, "최민준", "https://i.pravatar.cc/150?u=a042581f4e29026708d"));
    }};

    public void addFriend(Friend friend) {
        List<Friend> currentFriends = _friends.getValue();
        if (currentFriends == null) currentFriends = new ArrayList<>();

        // 이미 친구인지 확인
        if (currentFriends.stream().noneMatch(f -> f.getId() == friend.getId())) {
            currentFriends.add(friend);
            _friends.setValue(currentFriends); // LiveData 갱신
        }
    }

    public void searchFriends(String query) {
        if (query == null || query.trim().isEmpty()) {
            _searchResults.setValue(new ArrayList<>());
            return;
        }

        // (실제로는 API/DB 검색)
        // Mock 데이터를 필터링
        List<Friend> results = mockFriendsList.stream()
                .filter(f -> f.getName().contains(query))
                .collect(Collectors.toList());
        _searchResults.setValue(results);
    }

    private void loadProfileData() {
        // --- Mock 데이터 (React App.tsx) ---
        UserProfile profile = new UserProfile(
                "mock_user_id", "김헬스", "health@mate.com", // id, name, email 추가
                UserProfile.Gender.MALE, 30, 178, 75,
                UserProfile.ActivityLevel.MODERATE, new ArrayList<>()
        );
        _userProfile.setValue(profile);

        List<Friend> mockFriends = new ArrayList<>();
        mockFriends.add(new Friend(1, "안병은", "https://i.pravatar.cc/150?u=a042581f4e29026705d"));
        mockFriends.add(new Friend(2, "김철수", "https://i.pravatar.cc/150?u=a042581f4e29026706d"));
        _friends.setValue(mockFriends);

        // React의 allBadges
        _allBadges.setValue(BadgeService.getAllBadges()); // (BadgeService.java 필요)
    }

    public void saveUserProfile(UserProfile updatedProfile) {
        // TODO: Repository를 통해 DB 또는 서버에 프로필 저장

        // 저장이 성공했다고 가정하고 LiveData 업데이트
        _userProfile.setValue(updatedProfile);
    }

    // React의 onOpenAddFriendModal
    public void addFriend(String name) {
        // ... (친구 추가 로직) ...
    }
}
