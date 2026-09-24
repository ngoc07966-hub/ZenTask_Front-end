package com.dh24tin04.zentask.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.models.DanY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutlineTreeAdapter extends RecyclerView.Adapter<OutlineTreeAdapter.VH> {

    // DoTinCay (0–1) do AI trả về; thấp hơn ngưỡng này thì gắn nhãn "Cần thêm tài liệu"
    private static final double NGUONG_DO_TIN_CAY = 0.6;
    private static final int THUT_LE_DP = 16;    // mỗi cấp thụt thêm 16dp
    private static final int DO_SAU_TOI_DA = 5;  // tránh thụt quá sâu làm hẹp tiêu đề

    private final List<DanY> ds;
    private final int[] doSau;      // doSau[i] = độ sâu của ds.get(i), gốc = 0
    private final String tenMon;

    public OutlineTreeAdapter(List<DanY> danhSach, String tenMon) {
        this.tenMon = tenMon == null ? "" : tenMon;

        // Sắp theo IdDanY: server lưu tuần tự đúng thứ tự AI trả về (cha luôn được lưu trước con),
        this.ds = new ArrayList<>(danhSach);
        Collections.sort(this.ds, (a, b) -> Integer.compare(a.getIdDanY(), b.getIdDanY()));

        // Độ sâu = độ sâu của cha + 1 (dựa vào ParentId thật, không dựa CapDo thô
        this.doSau = new int[this.ds.size()];
        Map<Integer, Integer> doSauTheoId = new HashMap<>();
        for (int i = 0; i < this.ds.size(); i++) {
            DanY d = this.ds.get(i);
            Integer parentId = d.getParentId();
            int sau = 0;
            if (parentId != null && doSauTheoId.containsKey(parentId)) {
                sau = doSauTheoId.get(parentId) + 1;
            }
            doSau[i] = sau;
            doSauTheoId.put(d.getIdDanY(), sau);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dan_y, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DanY d = ds.get(position);
        int sau = doSau[position];

        // Tiêu đề + icon: mục gốc đậm, mục con thường
        h.tvTieuDe.setText(d.getTieuDe());
        h.tvTieuDe.setTypeface(null, sau == 0 ? Typeface.BOLD : Typeface.NORMAL);
        h.tvIcon.setText(sau == 0 ? "☰" : "•");

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) h.itemView.getLayoutParams();
        float density = h.itemView.getResources().getDisplayMetrics().density;
        lp.setMarginStart((int) (THUT_LE_DP * Math.min(sau, DO_SAU_TOI_DA) * density));
        h.itemView.setLayoutParams(lp);

        boolean canThemTaiLieu = d.getDoTinCay() < NGUONG_DO_TIN_CAY;
        h.tvBadge.setText(canThemTaiLieu ? "Cần thêm tài liệu" : tenMon);
        h.tvBadge.setTextColor(Color.parseColor(canThemTaiLieu ? "#B08900" : "#63587B"));
        h.tvBadge.setVisibility(!canThemTaiLieu && tenMon.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return ds.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvIcon, tvTieuDe, tvBadge;

        VH(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_icon_muc);
            tvTieuDe = itemView.findViewById(R.id.tv_tieu_de);
            tvBadge = itemView.findViewById(R.id.tv_badge_trang_thai);
        }
    }
}