package com.example.healthmate.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.adapter.FeedAdapter;
import com.example.healthmate.model.GroupFeedItem;
import com.example.healthmate.viewmodel.GroupViewModel;

import java.util.ArrayList;

public class GroupDetailFragment extends Fragment implements FeedAdapter.OnLikeClickListener {

    private static final String ARG_GROUP_ID = "group_id";
    private long groupId;

    private GroupViewModel viewModel;
    private FeedAdapter adapter;

    private TextView tvGroupName, tvEmptyFeed;
    private RecyclerView recyclerViewFeed;
    private ImageButton btnBack;

    // React의 (groupId) => setActiveGroupId(groupId)
    public static GroupDetailFragment newInstance(long groupId) {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getLong(ARG_GROUP_ID);
        }
        // Activity와 ViewModel 공유
        viewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 17.2에서 만든 레이아웃
        return inflater.inflate(R.layout.fragment_group_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뷰 ID 연결
        tvGroupName = view.findViewById(R.id.tvGroupName);
        recyclerViewFeed = view.findViewById(R.id.recyclerViewFeed);
        tvEmptyFeed = view.findViewById(R.id.tvEmptyFeed);
        btnBack = view.findViewById(R.id.btnBack);

        // 어댑터 설정
        adapter = new FeedAdapter(new ArrayList<>(), this);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFeed.setAdapter(adapter);

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // ViewModel 관찰
        viewModel.getGroupById(groupId).observe(getViewLifecycleOwner(), group -> {
            if (group != null) {
                tvGroupName.setText(group.getName());
                // (챌린지 카드 등 나머지 UI 데이터 바인딩)
            }
        });

        viewModel.getFeedForGroup(groupId).observe(getViewLifecycleOwner(), feedItems -> {
            if (feedItems == null || feedItems.isEmpty()) {
                tvEmptyFeed.setVisibility(View.VISIBLE);
                recyclerViewFeed.setVisibility(View.GONE);
            } else {
                tvEmptyFeed.setVisibility(View.GONE);
                recyclerViewFeed.setVisibility(View.VISIBLE);
                adapter.submitList(feedItems);
            }
        });
    }

    /**
     * "응원하기" 버튼 클릭 (onToggleLike)
     *
     */
    @Override
    public void onLikeClick(GroupFeedItem item) {
        viewModel.toggleFeedLike(item.getId());
    }
}