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
import com.dh24tin04.zentask.models.DanY; // Đổi thành Import Model DanY

import java.util.List;

public class nhatKyAdapter extends RecyclerView.Adapter<nhatKyAdapter.NhatKyViewHolder> {

    // 1. Đổi toàn bộ NhatKyItem thành DanY
    private final List<DanY> items;
    private final OnItemClickListener onClick;

    // 2. Khai báo Interface cho sự kiện Click ngay trong Adapter
    public interface OnItemClickListener {
        void onItemClick(DanY item);
    }

    public nhatKyAdapter(List<DanY> items, OnItemClickListener onClick) {
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

        void bind(DanY item, OnItemClickListener onClick) {
            // Thiết lập màu xanh lá mặc định cho các task đã hoàn thành
            int mau = Color.parseColor("#4CAF50");
            icNhatKy.setColorFilter(mau);

            GradientDrawable vienDrawable = (GradientDrawable) vongTronNen.getBackground().mutate();
            vienDrawable.setStroke(4, mau);

            // Gắn dữ liệu từ Database vào Giao diện
            tvTieuDe.setText(item.getTieuDe());
            // Vì DanY trả về Date dưới dạng String, ta lấy NgayHoanThanh để hiển thị
            tvThoiGian.setText(item.getNgayHoanThanh());

            itemView.setOnClickListener(v -> onClick.onItemClick(item));
        }
    }
}