package com.example.healthmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.model.MealPlan;
import com.example.healthmate.model.PlannedMeal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlannedMealAdapter extends RecyclerView.Adapter<PlannedMealAdapter.ViewHolder> {

    // (아침, 점심, 저녁, 간식) 4종류의 데이터를 담을 리스트
    private List<PlannedMealItem> mealItems = new ArrayList<>();

    // "기록" 버튼 클릭 리스너 (React의 onLogMeal)
    public interface OnLogMealListener {
        void onLogMealClick(String foodItem);
    }
    private final OnLogMealListener logListener;

    public PlannedMealAdapter(OnLogMealListener listener) {
        this.logListener = listener;
    }

    // ViewModel에서 MealPlan 데이터를 받으면 리스트 갱신
    public void setMealPlan(MealPlan plan) {
        mealItems.clear();
        if (plan == null) {
            notifyDataSetChanged();
            return;
        }

        if (plan.getBreakfast() != null) {
            mealItems.add(new PlannedMealItem("아침", R.drawable.ic_sun, plan.getBreakfast()));
        }
        if (plan.getLunch() != null) {
            mealItems.add(new PlannedMealItem("점심", R.drawable.ic_utensils, plan.getLunch()));
        }
        if (plan.getDinner() != null) {
            mealItems.add(new PlannedMealItem("저녁", R.drawable.ic_moon, plan.getDinner()));
        }
        if (plan.getSnacks() != null) {
            mealItems.add(new PlannedMealItem("간식", R.drawable.ic_sandwich, plan.getSnacks()));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_planned_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlannedMealItem item = mealItems.get(position);
        PlannedMeal meal = item.meal;

        holder.ivMealIcon.setImageResource(item.iconResId);
        holder.tvMealTitle.setText(String.format(Locale.getDefault(),
                "%s - %d kcal", item.title, meal.getKcal()));
        holder.tvMealName.setText(meal.getName());
        holder.tvMealDescription.setText(meal.getDescription());

        holder.btnLogMeal.setOnClickListener(v ->
                logListener.onLogMealClick(meal.getName())
        );
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }

    // 13.1 XML의 뷰들을 보관하는 ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMealIcon;
        TextView tvMealTitle, tvMealName, tvMealDescription;
        Button btnLogMeal;

        ViewHolder(View itemView) {
            super(itemView);
            ivMealIcon = itemView.findViewById(R.id.ivMealIcon);
            tvMealTitle = itemView.findViewById(R.id.tvMealTitle);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealDescription = itemView.findViewById(R.id.tvMealDescription);
            btnLogMeal = itemView.findViewById(R.id.btnLogMeal);
        }
    }

    // 어댑터 내부에서 사용할 헬퍼 클래스
    private static class PlannedMealItem {
        final String title;
        final int iconResId;
        final PlannedMeal meal;

        PlannedMealItem(String title, int iconResId, PlannedMeal meal) {
            this.title = title;
            this.iconResId = iconResId;
            this.meal = meal;
        }
    }
}