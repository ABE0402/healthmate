package com.example.healthmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_MODEL = 2;

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void submitList(List<ChatMessage> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged(); // (실제 앱에서는 DiffUtil 사용 권장)
    }

    // React의 msg.role === 'user' ? ...
    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getRole() == ChatMessage.Role.USER) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_MODEL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.list_item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.list_item_chat_model, parent, false);
            return new ModelMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((UserMessageViewHolder) holder).bind(message);
        } else {
            ((ModelMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // 사용자 뷰홀더 (15.2 XML)
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        UserMessageViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tvMessage);
        }
        void bind(ChatMessage message) {
            tvMessage.setText(message.getText());
        }
    }

    // AI 뷰홀더 (15.3 XML)
    static class ModelMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ProgressBar progressLoading;
        ModelMessageViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tvMessage);
            progressLoading = view.findViewById(R.id.progressLoading);
        }
        void bind(ChatMessage message) {
            // React의 {isLoading && ...}
            if (message.isLoading()) {
                tvMessage.setVisibility(View.GONE);
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.GONE);
                tvMessage.setText(message.getText());
            }
        }
    }
}