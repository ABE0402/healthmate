package com.example.healthmate.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.adapter.FriendAdapter;
import com.example.healthmate.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class ProfileFriendsTabFragment extends Fragment {

    private ProfileViewModel viewModel;
    private RecyclerView recyclerViewFriends;
    private FriendAdapter adapter;
    private TextView tvEmptyMessage, tvFriendsTitle;
    private Button btnAddFriend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 20.2에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_tab_profile_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모 Fragment(ProfileFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        // 뷰 ID 연결
        tvFriendsTitle = view.findViewById(R.id.tvFriendsTitle);
        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        btnAddFriend = view.findViewById(R.id.btnAddFriend);

        // 어댑터 설정
        adapter = new FriendAdapter(new ArrayList<>());
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFriends.setAdapter(adapter);

        // "친구 추가" 버튼 리스너
        btnAddFriend.setOnClickListener(v -> {
            AddFriendDialogFragment dialog = AddFriendDialogFragment.newInstance();
            // 자식 FragmentManager 사용
            dialog.show(getChildFragmentManager(), "AddFriendDialog");
        });

        // ViewModel 관찰
        viewModel.getFriends().observe(getViewLifecycleOwner(), friends -> {
            if (friends == null || friends.isEmpty()) {
                // React의 (friends.length === 0)
                tvEmptyMessage.setVisibility(View.VISIBLE);
                recyclerViewFriends.setVisibility(View.GONE);
                tvFriendsTitle.setText("친구 목록");
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                recyclerViewFriends.setVisibility(View.VISIBLE);
                adapter.submitList(friends);
                // 17.5단계에서 탭 제목 업데이트
                tvFriendsTitle.setText(String.format("친구 목록 (%d)", friends.size()));
            }
        });
    }
}