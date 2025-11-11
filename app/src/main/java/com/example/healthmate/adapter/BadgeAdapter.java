package com.example.healthmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.model.Badge;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ViewHolder> {

    private List<Badge> allBadges;
    private List<String> unlockedBadgeIds;

    public BadgeAdapter(List<Badge> allBadges, List<String> unlockedBadgeIds) {
        this.allBadges = allBadges;
        this.unlockedBadgeIds = unlockedBadgeIds;
    }

    public void setData(List<Badge> allBadges, List<String> unlockedBadgeIds) {
        this.allBadges = allBadges;
        this.unlockedBadgeIds = unlockedBadgeIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_badge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Badge badge = allBadges.get(position);
        holder.bind(badge, unlockedBadgeIds.contains(badge.getId()));
    }

    @Override
    public int getItemCount() {
        return allBadges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvDescription;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivBadgeIcon);
            tvName = itemView.findViewById(R.id.tvBadgeName);
            tvDescription = itemView.findViewById(R.id.tvBadgeDescription);
        }

        // React의 (isUnlocked ? 'grayscale-0' : 'grayscale opacity-60')
        //
        void bind(Badge badge, boolean isUnlocked) {
            ivIcon.setImageResource(badge.getIconResId());
            tvName.setText(badge.getName());
            tvDescription.setText(badge.getDescription());

            if (isUnlocked) {
                itemView.setAlpha(1.0f);
                // (아이콘 배경색 변경 등 추가 스타일)
            } else {
                itemView.setAlpha(0.6f); // opacity-60
                // (흑백(grayscale) 처리는 ColorFilter 추가 필요)
            }
        }
    }
}