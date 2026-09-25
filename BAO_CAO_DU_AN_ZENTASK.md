# 📋 BÁO CÁO TIẾN ĐỘ DỰ ÁN ZENTASK

> Ứng dụng Android quản lý & lên lịch học tập hỗ trợ AI – `com.dh24tin04.zentask`
> Báo cáo được lập dựa trên mã nguồn hiện tại của nhánh `master`.

---

## 📊 1. Tổng quan tiến độ

| Chức năng / Màn hình | Giao diện (XML) | Xử lý (Java) | Kết nối API | Trạng thái |
| :-- | :-: | :-: | :-: | :-- |
| Chào mừng (`WelcomeActivity`) | ✅ | ✅ | – | ⚠️ Chưa khai báo trong Manifest |
| Đăng nhập (`DangNhapActivity`) | ✅ | ❌ | ❌ | 🟡 Mới có giao diện |
| Đăng ký (`DangKyActivity`) | ✅ | ❌ | ❌ | 🟡 Mới có giao diện |
| Trang chủ (`HomeActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành |
| Danh sách môn học (`DanhSachMonHocActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành (còn thiếu click mở môn) |
| Tạo dàn ý AI (`TaoDanYActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành |
| Xem dàn ý (`DanYActivity`) | ✅ | ✅ | 🟡 | 🟡 Nút "Lên lịch" đang giả lập |
| Lịch học (`LichHocActivity`) | ✅ | 🟡 | ❌ | 🟡 Mới có lưới lịch tháng |
| Tạo lịch (`TaoLichActivity`) | ❌ | ❌ | ❌ | ❌ Chưa làm |
| Tạo môn học / workspace (`TaoWorkspaceActivity`) | ❌ | ❌ | ❌ | ❌ Chưa làm |
| Hồ sơ cá nhân (`HoSoActivity`) | ❌ | ❌ | ❌ | ❌ Chưa làm |

**Chú thích:** ✅ Đã xong · 🟡 Làm một phần · ❌ Chưa làm · – Không áp dụng

---

## ✅ 2. Những phần ĐÃ HOÀN THÀNH

### 2.1. Nền tảng dự án
- Cấu hình Gradle (Version Catalog), Retrofit, Gson, Material Components.
- Tổ chức mã nguồn theo gói: `activities`, `adapters`, `models`, `network`, `util`.
- Quyền `INTERNET` trong `AndroidManifest.xml`.

### 2.2. Giao diện (res/)
- Hoàn thiện layout: chào mừng, đăng nhập, đăng ký, trang chủ, danh sách môn học, tạo dàn ý, xem dàn ý, lịch học.
- Thanh điều hướng dưới dùng chung (`thanhdieuhuong.xml`, nhúng bằng `<include>`).
- Layout item: `item_monhoc`, `item_nhatky`, `item_dan_y`, `item_lich`, `item_daily_task`.
- Bộ drawable (nền gradient, nút bo tròn, chip, badge, icon), color selector cho ô nhập liệu, theme sáng/tối.

### 2.3. Màn hình Chào mừng – `WelcomeActivity`
- Kiểm tra token trong `SharedPreferences` (`ZenTaskPrefs`).
- "Bắt đầu ngay": đã đăng nhập → Trang chủ, chưa đăng nhập → Đăng ký.
- "Đăng nhập": chuyển sang `DangNhapActivity`.

### 2.4. Trang chủ – `HomeActivity`
- Gọi API `GET api/home` kèm Bearer Token.
- Hiển thị streak 🔥, danh sách môn học cuộn ngang (`MonHocAdapter`), nhật ký gần đây cuộn dọc (`nhatKyAdapter`).
- Bấm vào môn học: có dàn ý → mở `DanYActivity`, chưa có → gợi ý sang `TaoDanYActivity`.
- "Xem tất cả" → `DanhSachMonHocActivity`; thanh điều hướng dưới hoạt động.
- Báo lỗi khi chưa đăng nhập, lỗi server hoặc mất mạng.

### 2.5. Danh sách môn học – `DanhSachMonHocActivity`
- Hiển thị toàn bộ môn học dạng lưới 2 cột, lấy dữ liệu từ API `api/home`.
- Nút quay lại.

### 2.6. Tạo dàn ý AI – `TaoDanYActivity`
- Chọn 4 loại mẫu (Đề án, Ôn thi, Báo cáo, Tự do) dạng chip.
- Nhập tên môn học (kiểm tra rỗng, tối đa 100 ký tự).
- Chọn tệp PDF/JPG/PNG, kiểm tra định dạng và giới hạn 20MB (`FileUtils`, `Constants`).
- Upload dạng stream để tiết kiệm bộ nhớ (`UriRequestBody`).
- Luồng 2 bước: `POST subject/create` → `POST input/process` (multipart).
- Khi thử lại sau lỗi thì dùng lại môn học đã tạo, không tạo thêm môn rỗng.
- Hiển thị trạng thái đang xử lý, chặn bấm đúp và chặn nút Back khi AI đang chạy.
- Xử lý lỗi: 401 → về đăng nhập, timeout, mất kết nối, đọc thông báo lỗi từ server (`ApiErrorParser`).

### 2.7. Xem dàn ý – `DanYActivity`
- Nhận danh sách dàn ý (JSON) qua Intent và hiển thị dạng cây (`OutlineTreeAdapter`): tính độ sâu theo `ParentId`, thụt lề 16dp mỗi cấp, in đậm mục gốc.
- Gắn nhãn **"Cần thêm tài liệu"** khi độ tin cậy AI < 0.6.
- Bấm vào mục → hộp thoại hiển thị nội dung chi tiết và độ tin cậy.

### 2.8. Lịch học – `LichHocActivity` (phần giao diện lịch)
- Tạo lưới lịch tháng 7 cột bắt đầu từ Thứ 2, có ngày đệm tháng trước/sau (`LichThangAdapter`).
- Làm nổi bật ngày đang chọn, hiển thị tiêu đề tháng và tiêu đề "Hôm nay"/"Ngày mai".

### 2.9. Tầng mạng – `network/`
- `RetrofitClient` (singleton), `ApiResponse<T>` dùng chung.
- Đã khai báo các endpoint:

| Method | Endpoint | Mô tả | Đã dùng trong app |
| :-- | :-- | :-- | :-: |
| `GET` | `api/home` | Dữ liệu trang chủ | ✅ |
| `POST` | `subject/create` | Tạo môn học | ✅ |
| `POST` | `input/process` | Upload tài liệu cho AI tạo dàn ý | ✅ |
| `POST` | `lichhoc/{idSubject}` | Tự động xếp lịch học | 🟡 (kết quả chưa được xử lý) |
| `GET` | `lichhoc/{ngay}` | Xem bài học theo ngày | ❌ |
| `PATCH` | `lichhoc/{idDanY}/hoanthanh` | Đánh dấu hoàn thành | ❌ |

---

## ⏳ 3. Những phần CHƯA HOÀN THÀNH

### 3.1. Xác thực người dùng (ưu tiên cao)
- [ ] `DangNhapActivity` mới chỉ gắn layout, chưa xử lý nhập liệu, chưa gọi API, chưa lưu token.
- [ ] `DangKyActivity` là lớp rỗng (chưa có `onCreate`, chưa gắn layout `dangky.xml`).
- [ ] API `auth/login`, `auth/register` còn đang comment trong `ApiService`; chưa có model `LoginRequest`/`LoginResponse`.
- [ ] Chưa có chức năng đăng xuất.
- ⚠️ Hiện tại app **không thể có token** qua giao diện, nên các màn hình gọi API đều báo "Chưa đăng nhập".

### 3.2. Các màn hình chưa làm
- [ ] `HoSoActivity` – lớp rỗng, `hoso.xml` chưa có nội dung.
- [ ] `TaoWorkspaceActivity` – lớp rỗng, chưa có layout.
- [ ] `TaoLichActivity` – lớp rỗng, chưa có layout.

### 3.3. Lịch học
- [ ] Chưa gọi `GET lichhoc/{ngay}` để lấy bài học theo ngày.
- [ ] `rv_viec_hom_nay`, `rv_viec_ngay_mai` chưa có dữ liệu; `tv_han_nop` chưa được gán.
- [ ] Chọn ngày mới chỉ hiện Toast, chưa tải danh sách công việc của ngày đó.
- [ ] Chưa chuyển tháng trước/tháng sau.
- [ ] Chưa đánh dấu hoàn thành bài học (`PATCH lichhoc/{idDanY}/hoanthanh`).

### 3.4. Xem dàn ý & tạo lịch
- [ ] Nút "Lên lịch" trong `DanYActivity` luôn chuyển sang màn Lịch học kể cả khi API lỗi (đang giả lập), chưa đọc kết quả trả về.
- [ ] Chưa cho phép chỉnh sửa / xóa / thêm mục dàn ý.
- [ ] Trang chủ lấy dàn ý của môn từ danh sách "nhật ký gần đây" nên có thể thiếu mục; cần API lấy toàn bộ dàn ý theo môn.
- [ ] `DanhSachMonHocActivity`: bấm vào môn mới chỉ hiện Toast; đang dùng lại API `api/home` thay vì API danh sách môn riêng.

### 3.5. Lớp tiện ích / adapter còn trống
- [ ] `TokenManager` – chưa có code (token đang đọc trực tiếp từ `SharedPreferences` ở nhiều nơi).
- [ ] `DateUtils`, `OutlineTreeBuilder` – lớp rỗng.
- [ ] `DailyTaskAdapter`, `ActivityFeedAdapter` – lớp rỗng (dù đã có `item_daily_task.xml`).
- [ ] `MonHocItem`, `NhatKyItem` chưa được sử dụng.
- [ ] `ZenTaskApplication` chưa khai báo trong Manifest.

### 3.6. Cấu hình & lỗi cần sửa
- [ ] **Manifest:** màn hình khởi động (LAUNCHER) đang là `LichHocActivity` → cần đổi về `WelcomeActivity`.
- [ ] **Manifest:** chưa khai báo `WelcomeActivity`, `HoSoActivity`, `TaoWorkspaceActivity`, `TaoLichActivity` → bấm tab "Thông tin" hoặc nút "Thêm môn học" sẽ **crash** (`ActivityNotFoundException`).
- [ ] Đường dẫn API chưa thống nhất: `api/home` có tiền tố `api/`, các endpoint khác thì không → cần đối chiếu với backend.
- [ ] Chưa cấu hình timeout cho OkHttp (AI xử lý 1–2 phút có thể vượt timeout mặc định 10 giây).
- [ ] Gọi HTTP (không HTTPS) tới `10.0.2.2` cần bật `usesCleartextTraffic` hoặc `network_security_config`.
- [ ] `BASE_URL` đang viết cứng, chỉ dùng được trên Emulator.

### 3.7. Kiểm thử
- [ ] Mới có test mẫu (`ExampleUnitTest`, `ExampleInstrumentedTest`), chưa có test cho chức năng.
- [ ] Chưa kiểm thử end-to-end với backend.

---

## 🎯 4. Đề xuất thứ tự thực hiện tiếp theo

1. Sửa `AndroidManifest.xml` (LAUNCHER, khai báo đủ Activity, cleartext) để app chạy không crash.
2. Hoàn thiện Đăng nhập / Đăng ký + `TokenManager`.
3. Hoàn thiện Lịch học: lấy công việc theo ngày (`DailyTaskAdapter`), đánh dấu hoàn thành, chuyển tháng.
4. Làm `TaoWorkspaceActivity`, `HoSoActivity`, `TaoLichActivity`.
5. Thống nhất endpoint với backend, cấu hình timeout, kiểm thử end-to-end.
