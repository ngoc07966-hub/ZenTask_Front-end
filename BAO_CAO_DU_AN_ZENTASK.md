# 📋 BÁO CÁO TIẾN ĐỘ DỰ ÁN ZENTASK

> Ứng dụng Android quản lý & lên lịch học tập hỗ trợ AI – `com.dh24tin04.zentask`
> Cập nhật ngày **25/09/2026**, dựa trên mã nguồn hiện tại của nhánh `master` (commit `cab2a26`).

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
| Xem dàn ý (`DanYActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành (xếp lịch cần Deadline – xem 3.3) |
| Thanh điều hướng dùng chung (`ThanhDieuHuong`) | ✅ | ✅ | – | ✅ Hoàn thành |
| Lịch học (`LichHocActivity`) | ✅ | 🟡 | ❌ | 🟡 Mới có lưới lịch tháng |
| Tạo lịch / đặt Deadline (`TaoLichActivity`) | ❌ | ❌ | ❌ | ❌ Chưa làm (chờ API backend) |
| Hồ sơ cá nhân (`HoSoActivity`) | ❌ | ❌ | ❌ | ❌ Chưa làm |
| ~~Tạo workspace (`TaoWorkspaceActivity`)~~ | – | – | – | ⛔ Không dùng nữa, thay bằng `TaoDanYActivity` |

**Chú thích:** ✅ Đã xong · 🟡 Làm một phần · ❌ Chưa làm · – Không áp dụng · ⛔ Bỏ

> ⚠️ **Hiện project chưa build được**: xem mục 3.1.

---

## ✅ 2. Những phần ĐÃ HOÀN THÀNH

### 2.1. Nền tảng dự án
- Cấu hình Gradle (Version Catalog), Retrofit, Gson, Material Components.
- Tổ chức mã nguồn theo gói: `activities`, `adapters`, `models`, `network`, `util`.
- Quyền `INTERNET` trong `AndroidManifest.xml`.

### 2.2. Giao diện (res/)
- Hoàn thiện layout: chào mừng, đăng nhập, đăng ký, trang chủ, danh sách môn học, tạo dàn ý, xem dàn ý, lịch học.
- Thanh điều hướng dưới (`thanhdieuhuong.xml`, nhúng bằng `<include>`).
- Layout item: `item_monhoc`, `item_nhatky`, `item_dan_y`, `item_lich`, `item_daily_task`.
- Bộ drawable (nền gradient, nút bo tròn, chip, badge, icon), color selector cho ô nhập liệu, theme sáng/tối.

### 2.3. Thanh điều hướng dùng chung – `util/ThanhDieuHuong` 🆕
- Gom 3 hàm `caiDatThanhDieuHuong()` bị lặp ở Home, DanY, LichHoc thành **1 lớp tiện ích**.
- Mỗi màn hình chỉ cần 1 dòng: `ThanhDieuHuong.caiDat(this, ThanhDieuHuong.TAB_...)`.
- Tô màu đúng tab đang mở (trước đây XML luôn tô tab "Tổng quan").
- Hành vi chuyển màn thống nhất: bấm tab hiện tại thì không làm gì, bấm "Tổng quan" thì quay về Trang chủ có sẵn, các màn khác tự đóng khi chuyển để không chồng màn hình.
- Đã áp dụng cho 5 màn: Trang chủ, Nạp liệu (`TaoDanYActivity`), Danh sách môn, Dàn ý, Lịch học.

### 2.4. Màn hình Chào mừng – `WelcomeActivity`
- Kiểm tra token trong `SharedPreferences` (`ZenTaskPrefs`).
- "Bắt đầu ngay": đã đăng nhập → Trang chủ, chưa đăng nhập → Đăng ký.
- "Đăng nhập": chuyển sang `DangNhapActivity`.

### 2.5. Trang chủ – `HomeActivity`
- Gọi API `GET api/home` kèm Bearer Token.
- Hiển thị streak 🔥, danh sách môn học cuộn ngang (`MonHocAdapter`), nhật ký gần đây cuộn dọc (`nhatKyAdapter`).
- Bấm vào môn học: có dàn ý → mở `DanYActivity`, chưa có → gợi ý sang `TaoDanYActivity`.
- "Xem tất cả" → `DanhSachMonHocActivity`.
- Báo lỗi khi chưa đăng nhập, lỗi server hoặc mất mạng.

### 2.6. Danh sách môn học – `DanhSachMonHocActivity`
- Hiển thị toàn bộ môn học dạng lưới 2 cột, lấy dữ liệu từ API `api/home`.
- Nút quay lại.
- Nút **"Thêm môn học"** mở `TaoDanYActivity` 🆕 (trước đây mở `TaoWorkspaceActivity` rỗng nên bị crash).

### 2.7. Tạo dàn ý AI – `TaoDanYActivity`
- Chọn 4 loại mẫu (Đề án, Ôn thi, Báo cáo, Tự do) dạng chip.
- Nhập tên môn học (kiểm tra rỗng và độ dài tối đa).
- Chọn tệp PDF/JPG/PNG, kiểm tra định dạng và giới hạn 20MB (`FileUtils`, `Constants`).
- Upload dạng stream để tiết kiệm bộ nhớ (`UriRequestBody`).
- Luồng 2 bước: `POST api/subject` → `POST api/input/process` (multipart).
- Khi thử lại sau lỗi thì dùng lại môn học đã tạo, không tạo thêm môn rỗng.
- Hiển thị trạng thái đang xử lý, chặn bấm đúp và chặn nút Back khi AI đang chạy.
- Xử lý lỗi: 401 → về đăng nhập, timeout, mất kết nối, đọc thông báo lỗi từ server (`ApiErrorParser`).

### 2.8. Xem dàn ý – `DanYActivity`
- Nhận danh sách dàn ý (JSON) qua Intent và hiển thị dạng cây (`OutlineTreeAdapter`): tính độ sâu theo `ParentId`, thụt lề 16dp mỗi cấp, in đậm mục gốc.
- Gắn nhãn **"Cần thêm tài liệu"** khi độ tin cậy AI < 0.6.
- Bấm vào mục → hộp thoại hiển thị nội dung chi tiết và độ tin cậy.
- **Nút "Lên lịch" đã kết nối API thật** 🆕 (`POST api/lichhoc/{idSubject}`), bỏ phần giả lập:
  - Khóa nút và đổi chữ "Đang xếp lịch..." khi đang gọi, chặn bấm đúp.
  - Thành công → báo số mục đã xếp, mở `LichHocActivity` kèm `idSubject`.
  - Lỗi 401 → về màn Đăng nhập. Lỗi khác → hiện thông báo của backend.
  - Lỗi mạng → phân biệt quá thời gian chờ và mất kết nối. Lỗi thì **ở lại màn hình**, không tự chuyển trang.

### 2.9. Lịch học – `LichHocActivity` (phần giao diện lịch)
- Tạo lưới lịch tháng 7 cột bắt đầu từ Thứ 2, có ngày đệm tháng trước/sau (`LichThangAdapter`).
- Làm nổi bật ngày đang chọn, hiển thị tiêu đề tháng và tiêu đề "Hôm nay"/"Ngày mai".

### 2.10. Tầng mạng & model
- `RetrofitClient` (singleton), `ApiResponse<T>` dùng chung.
- **`ApiService` đã được đối chiếu với backend** 🆕: thêm tiền tố `api/` cho mọi endpoint, sửa `subject/create` → `api/subject`, thay `Call<Object>` bằng kiểu dữ liệu cụ thể, chia nhóm theo file `routes/*.js`.
- **Model `LichHoc`** 🆕 cho API xem lịch theo ngày: các trường của `DanY` + class lồng `MonHoc` chứa tên môn, getter `getTenMon()` có kiểm tra null.

| Method | Endpoint | Mô tả | Kiểu trả về | Đã dùng trong app |
| :-- | :-- | :-- | :-- | :-: |
| `POST` | `api/auth/register` | Đăng ký | `ApiResponse<User>` | ❌ (thiếu model, xem 3.1) |
| `POST` | `api/auth/login` | Đăng nhập | `ApiResponse<LoginData>` | ❌ (thiếu model, xem 3.1) |
| `GET` | `api/home` | Dữ liệu trang chủ | `HomeResponse` | ✅ |
| `POST` | `api/subject` | Tạo môn học | `ApiResponse<TaoSubjectData>` | ✅ |
| `POST` | `api/input/process` | Upload tài liệu cho AI tạo dàn ý | `ApiResponse<List<DanY>>` | ✅ |
| `POST` | `api/lichhoc/{idSubject}` | Tự động xếp lịch học | `ApiResponse<List<DanY>>` | ✅ |
| `GET` | `api/lichhoc/{ngay}` | Xem bài học theo ngày | `ApiResponse<List<LichHoc>>` | ❌ |
| `PATCH` | `api/lichhoc/{idDanY}/hoanthanh` | Đánh dấu hoàn thành | `ApiResponse<DanY>` | ❌ |

---

## ⏳ 3. Những phần CHƯA HOÀN THÀNH

### 3.1. Xác thực người dùng (ưu tiên cao nhất)
- [ ] 🔴 **Lỗi build:** `ApiService` đã khai báo `register`, `login` nhưng **chưa có** class `RegisterRequest`, `LoginRequest`, `LoginData` và chưa `import User` → project không biên dịch được. Tạo đủ model hoặc tạm comment 2 hàm này.
- [ ] `DangNhapActivity` mới chỉ gắn layout, chưa xử lý nhập liệu, chưa gọi API, chưa lưu token.
- [ ] `DangKyActivity` là lớp rỗng (chưa có `onCreate`, chưa gắn layout `dangky.xml`).
- [ ] Chưa có chức năng đăng xuất.
- ⚠️ Hiện tại app **không thể có token** qua giao diện, nên các màn hình gọi API đều báo "Chưa đăng nhập".

### 3.2. Các màn hình chưa làm
- [ ] `HoSoActivity`: lớp rỗng, `hoso.xml` chưa có nội dung.
- [ ] `TaoLichActivity`: lớp rỗng. Dự kiến dùng để **chọn Deadline** trước khi xếp lịch (xem 3.3).
- [ ] `TaoWorkspaceActivity`: không còn nơi nào sử dụng, có thể xóa file.

### 3.3. Xếp lịch cần Deadline (phụ thuộc backend)
- [ ] Backend chỉ xếp lịch khi môn học có `DeadLine`, nhưng lúc tạo môn **không gửi deadline** và backend **chưa có API cập nhật deadline**. Bấm "Lên lịch" hiện luôn nhận lỗi 400 *"Subject chưa có hạn chót"*.
- [ ] Cần backend bổ sung, ví dụ `PATCH api/subject/:idSubject` với body `{ deadline: "YYYY-MM-DD" }`, rồi làm `TaoLichActivity` (DatePicker → cập nhật deadline → gọi xếp lịch).
- Tạm thời: có thể đặt cột `Deadline` trực tiếp trong database để kiểm thử.

### 3.4. Lịch học
- [ ] Chưa gọi `GET api/lichhoc/{ngay}` để lấy bài học theo ngày (đã có model `LichHoc`).
- [ ] `DailyTaskAdapter` còn rỗng. `rv_viec_hom_nay`, `rv_viec_ngay_mai` chưa có dữ liệu. `tv_han_nop` chưa được gán.
- [ ] Chọn ngày mới chỉ hiện Toast, chưa tải danh sách công việc của ngày đó.
- [ ] Chưa chuyển tháng trước/tháng sau.
- [ ] Chưa đánh dấu hoàn thành bài học (`PATCH api/lichhoc/{idDanY}/hoanthanh`).
- [ ] Chưa dùng `idSubject` được `DanYActivity` gửi sang.

### 3.5. Dàn ý & danh sách môn học
- [ ] Chưa cho phép chỉnh sửa / xóa / thêm mục dàn ý.
- [ ] Trang chủ lấy dàn ý của môn từ danh sách "nhật ký gần đây" nên có thể thiếu mục. Cần API lấy toàn bộ dàn ý theo môn.
- [ ] `DanhSachMonHocActivity`: bấm vào môn mới chỉ hiện Toast. Đang dùng lại API `api/home` thay vì API danh sách môn riêng.
- [ ] Giới hạn tên môn trong app là 100 ký tự (`Constants.MAX_TEN_SUBJECT`) nhưng backend chỉ nhận **tối đa 30** → nên đổi về 30.
- [ ] `TaoDanYActivity` chặn nút Back khi AI đang xử lý, nhưng vẫn có thể rời màn bằng thanh điều hướng.

### 3.6. Lớp tiện ích / adapter còn trống
- [ ] `TokenManager`: chưa có code (token đang đọc trực tiếp từ `SharedPreferences` ở nhiều nơi).
- [ ] `DateUtils`, `OutlineTreeBuilder`: lớp rỗng.
- [ ] `DailyTaskAdapter`, `ActivityFeedAdapter`: lớp rỗng.
- [ ] `MonHocItem`, `NhatKyItem` chưa được sử dụng.
- [ ] `ZenTaskApplication` chưa khai báo trong Manifest.

### 3.7. Cấu hình
- [ ] **Manifest:** màn hình khởi động (LAUNCHER) đang là `LichHocActivity` → cần đổi về `WelcomeActivity`.
- [ ] **Manifest:** chưa khai báo `WelcomeActivity`, `HoSoActivity`, `TaoLichActivity` → bấm tab "Thông tin" sẽ **crash** (`ActivityNotFoundException`).
- [ ] Chưa cấu hình timeout cho OkHttp (AI xử lý 1–2 phút có thể vượt timeout mặc định 10 giây).
- [ ] Gọi HTTP (không HTTPS) tới `10.0.2.2` cần bật `usesCleartextTraffic` hoặc `network_security_config`.
- [ ] `BASE_URL` đang viết cứng, chỉ dùng được trên Emulator.

### 3.8. Kiểm thử
- [ ] Mới có test mẫu (`ExampleUnitTest`, `ExampleInstrumentedTest`), chưa có test cho chức năng.
- [ ] Chưa kiểm thử end-to-end với backend.

---

## 🎯 4. Đề xuất thứ tự thực hiện tiếp theo

1. Sửa lỗi build ở `ApiService` (tạo model Auth hoặc tạm comment).
2. Sửa `AndroidManifest.xml` (LAUNCHER, khai báo đủ Activity, cleartext) và thêm timeout OkHttp.
3. Hoàn thiện Đăng nhập / Đăng ký + `TokenManager`.
4. Phối hợp backend thêm API cập nhật Deadline → làm `TaoLichActivity`.
5. Hoàn thiện Lịch học: lấy công việc theo ngày (`DailyTaskAdapter` + `LichHoc`), đánh dấu hoàn thành, chuyển tháng.
6. Làm `HoSoActivity`, xóa `TaoWorkspaceActivity`, kiểm thử end-to-end.

---

## 📝 5. Nhật ký thay đổi

| Ngày | Nội dung |
| :-- | :-- |
| 25/09/2026 | Kết nối API thật cho nút "Lên lịch" (`DanYActivity`). Đối chiếu và sửa toàn bộ endpoint trong `ApiService`. Thêm model `LichHoc`. Gom thanh điều hướng vào `ThanhDieuHuong`. Nút "Thêm môn học" chuyển sang `TaoDanYActivity`. |
