package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.OutlineTreeAdapter;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.util.Constants;
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
        btnLenLich.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng lên lịch sẽ làm ở bước tiếp theo", Toast.LENGTH_SHORT).show();
        });
    }
    private void hienThiDanY() {
        tvTenMonHoc.setText(tenMon);
        tvThoiGianCapNhat.setText("Vừa xong");
        rvDanY.setAdapter(new OutlineTreeAdapter(dsDanY, tenMon));
    }
}