package com.example.healthmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthmate.model.Group;
import com.example.healthmate.model.GroupFeedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// React의 App.tsx의 groups, groupFeed 상태 관리
public class GroupViewModel extends ViewModel {

    private final MutableLiveData<List<Group>> _groups = new MutableLiveData<>();
    public LiveData<List<Group>> getGroups() { return _groups; }

    private final MutableLiveData<List<GroupFeedItem>> _groupFeed = new MutableLiveData<>();
    public LiveData<List<GroupFeedItem>> getGroupFeed() { return _groupFeed; }

    public GroupViewModel() {
        loadGroups(); // Mock 데이터 로드
    }

    // React의 App.tsx의 groups, groupFeed 목 데이터
    private void loadGroups() {
        List<Group> mockGroups = new ArrayList<>();
        mockGroups.add(new Group(1, "여름 준비 다이어트방", "여름까지 함께 달려봐요!", 5, "그룹 칼로리 50,000kcal 소모", 78));
        mockGroups.add(new Group(2, "단백질 용사들", "근손실은 절대 못 참지", 3, "이번 주 단백질 섭취 1등하기", 45));
        _groups.setValue(mockGroups);

        // (GroupFeed 데이터도 로드)
        _groupFeed.setValue(new ArrayList<>());
    }
    // React의 onToggleFeedLike
    public void toggleFeedLike(long feedItemId) {
        List<GroupFeedItem> currentFeed = _groupFeed.getValue();
        if (currentFeed == null) return;

        for (GroupFeedItem item : currentFeed) {
            if (item.getId() == feedItemId) {
                // (실제로는 DB/API에 좋아요 상태를 업데이트해야 함)
                item.setLikedByMe(!item.isLikedByMe());
                item.setLikes(item.isLikedByMe() ? item.getLikes() + 1 : item.getLikes() - 1);
                break;
            }
        }
        _groupFeed.postValue(currentFeed); // LiveData 갱신
    }

    // React의 groupFeed.filter(f => f.groupId === activeGroupId)
    public LiveData<List<GroupFeedItem>> getFeedForGroup(long groupId) {
        // (간단한 구현, 실제로는 Room DB의 쿼리를 사용해야 함)
        MutableLiveData<List<GroupFeedItem>> filteredFeed = new MutableLiveData<>();
        List<GroupFeedItem> allFeed = _groupFeed.getValue();
        if (allFeed != null) {
            filteredFeed.setValue(
                    allFeed.stream()
                            .filter(item -> item.getGroupId() == groupId)
                            .collect(Collectors.toList())
            );
        }
        return filteredFeed;
    }

    // (선택된 그룹 정보 로드)
    public LiveData<Group> getGroupById(long groupId) {
        // ... (DB에서 그룹 ID로 Group 객체 찾는 로직) ...
        MutableLiveData<Group> group = new MutableLiveData<>();
        group.setValue(_groups.getValue().stream().filter(g -> g.getId() == groupId).findFirst().orElse(null));
        return group;
    }

    // React의 onDeleteGroup
    public void deleteGroup(long groupId) {
        // ... (DB에서 그룹 삭제 로직) ...
    }
}