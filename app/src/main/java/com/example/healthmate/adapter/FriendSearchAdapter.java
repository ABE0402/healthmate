package com.example.healthmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bumptech.glide.Glide;
import com.example.healthmate.R;
import com.example.healthmate.model.Friend;

import java.util.List;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder> {

    private List<Friend> friends;

    // React의 onAddFriend(name)
    public interface OnAddFriendClickListener {
        void onAddFriendClick(Friend friend);
    }
    private final OnAddFriendClickListener listener;

    public FriendSearchAdapter(List<Friend> friends, OnAddFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    public void submitList(List<Friend> newFriends) {
        this.friends = newFriends;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_friend_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.bind(friend, listener);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        ImageButton btnAddFriend;

        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivFriendAvatar);
            tvName = itemView.findViewById(R.id.tvFriendName);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
        }

        void bind(Friend friend, OnAddFriendClickListener listener) {
            tvName.setText(friend.getName());
            Glide.with(itemView.getContext()).load(friend.getAvatarUrl()).circleCrop().into(ivAvatar);

            btnAddFriend.setOnClickListener(v -> listener.onAddFriendClick(friend));
        }
    }
}