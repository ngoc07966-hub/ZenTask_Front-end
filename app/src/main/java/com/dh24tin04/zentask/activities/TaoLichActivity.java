package com.dh24tin04.zentask.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.models.CapNhatDeadlineData;
import com.dh24tin04.zentask.models.CapNhatDeadlineRequest;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.network.ApiResponse;
import com.dh24tin04.zentask.network.RetrofitClient;
import com.dh24tin04.zentask.util.ApiErrorParser;
import com.dh24tin04.zentask.util.Constants;
import com.dh24tin04.zentask.util.ThanhDieuHuong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Màn chọn hạn chót -> PATCH api/subject/{id} -> POST api/lichhoc/{id} -> LichHocActivity
public class TaoLichActivity extends AppCompatActivity {

    private static final long MOT_NGAY_MS = 24L * 60 * 60 * 1000;

    private int idSubject = -1;
    private String tenMon = "";
    private int soMuc = 0; // 0 = không rõ, ẩn phần "mục mỗi ngày"

    private TextView btnQuayLai, tvTenMonHoc, tvSoMuc, tvDeadline, tvSoNgay, tvMucMoiNgay,
            btnXepLich, tvLoading;
    private LinearLayout llChonNgay, llXemTruoc, llLoading;
    private TextView[] chips;

