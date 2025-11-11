package com.example.healthmate.view.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.example.healthmate.R;
import com.example.healthmate.adapter.FriendSearchAdapter;
import com.example.healthmate.model.Friend;
import com.example.healthmate.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class AddFriendDialogFragment extends DialogFragment
        implements FriendSearchAdapter.OnAddFriendClickListener {

    private ProfileViewModel viewModel;

    // 22.2 XML 뷰
    private TextInputEditText etSearchTerm;
    private RecyclerView recyclerViewSearch;
    private ImageButton btnClose;

    private FriendSearchAdapter adapter;

    public static AddFriendDialogFragment newInstance() {
        return new AddFriendDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 부모(ProfileFragment)와 ViewModel 공유
        viewModel = new ViewModelProvider(requireParentFragment()).get(ProfileViewModel.class);

        // 뷰 ID 연결
        etSearchTerm = view.findViewById(R.id.etSearchTerm);
        recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
        btnClose = view.findViewById(R.id.btnClose);

        // 어댑터 설정
        adapter = new FriendSearchAdapter(new ArrayList<>(), this);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dismiss());

        // 검색창 리스너 (React의 setSearchTerm)
        etSearchTerm.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchFriends(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ViewModel 관찰 (검색 결과)
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), friends -> {
            adapter.submitList(friends);
        });
    }

    /**
     * "추가" 버튼 클릭 (onAddFriend)
     *
     */
    @Override
    public void onAddFriendClick(Friend friend) {
        viewModel.addFriend(friend);
        Toast.makeText(getContext(), friend.getName() + "님과 친구가 되었습니다!", Toast.LENGTH_SHORT).show();
        dismiss(); // 모달 닫기
    }
}