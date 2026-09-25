package com.dh24tin04.zentask.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.LichThangAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.dh24tin04.zentask.network.ApiResponse;
import com.dh24tin04.zentask.util.ApiErrorParser;
import com.dh24tin04.zentask.util.ThanhDieuHuong;
import java.io.IOException;
import java.net.SocketTimeoutException;

public class LichHocActivity extends AppCompatActivity {

    private TextView tvThangNam, tvHanNop, tvTieuDeHomNay, tvTieuDeNgayMai;
    private RecyclerView rvHangNgay, rvViecHomNay, rvViecNgayMai;
    private LichThangAdapter lichThangAdapter;
    private LocalDate ngayDangChon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lichhocactivity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lichhoc), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addview();
        caiDatLichThang();
        ThanhDieuHuong.caiDat(this, ThanhDieuHuong.TAB_LICH_HOC);
    }

    private void addview() {
        tvThangNam = findViewById(R.id.tv_thang_nam);
        tvHanNop = findViewById(R.id.tv_han_nop);
        tvTieuDeHomNay = findViewById(R.id.tv_tieu_de_hom_nay);
        tvTieuDeNgayMai = findViewById(R.id.tv_tieu_de_ngay_mai);

        rvHangNgay = findViewById(R.id.rv_hang_ngay);
        rvViecHomNay = findViewById(R.id.rv_viec_hom_nay);
        rvViecNgayMai = findViewById(R.id.rv_viec_ngay_mai);
    }

    private void caiDatLichThang() {
        ngayDangChon = LocalDate.now();

        // 1. Cập nhật tiêu đề Tháng, Năm
        capNhatTieuDeThang(ngayDangChon);

        // 2. Tạo danh sách các ngày trong lưới tháng (bao gồm ngày đệm)
        List<LichThangAdapter.NgayLich> dsNgay = LichThangAdapter.taoLuoiThang(
                ngayDangChon.getYear(),
                ngayDangChon.getMonthValue(),
                ngayDangChon
        );

        // 3. Khởi tạo Adapter
        lichThangAdapter = new LichThangAdapter(dsNgay, ngay -> {
            Toast.makeText(this, "Bạn chọn ngày: " + ngay.soNgay + "/" + ngay.thang + "/" + ngay.nam, Toast.LENGTH_SHORT).show();
        });

        // 4. Cài đặt LayoutManager Lưới 7 cột cho RecyclerView lịch
        rvHangNgay.setLayoutManager(new GridLayoutManager(this, 7));
        rvHangNgay.setAdapter(lichThangAdapter);

        // 5. Cập nhật tiêu đề ngày hôm nay và ngày mai
        DateTimeFormatter formatterFormat = DateTimeFormatter.ofPattern("dd/MM", Locale.getDefault());
        tvTieuDeHomNay.setText("Hôm nay - " + ngayDangChon.format(formatterFormat));
        tvTieuDeNgayMai.setText("Ngày mai - " + ngayDangChon.plusDays(1).format(formatterFormat));
    }

    private void capNhatTieuDeThang(LocalDate date) {
        tvThangNam.setText("Tháng " + date.getMonthValue() + ", " + date.getYear());
    }

}
