package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.dh24tin04.zentask.R;
import com.dh24tin04.zentask.network.ApiResponse;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.models.TaoSubjectData;
import com.dh24tin04.zentask.models.TaoSubjectRequest;
import com.dh24tin04.zentask.network.RetrofitClient;
import com.dh24tin04.zentask.util.ApiErrorParser;
import com.dh24tin04.zentask.util.Constants;
import com.dh24tin04.zentask.util.FileUtils;
import com.dh24tin04.zentask.util.UriRequestBody;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaoDanYActivity extends AppCompatActivity {

    private static final List<String> MIME_CHO_PHEP =
            Arrays.asList("application/pdf", "image/jpeg", "image/png");

    //  View
    private TextView[] chips;
    private TextInputEditText edtTenMonHoc;
    private LinearLayout llUpload, llLoading;
    private TextView tvTenFileDaChon, btnXuLyAi, tvLoading;

    // Trạng thái form
    private int loaiMau = 1;
    private Uri fileUri;
    private String fileName, fileMime;
    private long fileSize = -1;

    // Trạng thái xử lý
    private boolean dangXuLy = false;
    // Nhớ Subject đã tạo để bấm "thử lại" sau khi lỗi KHÔNG đẻ thêm Subject rỗng
    private Integer idSubjectDaTao = null;
    private String tenDaTao;
    private int loaiMauDaTao;

    private ActivityResultLauncher<String[]> chonFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Phải đăng ký launcher trước khi Activity vào trạng thái STARTED
        chonFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), this::xuLyFileDaChon);

        setContentView(R.layout.taodanyactivity);
        addview();
        addevent();
        chonLoaiMau(loaiMau);

        // Đang gọi API thì không cho thoát màn hình
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (dangXuLy) {
                    Toast.makeText(TaoDanYActivity.this,
                            "AI đang xử lý, vui lòng đợi...", Toast.LENGTH_SHORT).show();
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void addview() {
        chips = new TextView[]{
                findViewById(R.id.tv_chip_dean),
                findViewById(R.id.tv_chip_onthi),
                findViewById(R.id.tv_chip_baocao),
                findViewById(R.id.tv_chip_tudo)
        };
        edtTenMonHoc = findViewById(R.id.edt_ten_mon_hoc);
        llUpload = findViewById(R.id.ll_upload);
        tvTenFileDaChon = findViewById(R.id.tv_ten_file_da_chon);
        btnXuLyAi = findViewById(R.id.btn_xuly_ai);
        llLoading = findViewById(R.id.ll_loading);
        tvLoading = findViewById(R.id.tv_loading_text);
    }

    private void addevent() {
        for (int i = 0; i < chips.length; i++) {
            final int index = i;
            chips[i].setOnClickListener(v -> {
                if (!dangXuLy) chonLoaiMau(index);
            });
        }

        llUpload.setOnClickListener(v -> {
            if (!dangXuLy) chonFileLauncher.launch(MIME_CHO_PHEP.toArray(new String[0]));
        });

        btnXuLyAi.setOnClickListener(v -> batDauXuLy());

    }

    private void chonLoaiMau(int index) {
        loaiMau = index;
        for (int i = 0; i < chips.length; i++) {
            boolean chon = (i == index);
            chips[i].setBackgroundResource(chon ? R.drawable.nen_chip_dachon : R.drawable.nen_chip_chuachon);
            chips[i].setTextColor(Color.parseColor(chon ? "#FFFFFF" : "#9B8AAE"));
            chips[i].setTypeface(null, chon ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    private void xuLyFileDaChon(Uri uri) {
        if (uri == null) return; // người dùng bấm huỷ

        FileUtils.ThongTinFile info = FileUtils.doc(getContentResolver(), uri);

        if (info.mime == null || !MIME_CHO_PHEP.contains(info.mime)) {
            Toast.makeText(this, "Chỉ hỗ trợ file PDF, JPG hoặc PNG", Toast.LENGTH_LONG).show();
            return;
        }
        if (info.kichThuoc > Constants.MAX_FILE_BYTES) {
            Toast.makeText(this, "File vượt quá 20MB", Toast.LENGTH_LONG).show();
            return;
        }

        fileUri = uri;
        fileName = info.ten;
        fileMime = info.mime;
        fileSize = info.kichThuoc;

        String dungLuong = FileUtils.dinhDangKichThuoc(fileSize);
        tvTenFileDaChon.setText("📎 " + fileName + (dungLuong.isEmpty() ? "" : " (" + dungLuong + ")"));
        tvTenFileDaChon.setVisibility(View.VISIBLE);
    }

    private void batDauXuLy() {
        if (dangXuLy) return; // chặn bấm đúp

        String ten = edtTenMonHoc.getText() == null ? "" : edtTenMonHoc.getText().toString().trim();

        if (ten.isEmpty()) {
            edtTenMonHoc.setError("Vui lòng nhập tên môn học hoặc chủ đề");
            edtTenMonHoc.requestFocus();
            return;
        }
        if (ten.length() > Constants.MAX_TEN_SUBJECT) {
            edtTenMonHoc.setError("Tên tối đa " + Constants.MAX_TEN_SUBJECT + " ký tự");
            edtTenMonHoc.requestFocus();
            return;
        }
        if (fileUri == null) {
            Toast.makeText(this, "Vui lòng chọn file PDF hoặc ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        setDangXuLy(true, "Đang tạo môn học...");

        boolean dungLaiSubject = idSubjectDaTao != null
                && ten.equals(tenDaTao) && loaiMau == loaiMauDaTao;

        if (dungLaiSubject) {
            guiFileChoAI(idSubjectDaTao, ten);
        } else {
            taoSubject(ten);
        }
    }
    private String layToken() {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        return "Bearer " + sharedPreferences.getString("TOKEN", "");
    }
    private void taoSubject(String ten) {
        RetrofitClient.getApiService()
                .taoSubject(layToken(), new TaoSubjectRequest(ten, loaiMau))
                .enqueue(new Callback<ApiResponse<TaoSubjectData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<TaoSubjectData>> call,
                                           Response<ApiResponse<TaoSubjectData>> res) {
                        if (khongConSong()) return;

                        ApiResponse<TaoSubjectData> body = res.body();
                        if (res.isSuccessful() && body != null && body.getData() != null) {
                            idSubjectDaTao = body.getData().getIdSubject();
                            tenDaTao = ten;
                            loaiMauDaTao = loaiMau;
                            guiFileChoAI(idSubjectDaTao, ten);
                        } else {
                            baoLoiServer(res);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<TaoSubjectData>> call, Throwable t) {
                        if (khongConSong()) return;
                        baoLoiMang(t);
                    }
                });
    }

    private void guiFileChoAI(int idSubject, String ten) {
        setDangXuLy(true, "AI đang phân tích tài liệu...\nCó thể mất đến 1–2 phút, vui lòng không thoát màn hình");

        RequestBody fileBody = new UriRequestBody(
                getContentResolver(), fileUri, MediaType.parse(fileMime), fileSize);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, fileBody);
        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idSubject));

        RetrofitClient.getApiService()
                // truyền đủ 3 tham số: Token, ID môn học, File
                .processFile(layToken(), idBody, filePart)
                .enqueue(new Callback<ApiResponse<List<DanY>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<DanY>>> call,
                                           Response<ApiResponse<List<DanY>>> res) {
                        if (khongConSong()) return;

                        ApiResponse<List<DanY>> body = res.body();
                        if (!res.isSuccessful() || body == null) {
                            baoLoiServer(res);
                            return;
                        }

                        List<DanY> dsDanY = body.getData();
                        if (dsDanY == null || dsDanY.isEmpty()) {
                            setDangXuLy(false, null);
                            Toast.makeText(TaoDanYActivity.this,
                                    "AI không tách được nội dung. Hãy thử tài liệu rõ chữ hơn.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        moManHinhReview(idSubject, ten, dsDanY);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<DanY>>> call, Throwable t) {
                        if (khongConSong()) return;
                        baoLoiMang(t);
                    }
                });
    }
    // Sang màn xem/sửa dàn ý
    private void moManHinhReview(int idSubject, String ten, List<DanY> dsDanY) {
        Intent i = new Intent(this, DanYActivity.class);
        i.putExtra(Constants.EXTRA_ID_SUBJECT, idSubject);
        i.putExtra(Constants.EXTRA_TEN_SUBJECT, ten);
        i.putExtra(Constants.EXTRA_DAN_Y_JSON, new Gson().toJson(dsDanY));

        setDangXuLy(false, null);
        resetForm();          // quay lại màn này sẽ là form trống, không dính Subject cũ
        startActivity(i);
    }
    // Xử lý lỗi
    private void baoLoiServer(Response<?> res) {
        setDangXuLy(false, null);

        if (res.code() == 401) {
            // Token hết hạn / sai -> về đăng nhập
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, DangNhapActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }
        Toast.makeText(this, ApiErrorParser.layThongBao(res), Toast.LENGTH_LONG).show();
    }

    private void baoLoiMang(Throwable t) {
        setDangXuLy(false, null);
        String msg;
        if (t instanceof SocketTimeoutException) {
            msg = "Hết thời gian chờ. Server hoặc AI phản hồi quá lâu, hãy thử lại.";
        } else if (t instanceof IOException) {
            msg = "Không kết nối được server. Kiểm tra Wi-Fi và địa chỉ BASE_URL.";
        } else {
            msg = "Có lỗi xảy ra: " + t.getMessage();
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    // Tiện ích UI
    private void setDangXuLy(boolean b, String thongBao) {
        dangXuLy = b;
        llLoading.setVisibility(b ? View.VISIBLE : View.GONE);
        if (b && thongBao != null) tvLoading.setText(thongBao);
        btnXuLyAi.setEnabled(!b);
    }

    private void resetForm() {
        edtTenMonHoc.setText("");
        edtTenMonHoc.setError(null);
        fileUri = null;
        fileName = null;
        fileMime = null;
        fileSize = -1;
        tvTenFileDaChon.setText("");
        tvTenFileDaChon.setVisibility(View.GONE);
        idSubjectDaTao = null;
        tenDaTao = null;
        chonLoaiMau(1);
    }

    private boolean khongConSong() {
        return isFinishing() || isDestroyed();
    }
}