    // Hạn chót đang chọn (null = chưa chọn), luôn đặt về 00:00
    private Calendar deadline;
    private boolean dangXuLy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taolichactivity);

        if (!docDuLieuTuIntent()) {
            Toast.makeText(this, "Thiếu thông tin môn học để lên lịch", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        addview();
        addevent();
        ThanhDieuHuong.caiDat(this, ThanhDieuHuong.TAB_LICH_HOC);
        hienThiThongTinMon();

        // Chặn nút Back khi đang gọi API
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (dangXuLy) {
                    Toast.makeText(TaoLichActivity.this,
                            "Đang xếp lịch, vui lòng đợi...", Toast.LENGTH_SHORT).show();
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private boolean docDuLieuTuIntent() {
        Intent in = getIntent();
        idSubject = in.getIntExtra(Constants.EXTRA_ID_SUBJECT, -1);
        String ten = in.getStringExtra(Constants.EXTRA_TEN_SUBJECT);
        tenMon = ten == null ? "" : ten;
        soMuc = in.getIntExtra(Constants.EXTRA_SO_MUC, 0);
        return idSubject != -1;
    }

    private void addview() {
        btnQuayLai = findViewById(R.id.btn_quay_lai);
        tvTenMonHoc = findViewById(R.id.tv_ten_mon_hoc);
        tvSoMuc = findViewById(R.id.tv_so_muc);
        llChonNgay = findViewById(R.id.ll_chon_ngay);
        tvDeadline = findViewById(R.id.tv_deadline);
        chips = new TextView[]{
                findViewById(R.id.tv_chip_3ngay),
                findViewById(R.id.tv_chip_1tuan),
                findViewById(R.id.tv_chip_2tuan),
                findViewById(R.id.tv_chip_1thang)
        };
        llXemTruoc = findViewById(R.id.ll_xem_truoc);
        tvSoNgay = findViewById(R.id.tv_so_ngay);
        tvMucMoiNgay = findViewById(R.id.tv_muc_moi_ngay);
        btnXepLich = findViewById(R.id.btn_xep_lich);
        llLoading = findViewById(R.id.ll_loading);
        tvLoading = findViewById(R.id.tv_loading_text);
    }

    private void addevent() {
        btnQuayLai.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        llChonNgay.setOnClickListener(v -> {
            if (!dangXuLy) moLichChonNgay();
        });
        for (TextView chip : chips) {
            chip.setOnClickListener(v -> {
                if (dangXuLy) return;
                // tag trong XML = số ngày tính từ hôm nay
                int soNgay = Integer.parseInt(String.valueOf(v.getTag()));
                Calendar c = homNay();
                c.add(Calendar.DAY_OF_MONTH, soNgay);
                chonDeadline(c, (TextView) v);
            });
        }
        btnXepLich.setOnClickListener(v -> xepLich());
    }

    private void hienThiThongTinMon() {
        tvTenMonHoc.setText(tenMon.isEmpty() ? "Môn học" : tenMon);
        if (soMuc > 0) {
            tvSoMuc.setText(soMuc + " mục trong dàn ý");
        } else {
            tvSoMuc.setVisibility(View.GONE);
        }
    }

    // ===== Chọn ngày =====

    private void moLichChonNgay() {
        Calendar macDinh = deadline != null ? deadline : homNay();
        if (deadline == null) macDinh.add(Calendar.DAY_OF_MONTH, 7);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, nam, thang, ngay) -> {
                    Calendar c = homNay();
                    c.set(nam, thang, ngay);
                    chonDeadline(c, null);
                },
                macDinh.get(Calendar.YEAR),
                macDinh.get(Calendar.MONTH),
                macDinh.get(Calendar.DAY_OF_MONTH));

        // Backend yêu cầu deadline sau hôm nay -> sớm nhất là ngày mai
        Calendar ngayMai = homNay();
        ngayMai.add(Calendar.DAY_OF_MONTH, 1);
        dialog.getDatePicker().setMinDate(ngayMai.getTimeInMillis());
        dialog.show();
    }

    // chipDuocChon = null khi chọn từ DatePicker -> bỏ tô mọi chip
    private void chonDeadline(Calendar c, TextView chipDuocChon) {
        deadline = c;
        for (TextView chip : chips) {
            boolean chon = chip == chipDuocChon;
            chip.setBackgroundResource(chon ? R.drawable.nen_chip_dachon : R.drawable.nen_chip_chuachon);
            chip.setTextColor(Color.parseColor(chon ? "#FFFFFF" : "#9B8AAE"));
            chip.setTypeface(null, chon ? Typeface.BOLD : Typeface.NORMAL);
        }

        tvDeadline.setText(String.format(Locale.getDefault(), "%02d/%02d/%d",
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)));
        tvDeadline.setTextColor(Color.parseColor("#2B1B3D"));
        capNhatXemTruoc();
    }

    // Ước lượng giống backend: chia đều số mục cho số ngày còn lại
    private void capNhatXemTruoc() {
        int soNgay = (int) Math.max(1,
                Math.round((deadline.getTimeInMillis() - homNay().getTimeInMillis()) / (double) MOT_NGAY_MS));
        tvSoNgay.setText(String.valueOf(soNgay));
        if (soMuc > 0) {
            int mucMoiNgay = (int) Math.ceil(soMuc / (double) soNgay);
            tvMucMoiNgay.setText("~" + mucMoiNgay);
        } else {
            tvMucMoiNgay.setText("—");
        }
        llXemTruoc.setVisibility(View.VISIBLE);
    }

    private Calendar homNay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    private String layChuoiNgay(Calendar c) {
        return String.format(Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    // ===== Gọi API =====

    private String layToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        return "Bearer " + sharedPreferences.getString("TOKEN", "");
    }

    private void xepLich() {
        if (dangXuLy) return; // chặn bấm đúp
        if (deadline == null) {
            Toast.makeText(this, "Vui lòng chọn hạn chót trước", Toast.LENGTH_SHORT).show();
            return;
        }
        setDangXuLy(true, "Đang lưu hạn chót...");
        capNhatDeadline();
    }

    // Bước 1: lưu deadline cho Subject
    private void capNhatDeadline() {
        RetrofitClient.getApiService()
                .capNhatDeadline(layToken(), idSubject, new CapNhatDeadlineRequest(layChuoiNgay(deadline)))
                .enqueue(new Callback<ApiResponse<CapNhatDeadlineData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CapNhatDeadlineData>> call,
                                           Response<ApiResponse<CapNhatDeadlineData>> response) {
                        if (khongConSong()) return;
                        ApiResponse<CapNhatDeadlineData> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            taoLich();
                        } else {
                            setDangXuLy(false, null);
                            baoLoiServer(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CapNhatDeadlineData>> call, Throwable t) {
                        if (khongConSong()) return;
                        setDangXuLy(false, null);
                        baoLoiMang(t);
                    }
                });
    }

    // Bước 2: xếp lịch cho các mục chưa có ngày học
    private void taoLich() {
        tvLoading.setText("Đang xếp lịch học...");
        RetrofitClient.getApiService()
                .taoLich(layToken(), idSubject)
                .enqueue(new Callback<ApiResponse<List<DanY>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<DanY>>> call,
                                           Response<ApiResponse<List<DanY>>> response) {
                        if (khongConSong()) return;
                        setDangXuLy(false, null);
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
                        setDangXuLy(false, null);
                        baoLoiMang(t);
                    }
                });
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
        finish(); // không quay lại màn chọn deadline nữa
    }

    // Hiện lớp phủ loading + khóa nút khi đang gọi API
    private void setDangXuLy(boolean b, String thongBao) {
        dangXuLy = b;
        llLoading.setVisibility(b ? View.VISIBLE : View.GONE);
        if (b && thongBao != null) tvLoading.setText(thongBao);
        btnXepLich.setEnabled(!b);
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
}
