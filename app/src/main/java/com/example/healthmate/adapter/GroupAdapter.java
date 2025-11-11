package com.example.healthmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.model.Group;

import java.util.List;
import java.util.Locale;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> groups;

    // React의 onSelectGroup
    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }
    private final OnGroupClickListener clickListener;

    public GroupAdapter(List<Group> groups, OnGroupClickListener listener) {
        this.groups = groups;
        this.clickListener = listener;
    }

    public void submitList(List<Group> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.bind(group, clickListener);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    // 16.2 XML의 뷰들을 보관하는 ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvMemberCount, tvChallengeTitle, tvChallengeProgress;
        ProgressBar progressChallenge;
        // (btnGroupMenu는 이 예제에서 생략)

        ViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
            tvChallengeTitle = itemView.findViewById(R.id.tvChallengeTitle);
            tvChallengeProgress = itemView.findViewById(R.id.tvChallengeProgress);
            progressChallenge = itemView.findViewById(R.id.progressChallenge);
        }

        // React의 <GroupCard ... />
        void bind(Group group, OnGroupClickListener listener) {
            tvGroupName.setText(group.getName());
            tvMemberCount.setText(String.format(Locale.getDefault(), "%d명 참여중", group.getMembers()));
            tvChallengeTitle.setText(group.getChallenge());
            progressChallenge.setProgress(group.getProgress());
            tvChallengeProgress.setText(String.format(Locale.getDefault(), "%d%% 달성", group.getProgress()));

            // 항목 클릭 리스너
            itemView.setOnClickListener(v -> listener.onGroupClick(group));
        }
    }
}