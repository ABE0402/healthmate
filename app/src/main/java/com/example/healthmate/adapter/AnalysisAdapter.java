package com.example.healthmate.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.model.AnalysisResult;

import java.util.List;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {

    private final List<AnalysisResult> results;
    private final OnDataChangeListener listener;

    public interface OnDataChangeListener {
        void onDataChanged();
        void onItemRemoved(int position);
    }

    public AnalysisAdapter(List<AnalysisResult> results, OnDataChangeListener listener) {
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_analysis, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalysisResult item = results.get(position);

        holder.foodItemWatcher.setActive(false);
        holder.kcalWatcher.setActive(false);
        holder.servingSizeWatcher.setActive(false);

        // Corrected getter methods to match AnalysisResult model
        holder.etFoodItem.setText(item.getFoodName());
        holder.etKcal.setText(String.valueOf(item.getCalories()));
        holder.etServingSize.setText(String.valueOf(item.getServingSize()));

        holder.foodItemWatcher.setActive(true);
        holder.kcalWatcher.setActive(true);
        holder.servingSizeWatcher.setActive(true);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public List<AnalysisResult> getUpdatedResults() {
        return results;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        EditText etFoodItem, etServingSize, etKcal;
        ImageButton btnRemoveItem;
        CustomTextWatcher foodItemWatcher, servingSizeWatcher, kcalWatcher;

        ViewHolder(View itemView) {
            super(itemView);
            etFoodItem = itemView.findViewById(R.id.etFoodItem);
            etServingSize = itemView.findViewById(R.id.etServingSize);
            etKcal = itemView.findViewById(R.id.etKcal);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);

            // Corrected implementation using anonymous classes
            foodItemWatcher = new CustomTextWatcher() {
                @Override
                void onTextChanged(int position) {
                    if (position != RecyclerView.NO_POSITION) {
                        results.get(position).setFoodName(etFoodItem.getText().toString());
                    }
                }
            };

            servingSizeWatcher = new CustomTextWatcher() {
                @Override
                void onTextChanged(int position) {
                    if (position != RecyclerView.NO_POSITION) {
                        results.get(position).setServingSize(safeParseInt(etServingSize.getText().toString()));
                    }
                }
            };

            kcalWatcher = new CustomTextWatcher() {
                @Override
                void onTextChanged(int position) {
                    if (position != RecyclerView.NO_POSITION) {
                        results.get(position).setCalories(safeParseInt(etKcal.getText().toString()));
                        if (listener != null) {
                            listener.onDataChanged();
                        }
                    }
                }
            };

            etFoodItem.addTextChangedListener(foodItemWatcher);
            etServingSize.addTextChangedListener(servingSizeWatcher);
            etKcal.addTextChangedListener(kcalWatcher);

            btnRemoveItem.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemRemoved(position);
                    }
                }
            });
        }

        private int safeParseInt(String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    // Abstract helper class for TextWatcher
    abstract class CustomTextWatcher implements TextWatcher {
        private boolean active = true;

        void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (active) {
                // The position might not be available during a text change event,
                // so this abstract class cannot get the position here.
                // The anonymous class implementation in ViewHolder will handle getting the position.
            }
        }
        // This method will be implemented by the anonymous class in ViewHolder
        abstract void onTextChanged(int position);
    }
}