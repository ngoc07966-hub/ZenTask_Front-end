package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.OutlineTreeAdapter;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.util.Constants;
import com.dh24tin04.zentask.util.ThanhDieuHuong;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DanYActivity extends AppCompatActivity {

    private int idSubject = -1;
    private String tenMon = "";
    private List<DanY> dsDanY = new ArrayList<>();

    private TextView tvTenMonHoc, tvThoiGianCapNhat, btnLenLich;
    private RecyclerView rvDanY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danyactivity);

        if (!docDuLieuTuIntent()) {
            Toast.makeText(this, "Không có dữ liệu dàn ý để hiển thị", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        addview();
        addevent();
        ThanhDieuHuong.caiDat(this, ThanhDieuHuong.TAB_DAN_Y);
        hienThiDanY();
    }

    private boolean docDuLieuTuIntent() {
        Intent in = getIntent();
        idSubject = in.getIntExtra(Constants.EXTRA_ID_SUBJECT, -1);
        String ten = in.getStringExtra(Constants.EXTRA_TEN_SUBJECT);
        tenMon = ten == null ? "" : ten;
        String json = in.getStringExtra(Constants.EXTRA_DAN_Y_JSON);

        if (idSubject == -1 || json == null) return false;
        try {
            List<DanY> ds = new Gson().fromJson(json, new TypeToken<List<DanY>>() {}.getType());
            if (ds == null || ds.isEmpty()) return false;
            dsDanY = ds;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void addview() {
        tvTenMonHoc = findViewById(R.id.tv_ten_mon_hoc);
        tvThoiGianCapNhat = findViewById(R.id.tv_thoi_gian_cap_nhat);
        rvDanY = findViewById(R.id.rv_dan_y);
        btnLenLich = findViewById(R.id.btn_len_lich);
    }

    private void addevent() {
        btnLenLich.setOnClickListener(v -> moManTaoLich());
    }

    // Backend cần Deadline trước khi xếp lịch -> sang màn chọn hạn chót
    private void moManTaoLich() {
        Intent i = new Intent(this, TaoLichActivity.class);
        i.putExtra(Constants.EXTRA_ID_SUBJECT, idSubject);
        i.putExtra(Constants.EXTRA_TEN_SUBJECT, tenMon);
        i.putExtra(Constants.EXTRA_SO_MUC, dsDanY.size());
        startActivity(i);
    }

    private void hienThiDanY() {
        tvTenMonHoc.setText(tenMon);
        tvThoiGianCapNhat.setText("Vừa xong");

        OutlineTreeAdapter adapter = new OutlineTreeAdapter(dsDanY, tenMon, item -> hienThiChiTietMuc(item));
        rvDanY.setAdapter(adapter);
    }

    private void hienThiChiTietMuc(DanY item) {
        if (item == null) return;

        String noiDung = item.getNoiDung();
        if (noiDung == null || noiDung.trim().isEmpty()) {
            noiDung = "Chưa có nội dung tóm tắt chi tiết cho mục này.";
        }

        String thongTin = "📌 Môn học: " + tenMon + "\n\n"
                + "📝 Nội dung chi tiết:\n" + noiDung + "\n\n"
                + String.format("📊 Độ tin cậy AI: %.0f%%", item.getDoTinCay() * 100);

        new AlertDialog.Builder(this)
                .setTitle(item.getTieuDe())
                .setMessage(thongTin)
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }
}