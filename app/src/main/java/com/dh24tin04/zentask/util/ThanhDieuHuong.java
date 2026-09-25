package com.dh24tin04.zentask.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.activities.DanhSachMonHocActivity;
import com.dh24tin04.zentask.activities.HoSoActivity;
import com.dh24tin04.zentask.activities.HomeActivity;
import com.dh24tin04.zentask.activities.LichHocActivity;
import com.dh24tin04.zentask.activities.TaoDanYActivity;

public final class ThanhDieuHuong {

    public static final int TAB_TONG_QUAN = 0;
    public static final int TAB_NAP_LIEU = 1;
    public static final int TAB_DAN_Y = 2;
    public static final int TAB_LICH_HOC = 3;
    public static final int TAB_THONG_TIN = 4;

    private static final String MAU_DANG_CHON = "#6C4AB6";
    private static final String MAU_CHUA_CHON = "#ADA3BD";

    // Thứ tự các mảng phải khớp với các hằng TAB
    private static final int[] ID_TAB = {
            R.id.tab_tongquan, R.id.tab_naplieu, R.id.tab_dany, R.id.tab_lichhoc, R.id.tab_thongtin};
    private static final int[] ID_PILL = {
            R.id.pill_tongquan, R.id.pill_naplieu, R.id.pill_dany, R.id.pill_lichhoc, R.id.pill_thongtin};
    private static final int[] ID_ICON = {
            R.id.ic_tongquan, R.id.ic_naplieu, R.id.ic_dany, R.id.ic_lichhoc, R.id.ic_thongtin};
    private static final int[] ID_CHU = {
            R.id.tv_tongquan, R.id.tv_naplieu, R.id.tv_dany, R.id.tv_lichhoc, R.id.tv_thongtin};
    private static final Class<?>[] MAN_HINH = {
            HomeActivity.class, TaoDanYActivity.class, DanhSachMonHocActivity.class,
            LichHocActivity.class, HoSoActivity.class};

    private ThanhDieuHuong() {}

    public static void caiDat(Activity activity, int tabDangChon) {
        for (int i = 0; i < ID_TAB.length; i++) {
            View tab = activity.findViewById(ID_TAB[i]);
            if (tab == null) return; // layout không có thanh điều hướng

            toMauTab(activity, i, i == tabDangChon);

            final Class<?> manHinhDich = MAN_HINH[i];
            tab.setOnClickListener(v -> chuyenManHinh(activity, manHinhDich));
        }
    }

    private static void chuyenManHinh(Activity activity, Class<?> manHinhDich) {
        // Bấm vào tab của chính màn hình đang mở -> không làm gì
        if (activity.getClass() == manHinhDich) return;

        Intent intent = new Intent(activity, manHinhDich);
        if (manHinhDich == HomeActivity.class) {
            // Quay về Trang chủ đã có sẵn thay vì mở thêm một Trang chủ mới
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        activity.startActivity(intent);

        // Giữ Trang chủ ở dưới cùng, các màn hình khác đóng lại để không chồng nhiều lớp
        if (!(activity instanceof HomeActivity)) {
            activity.finish();
        }
    }

    private static void toMauTab(Activity activity, int viTri, boolean dangChon) {
        int mau = Color.parseColor(dangChon ? MAU_DANG_CHON : MAU_CHUA_CHON);

        activity.findViewById(ID_PILL[viTri])
                .setBackgroundResource(dangChon ? R.drawable.bg_nav_pill : 0);

        ImageView icon = activity.findViewById(ID_ICON[viTri]);
        icon.setColorFilter(mau);

        TextView chu = activity.findViewById(ID_CHU[viTri]);
        chu.setTextColor(mau);
        chu.setTypeface(null, dangChon ? Typeface.BOLD : Typeface.NORMAL);
    }
}
