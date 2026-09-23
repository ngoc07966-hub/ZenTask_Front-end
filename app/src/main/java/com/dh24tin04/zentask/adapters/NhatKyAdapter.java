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
import com.dh24tin04.zentask.models.NhatKyItem;

import java.util.List;

public class NhatKyAdapter extends RecyclerView.Adapter<NhatKyAdapter.NhatKyViewHolder> {

    private final List<NhatKyItem> items;
    private final OnItemClickListener<NhatKyItem> onClick;

    public NhatKyAdapter(List<NhatKyItem> items, OnItemClickListener<NhatKyItem> onClick) {
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

        void bind(NhatKyItem item, OnItemClickListener<NhatKyItem> onClick) {
            int mau = Color.parseColor(item.getLoai().getMauSac());

            icNhatKy.setImageResource(item.getLoai().getIconRes());
            icNhatKy.setColorFilter(mau);

            GradientDrawable vienDrawable = (GradientDrawable) vongTronNen.getBackground().mutate();
            vienDrawable.setStroke(4, mau); // 4px ~ 1.5dp

            tvTieuDe.setText(item.getTieuDe());
            tvThoiGian.setText(item.getThoiGianHienThi());
            itemView.setOnClickListener(v -> onClick.onItemClick(item));
        }
    }
}