package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.MonHocAdapter;
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

public class DanhSachMonHocActivity extends AppCompatActivity {
    private RecyclerView rvTatCaMonHoc;
    private TextView btnBack, btnThemMonHoc;
    private List<Subject> danhSachMonHoc;
    private MonHocAdapter monHocAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danhsachmonhocactivity);
        addview();
        addevent();
        caiDatRecyclerView();
        ApiLayDanhSachMonHoc();
    }

    private void addview() {
        rvTatCaMonHoc = findViewById(R.id.rv_tatcamonhoc);
        btnBack = findViewById(R.id.btn_back);
        btnThemMonHoc = findViewById(R.id.btn_them_monhoc);
    }

    private void addevent() {
        btnBack.setOnClickListener(v -> finish());

        btnThemMonHoc.setOnClickListener(v -> {
            Intent intent = new Intent(DanhSachMonHocActivity.this, TaoWorkspaceActivity.class);
            startActivity(intent);
        });
    }

    private void caiDatRecyclerView() {
        danhSachMonHoc = new ArrayList<>();
        monHocAdapter = new MonHocAdapter(danhSachMonHoc, item -> {
            Toast.makeText(this, "Bạn chọn môn: " + item.getTen(), Toast.LENGTH_SHORT).show();
        });
        // Lưới 2 cột — khớp comment "Danh sách dạng lưới" trong layout
        rvTatCaMonHoc.setLayoutManager(new GridLayoutManager(this, 2));
        rvTatCaMonHoc.setAdapter(monHocAdapter);
    }

    private void ApiLayDanhSachMonHoc() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
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
                    danhSachMonHoc.clear();
                    danhSachMonHoc.addAll(data.getSubjects());
                    monHocAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DanhSachMonHocActivity.this, "Lỗi từ máy chủ: Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(DanhSachMonHocActivity.this, "Mất mạng hoặc máy chủ tắt: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}