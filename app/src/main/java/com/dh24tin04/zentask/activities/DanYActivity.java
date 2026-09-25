package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.OutlineTreeAdapter;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.network.ApiResponse;
import com.dh24tin04.zentask.network.RetrofitClient;
import com.dh24tin04.zentask.util.ApiErrorParser;
import com.dh24tin04.zentask.util.Constants;
import com.dh24tin04.zentask.util.ThanhDieuHuong;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
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

    // Trạng thái nút "Lên lịch"
    private boolean dangXepLich = false;
    private CharSequence chuNutLenLich;

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
        chuNutLenLich = btnLenLich.getText();
    }

    private String layToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        return "Bearer " + sharedPreferences.getString("TOKEN", "");
    }

    private void addevent() {
        btnLenLich.setOnClickListener(v -> taoLichHocTuDong());
    }

    private void taoLichHocTuDong() {
        if (idSubject == -1 || dangXepLich) return; // chặn bấm đúp

        setDangXepLich(true);

        RetrofitClient.getApiService()
                .taoLich(layToken(), idSubject)
                .enqueue(new Callback<ApiResponse<List<DanY>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<DanY>>> call,
                                           Response<ApiResponse<List<DanY>>> response) {
                        if (khongConSong()) return;
                        setDangXepLich(false);

                        ApiResponse<List<DanY>> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            xepLichThanhCong(body.getData());
                        } else {
                            baoLoiServer(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<DanY>>> call, Throwable t) {
                        if (khongConSong()) return;
                        setDangXepLich(false);
                        baoLoiMang(t);
                    }
                });
    }

    // Khóa nút + đổi chữ khi đang gọi API, trả lại như cũ khi xong
    private void setDangXepLich(boolean b) {
        dangXepLich = b;
        btnLenLich.setEnabled(!b);
        btnLenLich.setText(b ? "Đang xếp lịch..." : chuNutLenLich);
    }

    private void xepLichThanhCong(List<DanY> dsDaXep) {
        // Backend trả mảng rỗng khi mọi mục đã có lịch từ trước
        String thongBao = (dsDaXep == null || dsDaXep.isEmpty())
                ? "Các mục của môn này đã được xếp lịch trước đó"
                : "Đã xếp lịch cho " + dsDaXep.size() + " mục học!";
        Toast.makeText(this, thongBao, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, LichHocActivity.class);
        i.putExtra(Constants.EXTRA_ID_SUBJECT, idSubject);
        startActivity(i);
    }

    private void baoLoiServer(Response<?> response) {
        if (response.code() == 401) {
            // Token hết hạn / sai -> về đăng nhập, xóa hết màn hình cũ
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, DangNhapActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }
        // Ví dụ: "Subject chưa có hạn chót, vui lòng đặt Deadline trước khi lên lịch"
        Toast.makeText(this, ApiErrorParser.layThongBao(response), Toast.LENGTH_LONG).show();
    }

    private void baoLoiMang(Throwable t) {
        String msg;
        if (t instanceof SocketTimeoutException) {
            msg = "Hết thời gian chờ, server phản hồi quá lâu. Hãy thử lại.";
        } else if (t instanceof IOException) {
            msg = "Không kết nối được server. Kiểm tra mạng và địa chỉ BASE_URL.";
        } else {
            msg = "Có lỗi xảy ra: " + t.getMessage();
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private boolean khongConSong() {
        return isFinishing() || isDestroyed();
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