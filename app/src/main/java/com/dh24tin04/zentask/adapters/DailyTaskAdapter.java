package com.dh24tin04.zentask.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.models.LichHoc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Danh sách bài học của 1 ngày trong màn Lịch học
public class DailyTaskAdapter extends RecyclerView.Adapter<DailyTaskAdapter.TaskViewHolder> {

    public interface OnTaskListener {
        // Người dùng tick 1 mục chưa hoàn thành
        void onHoanThanh(LichHoc item);

        // Bấm nút ⋯
        void onXemChiTiet(LichHoc item);
    }

    private final List<LichHoc> danhSach = new ArrayList<>();
    // Id các mục đang chờ API trả về -> khóa checkbox, chặn bấm đúp
    private final Set<Integer> dangGui = new HashSet<>();
    private final OnTaskListener listener;

    public DailyTaskAdapter(OnTaskListener listener) {
        this.listener = listener;
    }

    public void capNhatDuLieu(List<LichHoc> dsMoi) {
        danhSach.clear();
        if (dsMoi != null) danhSach.addAll(dsMoi);
        dangGui.clear();
        notifyDataSetChanged();
    }

    public int getSoMuc() {
        return danhSach.size();
    }

    public int getSoMucHoanThanh() {
        int dem = 0;
        for (LichHoc item : danhSach) {
            if (item.isTrangThaiHoanThanh()) dem++;
        }
        return dem;
    }

    public void batDauGui(LichHoc item) {
        dangGui.add(item.getIdDanY());
        veLai(item);
    }

    // thanhCong = false -> checkbox trở lại chưa tick
    public void ketThucGui(LichHoc item, boolean thanhCong) {
        dangGui.remove(item.getIdDanY());
        if (thanhCong) item.setTrangThaiHoanThanh(true);
        veLai(item);
    }

    private void veLai(LichHoc item) {
        int viTri = danhSach.indexOf(item);
        if (viTri != -1) notifyItemChanged(viTri);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        LichHoc item = danhSach.get(position);
        boolean daXong = item.isTrangThaiHoanThanh();
        boolean choPhanHoi = dangGui.contains(item.getIdDanY());

        holder.tvTenMuc.setText(item.getTieuDe());
        holder.tvTenMon.setText(item.getTenMon());

        // Đang gửi thì giữ dấu tick để người dùng thấy phản hồi ngay
        holder.cbHoanThanh.setChecked(daXong || choPhanHoi);
        // Backend không có API bỏ hoàn thành -> mục đã xong thì khóa luôn
        holder.cbHoanThanh.setEnabled(!daXong && !choPhanHoi);

        // Gạch ngang + làm mờ mục đã xong
        int flags = holder.tvTenMuc.getPaintFlags();
        holder.tvTenMuc.setPaintFlags(daXong
                ? flags | Paint.STRIKE_THRU_TEXT_FLAG
                : flags & ~Paint.STRIKE_THRU_TEXT_FLAG);
        holder.itemView.setAlpha(daXong ? 0.6f : 1f);

        // Dùng onClick thay vì onCheckedChanged: setChecked() lúc bind không được gọi API
        holder.cbHoanThanh.setOnClickListener(v -> {
            if (item.isTrangThaiHoanThanh() || dangGui.contains(item.getIdDanY())) return;
            listener.onHoanThanh(item);
        });
        holder.btnMenu.setOnClickListener(v -> listener.onXemChiTiet(item));
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final CheckBox cbHoanThanh;
        final TextView tvTenMuc, tvTenMon, btnMenu;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbHoanThanh = itemView.findViewById(R.id.cb_hoan_thanh);
            tvTenMuc = itemView.findViewById(R.id.tv_ten_muc);
            tvTenMon = itemView.findViewById(R.id.tv_ten_mon);
            btnMenu = itemView.findViewById(R.id.btn_menu_task);
        }
    }
}
