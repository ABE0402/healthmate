package com.example.healthmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthmate.R;
import com.example.healthmate.model.ChallengeProgress;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {

    private List<ChallengeProgress> challenges;

    public ChallengeAdapter(List<ChallengeProgress> challenges) {
        this.challenges = challenges;
    }

    public void submitList(List<ChallengeProgress> newChallenges) {
        this.challenges = newChallenges;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChallengeProgress item = challenges.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.progress.setProgress(item.getProgress());

        // 아이콘 설정
        int iconRes = R.drawable.ic_zap; // 기본
        if (item.getIcon() == ChallengeProgress.ChallengeIcon.TARGET) iconRes = R.drawable.ic_target;
        if (item.getIcon() == ChallengeProgress.ChallengeIcon.AWARD) iconRes = R.drawable.ic_award;
        holder.ivIcon.setImageResource(iconRes);

        // 상태(Status) 텍스트 및 배경
        if (item.getStatus() == ChallengeProgress.Status.COMPLETED) {
            holder.tvStatus.setText("완료");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.chart_protein)); // 초록색
        } else {
            holder.tvStatus.setText("진행중");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_inprogress);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.chart_fat)); // 주황색
        }
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription, tvStatus;
        ProgressBar progress;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivChallengeIcon);
            tvTitle = itemView.findViewById(R.id.tvChallengeTitle);
            tvDescription = itemView.findViewById(R.id.tvChallengeDesc);
            tvStatus = itemView.findViewById(R.id.tvChallengeStatus);
            progress = itemView.findViewById(R.id.progressChallenge);
        }
    }
}