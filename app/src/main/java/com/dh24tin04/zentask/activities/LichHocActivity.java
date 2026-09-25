package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.adapters.DailyTaskAdapter;
import com.dh24tin04.zentask.adapters.LichThangAdapter;
import com.dh24tin04.zentask.models.HoanThanhData;
import com.dh24tin04.zentask.models.HomeData;
import com.dh24tin04.zentask.models.HomeResponse;
import com.dh24tin04.zentask.models.LichHoc;
import com.dh24tin04.zentask.models.Subject;
import com.dh24tin04.zentask.network.ApiResponse;
import com.dh24tin04.zentask.network.RetrofitClient;
import com.dh24tin04.zentask.util.ApiErrorParser;
import com.dh24tin04.zentask.util.Constants;
import com.dh24tin04.zentask.util.ThanhDieuHuong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LichHocActivity extends AppCompatActivity {

    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM", Locale.getDefault());

    private TextView tvStreak, tvThangNam, btnThangTruoc, btnThangSau, tvHanNop,
            tvTieuDeTienDo, tvPhanTramNgay, tvSoMucXong,
            tvTieuDeHomNay, tvTieuDeNgayMai, tvTrongHomNay, tvTrongNgayMai;
    private LinearLayout llHanNop;
    private ProgressBar pbTienDoNgay;
    private RecyclerView rvHangNgay, rvViecHomNay, rvViecNgayMai;

    private LichThangAdapter lichThangAdapter;
    // Danh sách 1 = ngày đang chọn, danh sách 2 = ngày kế tiếp
    private DailyTaskAdapter adapterNgayChon, adapterNgayKe;

    private LocalDate ngayDangChon;
    private YearMonth thangDangXem;
    private int idSubject = -1; // môn vừa xếp lịch (nếu mở từ màn Tạo lịch)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lichhocactivity);

        idSubject = getIntent().getIntExtra(Constants.EXTRA_ID_SUBJECT, -1);
        ngayDangChon = LocalDate.now();
        thangDangXem = YearMonth.from(ngayDangChon);

        addview();
        caiDatLichThang();
        caiDatDanhSachViec();
        addevent();
        ThanhDieuHuong.caiDat(this, ThanhDieuHuong.TAB_LICH_HOC);

        capNhatTieuDeNgay();
        taiViecCuaNgayDangChon();
        taiStreakVaHanChot();
    }

    private void addview() {
        tvStreak = findViewById(R.id.tv_streak);
        tvThangNam = findViewById(R.id.tv_thang_nam);
        btnThangTruoc = findViewById(R.id.btn_thang_truoc);
        btnThangSau = findViewById(R.id.btn_thang_sau);
        llHanNop = findViewById(R.id.ll_han_nop);
        tvHanNop = findViewById(R.id.tv_han_nop);

        tvTieuDeTienDo = findViewById(R.id.tv_tieu_de_tien_do);
        tvPhanTramNgay = findViewById(R.id.tv_phan_tram_ngay);
        pbTienDoNgay = findViewById(R.id.pb_tien_do_ngay);
        tvSoMucXong = findViewById(R.id.tv_so_muc_xong);

        tvTieuDeHomNay = findViewById(R.id.tv_tieu_de_hom_nay);
        tvTieuDeNgayMai = findViewById(R.id.tv_tieu_de_ngay_mai);
        tvTrongHomNay = findViewById(R.id.tv_trong_hom_nay);
        tvTrongNgayMai = findViewById(R.id.tv_trong_ngay_mai);

        rvHangNgay = findViewById(R.id.rv_hang_ngay);
        rvViecHomNay = findViewById(R.id.rv_viec_hom_nay);
        rvViecNgayMai = findViewById(R.id.rv_viec_ngay_mai);
    }

    private void addevent() {
        btnThangTruoc.setOnClickListener(v -> {
            thangDangXem = thangDangXem.minusMonths(1);
            veLaiLuoiThang();
        });
        btnThangSau.setOnClickListener(v -> {
            thangDangXem = thangDangXem.plusMonths(1);
            veLaiLuoiThang();
        });
    }

    // ===== Lịch tháng =====

    private void caiDatLichThang() {
        lichThangAdapter = new LichThangAdapter(taoLuoiThangDangXem(), this::chonNgay);
        rvHangNgay.setLayoutManager(new GridLayoutManager(this, 7));
        rvHangNgay.setAdapter(lichThangAdapter);
        tvThangNam.setText("Tháng " + thangDangXem.getMonthValue() + ", " + thangDangXem.getYear());
    }

    private List<LichThangAdapter.NgayLich> taoLuoiThangDangXem() {
        return LichThangAdapter.taoLuoiThang(
                thangDangXem.getYear(), thangDangXem.getMonthValue(), ngayDangChon);
    }

    private void veLaiLuoiThang() {
        tvThangNam.setText("Tháng " + thangDangXem.getMonthValue() + ", " + thangDangXem.getYear());
        lichThangAdapter.capNhatDuLieu(taoLuoiThangDangXem());
    }

    private void chonNgay(LichThangAdapter.NgayLich ngay) {
        ngayDangChon = LocalDate.of(ngay.nam, ngay.thang, ngay.soNgay);

        // Bấm vào ngày đệm của tháng trước/sau -> nhảy sang tháng đó
        if (!ngay.thuocThangHienTai) {
            thangDangXem = YearMonth.from(ngayDangChon);
            veLaiLuoiThang();
        }

        capNhatTieuDeNgay();
        taiViecCuaNgayDangChon();
    }

    // "Hôm nay - 25/09", "Ngày mai - 26/09", "Thứ 5 - 02/10"...
    private String tenNgay(LocalDate ngay) {
        LocalDate homNay = LocalDate.now();
        String ten;
        if (ngay.equals(homNay)) {
            ten = "Hôm nay";
        } else if (ngay.equals(homNay.plusDays(1))) {
            ten = "Ngày mai";
        } else if (ngay.equals(homNay.minusDays(1))) {
            ten = "Hôm qua";
        } else {
            int thu = ngay.getDayOfWeek().getValue(); // 1 = Thứ 2 ... 7 = Chủ nhật
            ten = thu == 7 ? "Chủ nhật" : "Thứ " + (thu + 1);
        }
        return ten + " - " + ngay.format(DINH_DANG_NGAY);
    }

    private void capNhatTieuDeNgay() {
        tvTieuDeHomNay.setText(tenNgay(ngayDangChon));
        tvTieuDeNgayMai.setText(tenNgay(ngayDangChon.plusDays(1)));
        tvTieuDeTienDo.setText(ngayDangChon.equals(LocalDate.now())
                ? "Tiến độ hôm nay"
                : "Tiến độ ngày " + ngayDangChon.format(DINH_DANG_NGAY));
    }

    // ===== Danh sách bài học =====

    private void caiDatDanhSachViec() {
        adapterNgayChon = new DailyTaskAdapter(taoListener(true));
        adapterNgayKe = new DailyTaskAdapter(taoListener(false));

        rvViecHomNay.setLayoutManager(new LinearLayoutManager(this));
        rvViecHomNay.setAdapter(adapterNgayChon);
        rvViecNgayMai.setLayoutManager(new LinearLayoutManager(this));
        rvViecNgayMai.setAdapter(adapterNgayKe);
    }

    private DailyTaskAdapter.OnTaskListener taoListener(boolean laNgayChon) {
        return new DailyTaskAdapter.OnTaskListener() {
            @Override
            public void onHoanThanh(LichHoc item) {
                danhDauHoanThanh(item, laNgayChon ? adapterNgayChon : adapterNgayKe);
            }

            @Override
            public void onXemChiTiet(LichHoc item) {
                hienThiChiTiet(item);
            }
        };
    }

    private void taiViecCuaNgayDangChon() {
        // Xóa dữ liệu cũ ngay để không hiện nhầm bài của ngày trước trong lúc chờ API
        adapterNgayChon.capNhatDuLieu(null);
        adapterNgayKe.capNhatDuLieu(null);
        capNhatTienDoNgay();

        taiViecTheoNgay(ngayDangChon, true);
        taiViecTheoNgay(ngayDangChon.plusDays(1), false);
    }

    private void taiViecTheoNgay(LocalDate ngay, boolean laNgayChon) {
        DailyTaskAdapter adapter = laNgayChon ? adapterNgayChon : adapterNgayKe;
        TextView tvTrong = laNgayChon ? tvTrongHomNay : tvTrongNgayMai;

        // LocalDate.toString() cho đúng dạng "YYYY-MM-DD" backend cần
        RetrofitClient.getApiService()
                .xemLich(layToken(), ngay.toString())
                .enqueue(new Callback<ApiResponse<List<LichHoc>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<LichHoc>>> call,
                                           Response<ApiResponse<List<LichHoc>>> response) {
                        // Người dùng đã chọn ngày khác trong lúc chờ -> bỏ kết quả cũ
                        if (khongConSong() || !laNgayConHienThi(ngay, laNgayChon)) return;

                        ApiResponse<List<LichHoc>> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            adapter.capNhatDuLieu(body.getData());
                        } else {
                            adapter.capNhatDuLieu(null);
                            // Chỉ báo lỗi 1 lần (ở danh sách chính) để không hiện 2 Toast giống nhau
                            if (laNgayChon) baoLoiServer(response);
                        }
                        tvTrong.setVisibility(adapter.getSoMuc() == 0 ? View.VISIBLE : View.GONE);
                        if (laNgayChon) capNhatTienDoNgay();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<LichHoc>>> call, Throwable t) {
                        if (khongConSong() || !laNgayConHienThi(ngay, laNgayChon)) return;
                        adapter.capNhatDuLieu(null);
                        tvTrong.setVisibility(View.VISIBLE);
                        if (laNgayChon) {
                            capNhatTienDoNgay();
                            baoLoiMang(t);
                        }
                    }
                });
    }

    private boolean laNgayConHienThi(LocalDate ngay, boolean laNgayChon) {
        LocalDate mongDoi = laNgayChon ? ngayDangChon : ngayDangChon.plusDays(1);
        return ngay.equals(mongDoi);
    }

    // Thanh tiến độ = số mục đã xong / tổng số mục của ngày đang chọn
    private void capNhatTienDoNgay() {
        int tong = adapterNgayChon.getSoMuc();
        int xong = adapterNgayChon.getSoMucHoanThanh();
        int phanTram = tong > 0 ? Math.round(xong * 100f / tong) : 0;

        pbTienDoNgay.setProgress(phanTram, true);
        tvPhanTramNgay.setText(phanTram + "%");
        if (tong == 0) {
            tvSoMucXong.setText("Chưa có bài học cho ngày này");
        } else if (xong == tong) {
            tvSoMucXong.setText("Đã hoàn thành cả " + tong + " mục 🎉");
        } else {
            tvSoMucXong.setText(xong + "/" + tong + " mục đã hoàn thành");
        }
    }

    // ===== Đánh dấu hoàn thành =====

    private void danhDauHoanThanh(LichHoc item, DailyTaskAdapter adapter) {
        adapter.batDauGui(item); // khóa checkbox trong lúc chờ

        RetrofitClient.getApiService()
                .danhDauHoanThanh(layToken(), item.getIdDanY())
                .enqueue(new Callback<ApiResponse<HoanThanhData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<HoanThanhData>> call,
                                           Response<ApiResponse<HoanThanhData>> response) {
                        if (khongConSong()) return;
                        ApiResponse<HoanThanhData> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            adapter.ketThucGui(item, true);
                            if (adapter == adapterNgayChon) capNhatTienDoNgay();
                            hoanThanhThanhCong(body.getData());
                        } else {
                            adapter.ketThucGui(item, false);
                            baoLoiServer(response);
                            // VD "đã được đánh dấu hoàn thành trước đó" -> tải lại cho khớp server
                            if (response.code() != 401) taiViecCuaNgayDangChon();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<HoanThanhData>> call, Throwable t) {
                        if (khongConSong()) return;
                        adapter.ketThucGui(item, false);
                        baoLoiMang(t);
                    }
                });
    }

    private void hoanThanhThanhCong(HoanThanhData data) {
        if (data == null) return;
        hienThiStreak(data.getStreak());

        HoanThanhData.TienDoMon tienDo = data.getTienDoMon();
        String thongBao = "✅ Đã hoàn thành! 🔥 " + data.getStreak() + " ngày";
        if (tienDo != null) {
            thongBao = "✅ " + tienDo.getTenMon() + ": " + tienDo.getPhanTram() + "% ("
                    + tienDo.getSoMucHoanThanh() + "/" + tienDo.getTongSoMuc() + " mục)"
                    + " · 🔥 " + data.getStreak() + " ngày";
        }
        Toast.makeText(this, thongBao, Toast.LENGTH_SHORT).show();
    }

    private void hienThiChiTiet(LichHoc item) {
        String noiDung = item.getNoiDung();
        if (noiDung == null || noiDung.trim().isEmpty()) {
            noiDung = "Chưa có nội dung tóm tắt cho mục này.";
        }
        String thongTin = "📌 Môn học: " + item.getTenMon() + "\n"
                + "📅 Ngày học: " + dinhDangNgay(item.getNgayLenLich()) + "\n"
                + "✔️ Trạng thái: " + (item.isTrangThaiHoanThanh() ? "Đã hoàn thành" : "Chưa hoàn thành")
                + "\n\n📝 Nội dung:\n" + noiDung;

        new AlertDialog.Builder(this)
                .setTitle(item.getTieuDe())
                .setMessage(thongTin)
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ===== Streak + hạn chót (lấy từ api/home) =====

    private void taiStreakVaHanChot() {
        RetrofitClient.getApiService()
                .getHomeData(layToken())
                .enqueue(new Callback<HomeResponse>() {
                    @Override
                    public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                        if (khongConSong()) return;
                        HomeResponse body = response.body();
                        if (!response.isSuccessful() || body == null || !body.isSuccess()
                                || body.getData() == null) return; // không quan trọng, lỗi thì bỏ qua
                        HomeData data = body.getData();
                        hienThiStreak(data.getStreak());
                        hienThiHanChot(data.getSubjects());
                    }

                    @Override
                    public void onFailure(Call<HomeResponse> call, Throwable t) {
                        // Lỗi mạng đã được báo ở phần tải danh sách bài học
                    }
                });
    }

    private void hienThiStreak(int streak) {
        tvStreak.setText("🔥 " + streak + " ngày");
    }

    // Ưu tiên môn vừa xếp lịch, nếu không thì lấy deadline sắp tới gần nhất
    private void hienThiHanChot(List<Subject> dsMon) {
        if (dsMon == null) return;
        LocalDate homNay = LocalDate.now();
        Subject monChon = null;
        LocalDate hanChon = null;

        for (Subject mon : dsMon) {
            LocalDate han = docNgay(mon.getDeadline());
            if (han == null || han.isBefore(homNay)) continue;
            if (mon.getIdSubject() == idSubject) {
                monChon = mon;
                hanChon = han;
                break;
            }
            if (hanChon == null || han.isBefore(hanChon)) {
                monChon = mon;
                hanChon = han;
            }
        }

        if (monChon == null) {
            llHanNop.setVisibility(View.GONE);
            return;
        }
        tvHanNop.setText("Hạn chót " + monChon.getTen() + " - " + hanChon.format(DINH_DANG_NGAY));
        llHanNop.setVisibility(View.VISIBLE);
    }

    // Backend trả "YYYY-MM-DD" (có thể kèm giờ) -> chỉ lấy 10 ký tự đầu
    private LocalDate docNgay(String s) {
        if (s == null || s.length() < 10) return null;
        try {
            return LocalDate.parse(s.substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }

    private String dinhDangNgay(String s) {
        LocalDate ngay = docNgay(s);
        return ngay == null ? "Chưa xếp" : ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // ===== Tiện ích chung =====

    private String layToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        return "Bearer " + sharedPreferences.getString("TOKEN", "");
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
