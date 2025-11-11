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
import com.example.healthmate.model.AnalysisResult; // 4.2에서 만든 모델

import java.util.List;

// React의 EditableFoodItem 리스트 로직
public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {

    private List<AnalysisResult> results;

    // 총 칼로리 계산 및 삭제를 위한 콜백
    public interface OnDataChangeListener {
        void onDataChanged();
        void onItemRemoved(int position);
    }
    private OnDataChangeListener listener;

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

        // TextWatcher를 임시로 제거 (데이터 바인딩 시 무한 루프 방지)
        holder.foodItemWatcher.setActive(false);
        holder.kcalWatcher.setActive(false);
        holder.servingSizeWatcher.setActive(false);

        holder.etFoodItem.setText(item.getFoodItem());
        holder.etKcal.setText(String.valueOf(item.getKcal()));
        holder.etServingSize.setText(String.valueOf(item.getServingSize()));

        // TextWatcher를 다시 활성화
        holder.foodItemWatcher.setActive(true);
        holder.kcalWatcher.setActive(true);
        holder.servingSizeWatcher.setActive(true);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    // 수정된 리스트 반환
    public List<AnalysisResult> getUpdatedResults() {
        return results;
    }

    // React의 onResultChange와 onRemoveItem
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

            // TextWatcher 설정 (React의 onChange)
            foodItemWatcher = new CustomTextWatcher(
                    pos -> results.get(pos).setFoodItem(etFoodItem.getText().toString())
            );
            servingSizeWatcher = new CustomTextWatcher(
                    pos -> results.get(pos).setServingSize(safeParseInt(etServingSize.getText().toString()))
            );
            kcalWatcher = new CustomTextWatcher(
                    pos -> {
                        results.get(pos).setKcal(safeParseInt(etKcal.getText().toString()));
                        listener.onDataChanged(); // 칼로리 변경 시 총합 다시 계산
                    }
            );

            etFoodItem.addTextChangedListener(foodItemWatcher);
            etServingSize.addTextChangedListener(servingSizeWatcher);
            etKcal.addTextChangedListener(kcalWatcher);

            // 삭제 버튼 (onRemoveItem)
            btnRemoveItem.setOnClickListener(v -> {
                listener.onItemRemoved(getAdapterPosition());
            });
        }

        private int safeParseInt(String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
        }

        // TextWatcher를 동적으로 켜고 끄기 위한 헬퍼 클래스
        abstract class CustomTextWatcher implements TextWatcher {
            private boolean active = true;
            void setActive(boolean active) { this.active = active; }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (active) {
                    onTextChanged(getAdapterPosition());
                }
            }
            abstract void onTextChanged(int position);
        }
    }
}