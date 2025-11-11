package com.example.healthmate.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.example.healthmate.R;
import com.example.healthmate.adapter.GroupAdapter;
import com.example.healthmate.model.Group;
import com.example.healthmate.viewmodel.GroupViewModel;

import java.util.ArrayList;

public class GroupsFragment extends Fragment implements GroupAdapter.OnGroupClickListener {

    private GroupViewModel viewModel;
    private RecyclerView recyclerViewGroups;
    private GroupAdapter adapter;
    private TextView tvEmptyMessage;
    private ExtendedFloatingActionButton btnCreateGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 16.3에서 만든 실제 레이아웃
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Activity와 ViewModel 공유
        viewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);

        // 뷰 ID 연결
        recyclerViewGroups = view.findViewById(R.id.recyclerViewGroups);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        btnCreateGroup = view.findViewById(R.id.btnCreateGroup);

        // 어댑터 설정
        adapter = new GroupAdapter(new ArrayList<>(), this);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGroups.setAdapter(adapter);

        // "그룹 만들기" 버튼 리스너
        btnCreateGroup.setOnClickListener(v -> {
            // React의 onOpenCreateModal
            // (GroupCreateDialogFragment 띄우기)
        });

        // ViewModel 관찰
        viewModel.getGroups().observe(getViewLifecycleOwner(), groups -> {
            if (groups == null || groups.isEmpty()) {
                // React의 (groups.length === 0)
                tvEmptyMessage.setVisibility(View.VISIBLE);
                recyclerViewGroups.setVisibility(View.GONE);
            } else {
                tvEmptyMessage.setVisibility(View.GONE);
                recyclerViewGroups.setVisibility(View.VISIBLE);
                adapter.submitList(groups);
            }
        });
    }

    /**
     * 그룹 카드 클릭 시 호출 (onSelectGroup)
     *
     */
    @Override
    public void onGroupClick(Group group) {
        // React의 setActiveGroupId(groupId) 로직

        // GroupDetailFragment를 생성하고 groupId 전달
        GroupDetailFragment detailFragment = GroupDetailFragment.newInstance(group.getId());

        // MainActivity의 FragmentContainer를 교체
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null) // 뒤로가기 버튼으로 돌아올 수 있게
                .commit();
    }
}