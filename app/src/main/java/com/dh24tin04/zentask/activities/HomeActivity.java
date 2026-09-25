package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.MonHocAdapter;
import com.dh24tin04.zentask.adapters.nhatKyAdapter;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.models.HomeData;
import com.dh24tin04.zentask.models.HomeResponse;
import com.dh24tin04.zentask.models.Subject;
import com.dh24tin04.zentask.network.ApiService;
import com.dh24tin04.zentask.network.RetrofitClient;
import com.dh24tin04.zentask.util.Constants;
import com.dh24tin04.zentask.util.ThanhDieuHuong;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private TextView tvStreak, tvXemTatCa;
    private RecyclerView rvmonhoc, rvnhatky;
    private List<Subject> danhSachMonHoc;
    private MonHocAdapter monHocAdapter;
    private List<DanY> danhSachNhatKy;
    private nhatKyAdapter nhatKyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
        EdgeToEdge.enable(this);
        addview();
        addevent();
        caiDatRecyclerView();
        ThanhDieuHuong.caiDat(this, ThanhDieuHuong.TAB_TONG_QUAN);
        ApiLayDuLieuHome();
    }

    // Quay lại Trang chủ (VD sau khi tick hoàn thành ở Lịch học) -> tải lại % tiến độ và streak
    @Override
    protected void onRestart() {
        super.onRestart();
        ApiLayDuLieuHome();
    }

    private void addview() {
        tvStreak = findViewById(R.id.tvStreak);
        tvXemTatCa = findViewById(R.id.tvxemtatca);
        rvmonhoc = findViewById(R.id.rvmonhoc);
        rvnhatky = findViewById(R.id.rvnhatky);
    }

    private void caiDatRecyclerView() {
        // Môn học (Cuộn ngang)
        danhSachMonHoc = new ArrayList<>();
        monHocAdapter = new MonHocAdapter(danhSachMonHoc, item -> moDanYMonHoc(item));
        LinearLayoutManager layoutManagerMonHoc = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rvmonhoc.setLayoutManager(layoutManagerMonHoc);
        rvmonhoc.setAdapter(monHocAdapter);

        // Nhật ký (Cuộn dọc)
        danhSachNhatKy = new ArrayList<>();
        nhatKyAdapter = new nhatKyAdapter(danhSachNhatKy, item -> {
            Toast.makeText(this, "Bài học: " + item.getTieuDe(), Toast.LENGTH_SHORT).show();
        });
        rvnhatky.setLayoutManager(new LinearLayoutManager(this));
        rvnhatky.setAdapter(nhatKyAdapter);
    }

    private void moDanYMonHoc(Subject item) {
        if (item == null) return;

        // Lọc các mục dàn ý thuộc về môn học được chọn
        List<DanY> subDanY = new ArrayList<>();
        for (DanY d : danhSachNhatKy) {
            if (d.getIdSubject() == item.getIdSubject()) {
                subDanY.add(d);
            }
        }

        Intent intent = new Intent(HomeActivity.this, DanYActivity.class);
        intent.putExtra(Constants.EXTRA_ID_SUBJECT, item.getIdSubject());
        intent.putExtra(Constants.EXTRA_TEN_SUBJECT, item.getTen());
        if (!subDanY.isEmpty()) {
            intent.putExtra(Constants.EXTRA_DAN_Y_JSON, new Gson().toJson(subDanY));
            startActivity(intent);
        } else {
            // Nếu môn chưa có dàn ý, mở màn hình tạo dàn ý cho môn đó
            Toast.makeText(this, "Môn học chưa có dàn ý. Bạn có thể chọn file để AI tạo dàn ý!", Toast.LENGTH_SHORT).show();
            Intent intentTao = new Intent(HomeActivity.this, TaoDanYActivity.class);
            startActivity(intentTao);
        }
    }

    private void addevent() {
        tvXemTatCa.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DanhSachMonHocActivity.class);
            startActivity(intent);
        });
    }

    private void ApiLayDuLieuHome() {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập, không có Token!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<HomeResponse> call = apiService.getHomeData("Bearer " + token);

        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    HomeData data = response.body().getData();
                    if (tvStreak != null) {
                        tvStreak.setText("🔥 " + data.getStreak() + " ngày");
                    }
                    danhSachMonHoc.clear();
                    danhSachMonHoc.addAll(data.getSubjects());
                    monHocAdapter.notifyDataSetChanged();

                    danhSachNhatKy.clear();
                    danhSachNhatKy.addAll(data.getRecentActivities());
                    nhatKyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, "Lỗi từ máy chủ: Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Mất mạng hoặc máy chủ tắt: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}