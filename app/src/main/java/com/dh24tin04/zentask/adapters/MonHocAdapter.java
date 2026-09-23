package com.dh24tin04.zentask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.models.MonHocItem;

import java.util.List;

public class MonHocAdapter extends RecyclerView.Adapter<MonHocAdapter.MonHocViewHolder> {

    private final List<MonHocItem> items;
    private final OnItemClickListener<MonHocItem> onClick;

    public MonHocAdapter(List<MonHocItem> items, OnItemClickListener<MonHocItem> onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MonHocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monhoc, parent, false);
        return new MonHocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonHocViewHolder holder, int position) {
        holder.bind(items.get(position), onClick);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class MonHocViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBadgeAi;
        private final TextView tvTenMonHoc;
        private final TextView tvTienDo;
        private final ProgressBar pbTienDo;

        MonHocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBadgeAi = itemView.findViewById(R.id.tv_badge_ai);
            tvTenMonHoc = itemView.findViewById(R.id.tv_tenmonhoc);
            tvTienDo = itemView.findViewById(R.id.tv_tiendo);
            pbTienDo = itemView.findViewById(R.id.pb_tiendo);
        }

        void bind(MonHocItem item, OnItemClickListener<MonHocItem> onClick) {
            tvBadgeAi.setVisibility(item.isTaoTuAi() ? View.VISIBLE : View.GONE);
            tvTenMonHoc.setText(item.getTen());
            tvTienDo.setText(String.format("Tiến độ: %d%%", item.getPhanTramTienDo()));
            pbTienDo.setProgress(item.getPhanTramTienDo());
            itemView.setOnClickListener(v -> onClick.onItemClick(item));
        }
    }
}