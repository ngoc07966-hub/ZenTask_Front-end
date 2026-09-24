package com.dh24tin04.zentask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.models.Subject;

import java.util.List;

public class MonHocAdapter extends RecyclerView.Adapter<MonHocAdapter.MonHocViewHolder> {

    private final List<Subject> items;
    private final OnItemClickListener onClick;

    public interface OnItemClickListener {
        void onItemClick(Subject item);
    }

    public MonHocAdapter(List<Subject> items, OnItemClickListener onClick) {
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

        void bind(Subject item, OnItemClickListener onClick) {
            tvBadgeAi.setVisibility(View.GONE);
            tvTenMonHoc.setText(item.getTen());

            int phanTram = 0;
            if (item.getTongSoMuc() > 0) {
                phanTram = Math.round(((float) item.getSoMucHoanThanh() / item.getTongSoMuc()) * 100);
            }

            tvTienDo.setText(String.format("Tiến độ: %d%%", phanTram));
            pbTienDo.setProgress(phanTram);

            itemView.setOnClickListener(v -> onClick.onItemClick(item));
        }
    }
}