package com.dh24tin04.zentask.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.models.DanY;

import java.util.List;

public class nhatKyAdapter extends RecyclerView.Adapter<nhatKyAdapter.NhatKyViewHolder> {

    private final List<DanY> items;
    private final OnItemClickListener<DanY> onClick;

    public nhatKyAdapter(List<DanY> items, OnItemClickListener<DanY> onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public NhatKyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nhatky, parent, false);
        return new NhatKyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NhatKyViewHolder holder, int position) {
        holder.bind(items.get(position), onClick);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class NhatKyViewHolder extends RecyclerView.ViewHolder {
        private final View vongTronNen;
        private final ImageView icNhatKy;
        private final TextView tvTieuDe;
        private final TextView tvThoiGian;

        NhatKyViewHolder(@NonNull View itemView) {
            super(itemView);
            vongTronNen = itemView.findViewById(R.id.bg_icon_circle_view);
            icNhatKy = itemView.findViewById(R.id.ic_nhatky);
            tvTieuDe = itemView.findViewById(R.id.tv_tieude);
            tvThoiGian = itemView.findViewById(R.id.tv_thoigian);
        }

        void bind(DanY item, OnItemClickListener<DanY> onClick) {
            int mau = Color.parseColor("#4CAF50");
            icNhatKy.setColorFilter(mau);

            GradientDrawable vienDrawable = (GradientDrawable) vongTronNen.getBackground().mutate();
            vienDrawable.setStroke(4, mau);

            tvTieuDe.setText(item.getTieuDe());
            tvThoiGian.setText(item.getNgayHoanThanh());

            itemView.setOnClickListener(v -> {
                if (onClick != null) {
                    onClick.onItemClick(item);
                }
            });
        }
    }
}