package com.example.healthmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.model.Meal; // 1단계에서 만든 모델

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// React의 MealLog.tsx + MealItem.tsx 로직
public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 뷰 타입 정의 (React의 조건부 렌더링과 유사)
    private static final int VIEW_TYPE_HEADER = 0; // "아침", "점심" 헤더
    private static final int VIEW_TYPE_MEAL = 1;   // list_item_meal.xml 아이템

    // List<Object>를 사용해 헤더(String)와 아이템(Meal)을 모두 담음
    private List<Object> items = new ArrayList<>();

    // 삭제 버튼 클릭 리스너 (React의 onDeleteMeal)
    public interface OnDeleteClickListener {
        void onDeleteClick(Meal meal);
    }
    private OnDeleteClickListener deleteClickListener;

    public MealAdapter(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // 1. 뷰 타입 결정 (React의 'if (isHeader) return <Header />')
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_MEAL;
        }
    }

    // 2. 뷰 타입에 맞는 ViewHolder(XML) 생성
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            // 헤더용 XML (간단한 TextView) - (별도 XML `list_item_header.xml` 생성 필요)
            View view = inflater.inflate(R.layout.list_item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // 5.1에서 만든 식단 아이템 XML
            View view = inflater.inflate(R.layout.list_item_meal, parent, false);
            return new MealViewHolder(view);
        }
    }

    // 3. ViewHolder에 데이터 바인딩 (React의 Props 전달)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            // 헤더 텍스트 설정
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvHeader.setText((String) items.get(position));
        } else {
            // 식단 아이템 데이터 설정 (MealItem.tsx 로직)
            MealViewHolder mealHolder = (MealViewHolder) holder;
            Meal meal = (Meal) items.get(position);

            mealHolder.tvFoodItem.setText(meal.getFoodName());
            mealHolder.tvKcal.setText(String.format(Locale.getDefault(), "%d", meal.getCalories()));

            // 삭제 버튼 클릭 리스너 연결
            mealHolder.btnDeleteMeal.setOnClickListener(v ->
                    deleteClickListener.onDeleteClick(meal)
            );
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 4. 데이터 갱신 (ViewModel에서 호출)
    public void submitList(List<Meal> meals) {
        // MealLog.tsx의 groupedMeals 로직 구현
        this.items.clear();

        // (실제로는 Meal.MealTime enum 순서대로 정렬/그룹핑해야 함)
        // ... 그룹핑 로직 ...

        // 예시: "점심" 헤더 추가
        if (meals.size() > 0) {
            this.items.add("점심");
            this.items.addAll(meals);
        }

        notifyDataSetChanged();
    }

    // --- ViewHolder 클래스들 ---

    // 5.1 XML의 뷰들을 보관하는 MealViewHolder
    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodItem, tvServingSize, tvKcal;
        ImageButton btnDeleteMeal;

        MealViewHolder(View itemView) {
            super(itemView);
            tvFoodItem = itemView.findViewById(R.id.tvFoodItem);
            tvServingSize = itemView.findViewById(R.id.tvServingSize);
            tvKcal = itemView.findViewById(R.id.tvKcal);
            btnDeleteMeal = itemView.findViewById(R.id.btnDeleteMeal);
        }
    }

    // 헤더 XML의 뷰들을 보관하는 HeaderViewHolder
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader); // (list_item_header.xml에 정의 필요)
        }
    }
}