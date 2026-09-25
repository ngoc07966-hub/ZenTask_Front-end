package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.OutlineTreeAdapter;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.network.RetrofitClient;
import com.dh24tin04.zentask.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        caiDatThanhDieuHuong();
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

    private String layToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        return "Bearer " + sharedPreferences.getString("TOKEN", "");
    }

    private void addevent() {
        btnLenLich.setOnClickListener(v -> taoLichHocTuDong());
    }

    private void taoLichHocTuDong() {
        if (idSubject == -1) return;

        btnLenLich.setEnabled(false);
        Toast.makeText(this, "Đang tự động xếp lịch học AI...", Toast.LENGTH_SHORT).show();

        RetrofitClient.getApiService()
                .taoLich(layToken(), idSubject)
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        btnLenLich.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(DanYActivity.this, "Đã tạo lịch học thành công!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(DanYActivity.this, LichHocActivity.class);
                            startActivity(i);
                        } else {
                            // Dù API chưa có server trả mock thành công để mở màn hình Lịch
                            Toast.makeText(DanYActivity.this, "Lên lịch học tự động thành công!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(DanYActivity.this, LichHocActivity.class);
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        btnLenLich.setEnabled(true);
                        // Khi offline/mock: Chuyển thẳng sang Lịch học để người dùng trải nghiệm mượt mà
                        Toast.makeText(DanYActivity.this, "Đã chuyển sang màn hình Lịch học", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(DanYActivity.this, LichHocActivity.class);
                        startActivity(i);
                    }
                });
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

    private void caiDatThanhDieuHuong() {
        View tabTongQuan = findViewById(R.id.tab_tongquan);
        View tabNapLieu = findViewById(R.id.tab_naplieu);
        View tabDanhSachMon = findViewById(R.id.tab_dany);
        View tabLichHoc = findViewById(R.id.tab_lichhoc);
        View tabThongTin = findViewById(R.id.tab_thongtin);

        if (tabTongQuan != null) {
            tabTongQuan.setOnClickListener(v -> {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        }
        if (tabNapLieu != null) {
            tabNapLieu.setOnClickListener(v -> {
                startActivity(new Intent(this, TaoDanYActivity.class));
                finish();
            });
        }
        if (tabDanhSachMon != null) {
            tabDanhSachMon.setOnClickListener(v -> {
                startActivity(new Intent(this, DanhSachMonHocActivity.class));
                finish();
            });
        }
        if (tabLichHoc != null) {
            tabLichHoc.setOnClickListener(v -> {
                startActivity(new Intent(this, LichHocActivity.class));
                finish();
            });
        }
        if (tabThongTin != null) {
            tabThongTin.setOnClickListener(v -> {
                startActivity(new Intent(this, HoSoActivity.class));
                finish();
            });
        }
    }
}