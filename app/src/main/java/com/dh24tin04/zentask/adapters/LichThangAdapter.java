package com.dh24tin04.zentask.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class LichThangAdapter extends RecyclerView.Adapter<LichThangAdapter.NgayViewHolder> {

    public static class NgayLich {
        public final int soNgay;
        public final int thang;
        public final int nam;
        public final boolean thuocThangHienTai;
        public boolean duocChon;
        public final boolean laHomNay;

        public NgayLich(int soNgay, int thang, int nam, boolean thuocThangHienTai,
                        boolean duocChon, boolean laHomNay) {
            this.soNgay = soNgay;
            this.thang = thang;
            this.nam = nam;
            this.thuocThangHienTai = thuocThangHienTai;
            this.duocChon = duocChon;
            this.laHomNay = laHomNay;
        }
    }

    public static List<NgayLich> taoLuoiThang(int nam, int thang, LocalDate ngayDangChon) {
        LocalDate ngayHomNay = LocalDate.now();
        YearMonth thangHienTai = YearMonth.of(nam, thang);
        LocalDate ngayDauThang = thangHienTai.atDay(1);
        int soNgayTrongThang = thangHienTai.lengthOfMonth();

        // offset so với Thứ 2 (0..6)
        int offsetDau = ngayDauThang.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();

        List<NgayLich> danhSach = new ArrayList<>();

        // Ngày đệm đầu tháng (thuộc tháng trước)
        if (offsetDau > 0) {
            YearMonth thangTruoc = thangHienTai.minusMonths(1);
            int soNgayThangTruoc = thangTruoc.lengthOfMonth();
            for (int i = offsetDau; i >= 1; i--) {
                int soNgay = soNgayThangTruoc - i + 1;
                danhSach.add(new NgayLich(
                        soNgay, thangTruoc.getMonthValue(), thangTruoc.getYear(),
                        false, false, false));
            }
        }

        // Ngày thuộc tháng hiện tại
        for (int soNgay = 1; soNgay <= soNgayTrongThang; soNgay++) {
            LocalDate ngay = thangHienTai.atDay(soNgay);
            boolean duocChon = ngayDangChon != null && ngay.equals(ngayDangChon);
            boolean laHomNay = ngay.equals(ngayHomNay);
            danhSach.add(new NgayLich(soNgay, thang, nam, true, duocChon, laHomNay));
        }

        // Ngày đệm cuối tháng (thuộc tháng sau) để tổng số ô chia hết cho 7
        int soConThieu = (7 - danhSach.size() % 7) % 7;
        if (soConThieu > 0) {
            YearMonth thangSau = thangHienTai.plusMonths(1);
            for (int soNgay = 1; soNgay <= soConThieu; soNgay++) {
                danhSach.add(new NgayLich(
                        soNgay, thangSau.getMonthValue(), thangSau.getYear(),
                        false, false, false));
            }
        }

        return danhSach;
    }

   //Apdapter

    public interface OnChonNgay {
        void onChon(NgayLich ngay);
    }

    private List<NgayLich> danhSachNgay;
    private final OnChonNgay onChonNgay;

    public LichThangAdapter(List<NgayLich> danhSachNgay, OnChonNgay onChonNgay) {
        this.danhSachNgay = danhSachNgay;
        this.onChonNgay = onChonNgay;
    }

    public void capNhatDuLieu(List<NgayLich> danhSachMoi) {
        this.danhSachNgay = danhSachMoi;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NgayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lich, parent, false);
        return new NgayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NgayViewHolder holder, int position) {
        NgayLich ngay = danhSachNgay.get(position);
        holder.tvNgay.setText(String.valueOf(ngay.soNgay));

        if (ngay.duocChon) {
            holder.tvNgay.setBackgroundResource(R.drawable.nen_ngay_dachon);
            holder.tvNgay.setTextColor(Color.WHITE);
            holder.tvNgay.setTypeface(null, Typeface.BOLD);
        } else {
            holder.tvNgay.setBackground(null);
            holder.tvNgay.setTypeface(null, Typeface.NORMAL);
            holder.tvNgay.setTextColor(Color.parseColor(
                    ngay.thuocThangHienTai ? "#2B1B3D" : "#C9BFDA")); // mờ ngày đệm
        }

        holder.tvNgay.setOnClickListener(v -> {
            int viTriCu = -1;
            for (int i = 0; i < danhSachNgay.size(); i++) {
                if (danhSachNgay.get(i).duocChon) {
                    viTriCu = i;
                    break;
                }
            }
            if (viTriCu != -1) {
                danhSachNgay.get(viTriCu).duocChon = false;
                notifyItemChanged(viTriCu);
            }
            ngay.duocChon = true;
            notifyItemChanged(position);
            onChonNgay.onChon(ngay);
        });
    }

    @Override
    public int getItemCount() {
        return danhSachNgay.size();
    }

    static class NgayViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNgay;

        NgayViewHolder(@NonNull TextView itemView) {
            super(itemView);
            tvNgay = itemView;
        }
    }
}