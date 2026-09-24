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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private TextView tvStreak, tvXemTatCa;
    private RecyclerView rvmonhoc, rvnhatky;
    private List<Subject> danhSachMonHoc;
    private MonHocAdapter MonHocAdapter;
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
        ApiLayDuLieuHome();
    }
    private void addview() {
        tvStreak = findViewById(R.id.tvStreak);
        tvXemTatCa = findViewById(R.id.tvxemtatca);
        rvmonhoc = findViewById(R.id.rvmonhoc);
        rvnhatky = findViewById(R.id.rvnhatky);

    }
    private void caiDatRecyclerView() {
        // Cài đặt cho Môn học (Cuộn ngang)
        danhSachMonHoc = new ArrayList<>();
        MonHocAdapter = new MonHocAdapter(danhSachMonHoc, item -> {
            Toast.makeText(this, "Bạn chọn môn: " + item.getTen(), Toast.LENGTH_SHORT).show();
        });
        // Cài đặt hướng cuộn: HORIZONTAL
        LinearLayoutManager layoutManagerMonHoc = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rvmonhoc.setLayoutManager(layoutManagerMonHoc);
        rvmonhoc.setAdapter(MonHocAdapter);

        //Cài đặt cho Nhật ký (Cuộn dọc)
        danhSachNhatKy = new ArrayList<>();
        nhatKyAdapter = new nhatKyAdapter(danhSachNhatKy, item -> {
            Toast.makeText(this, "Bạn đã chọn: " + item.getTieuDe(), Toast.LENGTH_SHORT).show();
        });
        // Cài đặt hướng cuộn
        rvnhatky.setLayoutManager(new LinearLayoutManager(this));
        rvnhatky.setAdapter(nhatKyAdapter);
    }
    private void addevent(){
        tvXemTatCa.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            int maNguoiDung = getIntent().getIntExtra("ID_NGUOI_DUNG", 0);
            startActivity(intent);
        });
    }
    private void ApiLayDuLieuHome() {
        // Lấy Token từ bộ nhớ đệm điện thoại
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập, không có Token!", Toast.LENGTH_SHORT).show();
            return; // Dừng lại không gọi API nếu không có token
        }

        // Tạo kết nối mạng
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<HomeResponse> call = apiService.getHomeData("Bearer " + token);

        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    // Lấy cục dữ liệu bên trong ra
                    HomeData data = response.body().getData();
                    // Cập nhật giao diện
                    if(tvStreak != null) {
                        tvStreak.setText("🔥 " + data.getStreak() + " ngày");
                    }
                    danhSachMonHoc.clear();
                    danhSachMonHoc.addAll(data.getSubjects());
                    MonHocAdapter.notifyDataSetChanged();

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
