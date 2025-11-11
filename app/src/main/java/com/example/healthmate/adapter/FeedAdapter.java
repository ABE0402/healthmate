package com.example.healthmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.model.GroupFeedItem;

import java.util.Date;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<GroupFeedItem> feedItems;

    // React의 onToggleLike
    public interface OnLikeClickListener {
        void onLikeClick(GroupFeedItem item);
    }
    private final OnLikeClickListener likeListener;

    public FeedAdapter(List<GroupFeedItem> items, OnLikeClickListener listener) {
        this.feedItems = items;
        this.likeListener = listener;
    }

    public void submitList(List<GroupFeedItem> newItems) {
        this.feedItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupFeedItem item = feedItems.get(position);
        holder.bind(item, likeListener);
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    private static String formatRelativeTime(Date timestamp) {
        if (timestamp == null) {
            return "";
        }
        long time = timestamp.getTime();
        long now = System.currentTimeMillis();
        long diff = now - time;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "일 전";
        } else if (hours > 0) {
            return hours + "시간 전";
        } else if (minutes > 0) {
            return minutes + "분 전";
        } else {
            return "방금 전";
        }
    }

    // 17.1 XML의 뷰들을 보관
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFeedIcon;
        TextView tvFeedContent, tvFeedTimestamp;
        Button btnLike;

        ViewHolder(View itemView) {
            super(itemView);
            ivFeedIcon = itemView.findViewById(R.id.ivFeedIcon);
            tvFeedContent = itemView.findViewById(R.id.tvFeedContent);
            tvFeedTimestamp = itemView.findViewById(R.id.tvFeedTimestamp);
            btnLike = itemView.findViewById(R.id.btnLike);
        }

        // React의 FeedItem.tsx 로직
        void bind(GroupFeedItem item, OnLikeClickListener listener) {
            Context context = itemView.getContext();

            // 아이콘 설정 (type: 'meal' | 'challenge')
            if (item.getType() == GroupFeedItem.FeedType.CHALLENGE) {
                ivFeedIcon.setImageResource(R.drawable.ic_award);
            } else {
                ivFeedIcon.setImageResource(R.drawable.ic_utensils);
            }

            // 텍스트 설정
            tvFeedContent.setText(String.format("%s님이 %s", item.getUserName(), item.getContent()));
            tvFeedTimestamp.setText(formatRelativeTime(item.getTimestamp())); // (formatRelativeTime 함수 구현 필요)

            // "응원하기" 버튼 (likedByMe)
            if (item.isLikedByMe()) {
                btnLike.setText("응원 취소");
                btnLike.setTextColor(ContextCompat.getColor(context, R.color.primary_blue));
                btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbs_up, 0, 0, 0); // (채워진 아이콘)
            } else {
                btnLike.setText("응원하기 (" + item.getLikes() + ")");
                btnLike.setTextColor(ContextCompat.getColor(context, R.color.text_sub_light));
                btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbs_up, 0, 0, 0);
            }

            btnLike.setOnClickListener(v -> listener.onLikeClick(item));
        }
    }
}