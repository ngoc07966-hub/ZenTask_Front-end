# 📋 BÁO CÁO TIẾN ĐỘ DỰ ÁN ZENTASK

> Ứng dụng Android quản lý & lên lịch học tập hỗ trợ AI – `com.dh24tin04.zentask`
> Cập nhật ngày **25/09/2026**, dựa trên mã nguồn hiện tại của nhánh `master` (sau commit `5b9a5f5`, kèm các thay đổi làm màn Lịch học).

---

## 📊 1. Tổng quan tiến độ

| Chức năng / Màn hình | Giao diện (XML) | Xử lý (Java) | Kết nối API | Trạng thái |
| :-- | :-: | :-: | :-: | :-- |
| Chào mừng (`WelcomeActivity`) | ✅ | ✅ | – | ✅ Hoàn thành (là màn LAUNCHER) |
| Đăng nhập (`DangNhapActivity`) | ✅ | ❌ | ❌ | 🟡 Mới có giao diện |
| Đăng ký (`DangKyActivity`) | ✅ | ❌ | ❌ | 🟡 Mới có giao diện |
| Trang chủ (`HomeActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành |
| Danh sách môn học (`DanhSachMonHocActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành (còn thiếu click mở môn) |
| Tạo dàn ý AI (`TaoDanYActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành |
| Xem dàn ý (`DanYActivity`) | ✅ | ✅ | ✅ | ✅ Hoàn thành |
| Thanh điều hướng dùng chung (`ThanhDieuHuong`) | ✅ | ✅ | – | ✅ Hoàn thành |
| Lịch học (`LichHocActivity`) 🆕 | ✅ | ✅ | ✅ | ✅ Hoàn thành (chưa kiểm thử với backend thật) |
| Tạo lịch / đặt Deadline (`TaoLichActivity`) 🆕 | ✅ | ✅ | ✅ | ✅ Hoàn thành (chưa kiểm thử với backend thật) |
| Hồ sơ cá nhân (`HoSoActivity`) | ❌ | ❌ | ❌ | ❌ Chưa làm |
| ~~Tạo workspace (`TaoWorkspaceActivity`)~~ | – | – | – | ⛔ Đã xóa file, thay bằng `TaoDanYActivity` |

**Chú thích:** ✅ Đã xong · 🟡 Làm một phần · ❌ Chưa làm · – Không áp dụng · ⛔ Bỏ

> ⚠️ **Hiện project chưa build được**: xem mục 3.1.

---

## ✅ 2. Những phần ĐÃ HOÀN THÀNH

### 2.1. Nền tảng dự án
- Cấu hình Gradle (Version Catalog), Retrofit, Gson, Material Components.
- Tổ chức mã nguồn theo gói: `activities`, `adapters`, `models`, `network`, `util`.
- Quyền `INTERNET` trong `AndroidManifest.xml`.

### 2.2. Giao diện (res/)
- Hoàn thiện layout: chào mừng, đăng nhập, đăng ký, trang chủ, danh sách môn học, tạo dàn ý, xem dàn ý, lịch học, tạo lịch (`taolichactivity.xml`) 🆕.
- Thanh điều hướng dưới (`thanhdieuhuong.xml`, nhúng bằng `<include>`).
- Layout item: `item_monhoc`, `item_nhatky`, `item_dan_y`, `item_lich`, `item_daily_task`.
- Bộ drawable (nền gradient, nút bo tròn, chip, badge, icon), color selector cho ô nhập liệu, theme sáng/tối.

### 2.3. Thanh điều hướng dùng chung – `util/ThanhDieuHuong` 🆕
- Gom 3 hàm `caiDatThanhDieuHuong()` bị lặp ở Home, DanY, LichHoc thành **1 lớp tiện ích**.
- Mỗi màn hình chỉ cần 1 dòng: `ThanhDieuHuong.caiDat(this, ThanhDieuHuong.TAB_...)`.
- Tô màu đúng tab đang mở (trước đây XML luôn tô tab "Tổng quan").
- Hành vi chuyển màn thống nhất: bấm tab hiện tại thì không làm gì, bấm "Tổng quan" thì quay về Trang chủ có sẵn, các màn khác tự đóng khi chuyển để không chồng màn hình.
- Đã áp dụng cho 6 màn: Trang chủ, Nạp liệu (`TaoDanYActivity`), Danh sách môn, Dàn ý, Lịch học, Tạo lịch.

### 2.4. Màn hình Chào mừng – `WelcomeActivity`
- Kiểm tra token trong `SharedPreferences` (`ZenTaskPrefs`).
- "Bắt đầu ngay": đã đăng nhập → Trang chủ, chưa đăng nhập → Đăng ký.
- "Đăng nhập": chuyển sang `DangNhapActivity`.

### 2.5. Trang chủ – `HomeActivity`
- Gọi API `GET api/home` kèm Bearer Token.
- Hiển thị streak 🔥, danh sách môn học cuộn ngang (`MonHocAdapter`) kèm thanh % tiến độ, nhật ký gần đây cuộn dọc (`nhatKyAdapter`).
- **Tự tải lại khi quay về Trang chủ** 🆕 (`onRestart`) → thanh % và streak luôn cập nhật sau khi tick hoàn thành ở Lịch học.
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
- Nút **"Lên lịch học tự động"** 🆕 mở `TaoLichActivity`, truyền kèm `idSubject`, tên môn và số mục dàn ý. Phần gọi API xếp lịch đã chuyển sang màn Tạo lịch vì backend cần Deadline trước.

### 2.9. Tạo lịch / đặt Deadline – `TaoLichActivity` 🆕
- **Giao diện (`taolichactivity.xml`)**, cùng phong cách với màn Tạo dàn ý:
  - Nút quay lại, thẻ thông tin môn (tên môn, số mục).
  - Ô chọn hạn chót mở `DatePickerDialog`, sớm nhất là **ngày mai**.
  - 4 chip chọn nhanh: 3 ngày, 1 tuần, 2 tuần, 1 tháng (số ngày lưu trong `android:tag`).
  - Khung xem trước: số ngày học và khoảng số mục mỗi ngày (cùng công thức chia đều với backend).
  - Ghi chú cách xếp lịch, nút "Xếp Lịch Học", lớp phủ loading, thanh điều hướng.
- **Luồng 2 bước:** `PATCH api/subject/{idSubject}` (lưu deadline) → `POST api/lichhoc/{idSubject}` (xếp lịch) → mở `LichHocActivity` và đóng màn hiện tại.
- Chữ trên lớp phủ loading đổi theo từng bước ("Đang lưu hạn chót..." → "Đang xếp lịch học..."). Chặn bấm đúp và chặn nút Back khi đang gọi API.
- Báo riêng trường hợp mọi mục đã được xếp lịch từ trước (API trả danh sách rỗng).
- Xử lý lỗi: 401 → về đăng nhập, 400 → hiện thông báo của backend, phân biệt quá thời gian chờ và mất kết nối. Lỗi thì **ở lại màn hình** để thử lại.
- Model mới: `CapNhatDeadlineRequest`, `CapNhatDeadlineData`. Hằng mới `Constants.EXTRA_SO_MUC`.

### 2.10. Backend – API cập nhật Deadline 🆕
- `PATCH api/subject/:idSubject`, body `{ "deadline": "YYYY-MM-DD" }` → trả `{ idSubject, deadline }`.
- Kiểm tra: đúng định dạng, là ngày có thật (chặn kiểu 30/02), phải **sau hôm nay**, và môn học phải thuộc người dùng đang đăng nhập.
- File đã sửa: `routes/subject.routes.js`, `controllers/subject.controller.js`, `services/subject.service.js`.

### 2.11. Lịch học – `LichHocActivity` 🆕
- **Lịch tháng:** lưới 7 cột bắt đầu từ Thứ 2, có ngày đệm tháng trước/sau (`LichThangAdapter`). Nút ‹ › để chuyển tháng. Bấm vào ngày đệm thì nhảy sang tháng đó.
- **Danh sách bài học theo ngày:** chọn 1 ngày → gọi `GET api/lichhoc/{ngay}` cho ngày đó và ngày kế tiếp. Tiêu đề tự đổi "Hôm nay" / "Ngày mai" / "Hôm qua" / "Thứ X - dd/MM".
  - Bỏ qua kết quả cũ nếu người dùng đã chọn ngày khác trong lúc chờ API.
  - Hiện "Không có bài học nào 🎉" khi ngày không có bài.
- **`DailyTaskAdapter`:** hiển thị tên mục + tên môn. Mục đã xong thì gạch ngang, làm mờ, khóa checkbox (backend không có API bỏ hoàn thành). Dùng `onClick` thay vì `onCheckedChanged` để việc bind lại view không gọi API nhầm.
- **Tick hoàn thành:** gọi `PATCH api/lichhoc/{idDanY}/hoanthanh`, khóa checkbox trong lúc chờ.
  - Thành công → cập nhật thanh tiến độ ngày, badge 🔥, Toast dạng `✅ Toán rời rạc: 45% (9/20 mục) · 🔥 4 ngày`.
  - Lỗi → bỏ tick; 401 về đăng nhập; lỗi khác tải lại danh sách cho khớp server.
- **Thẻ tiến độ ngày:** thanh % = số mục đã xong / tổng số mục của ngày đang chọn, tính ngay trên app.
- **Badge streak** và **dòng hạn chót** lấy từ `api/home`: ưu tiên môn vừa xếp lịch (`idSubject` từ màn Tạo lịch), không thì lấy deadline sắp tới gần nhất; ẩn khi không có.
- Nút ⋯ → hộp thoại chi tiết mục (môn, ngày học, trạng thái, nội dung).

### 2.12. Streak & tiến độ (backend) 🆕
- **Quy tắc streak** (`tinhStreakMoi`, tính khi tick hoàn thành):

| Tình huống | Kết quả |
| :-- | :-- |
| Tick mục đầu tiên sau khi đăng ký | 1 |
| Hôm qua có học, hôm nay tick | +1 |
| Tick thêm mục trong cùng ngày | Giữ nguyên (mỗi ngày tính 1 lần) |
| Bỏ lỡ ≥ 1 ngày rồi tick lại | Về 1 |

- **Sửa lỗi hiển thị streak:** thêm `layStreakHienTai` → `api/home` trả **0** nếu lần học cuối trước hôm qua (trước đây vẫn hiện số cũ). Không ghi xuống DB.
- **API hoàn thành trả thêm dữ liệu:** `{ danY, tienDoMon: { idSubject, tenMon, soMucHoanThanh, tongSoMuc, phanTram }, streak }` để app cập nhật % và streak ngay, không phải gọi lại `api/home`.
- File đã sửa: `services/lichhoc.service.js`, `services/home.service.js`.

### 2.13. Tầng mạng & model
- `RetrofitClient` (singleton), `ApiResponse<T>` dùng chung.
- **`ApiService` đã được đối chiếu với backend** 🆕: thêm tiền tố `api/` cho mọi endpoint, sửa `subject/create` → `api/subject`, thay `Call<Object>` bằng kiểu dữ liệu cụ thể, chia nhóm theo file `routes/*.js`.
- **Model `LichHoc`** cho API xem lịch theo ngày: các trường của `DanY` + class lồng `MonHoc` chứa tên môn, getter `getTenMon()` có kiểm tra null. Thêm `NoiDung` và `setTrangThaiHoanThanh()` 🆕.
- **Model `HoanThanhData`** 🆕 (kèm class lồng `TienDoMon`) cho kết quả API hoàn thành.
- **Model `Subject`** 🆕: đọc được cả key `"DeadLine"` mà Sequelize trả về (`alternate`).

| Method | Endpoint | Mô tả | Kiểu trả về | Đã dùng trong app |
| :-- | :-- | :-- | :-- | :-: |
| `POST` | `api/auth/register` | Đăng ký | `ApiResponse<User>` | ❌ (thiếu model, xem 3.1) |
| `POST` | `api/auth/login` | Đăng nhập | `ApiResponse<LoginData>` | ❌ (thiếu model, xem 3.1) |
| `GET` | `api/home` | Dữ liệu trang chủ | `HomeResponse` | ✅ |
| `POST` | `api/subject` | Tạo môn học | `ApiResponse<TaoSubjectData>` | ✅ |
| `POST` | `api/input/process` | Upload tài liệu cho AI tạo dàn ý | `ApiResponse<List<DanY>>` | ✅ |
| `PATCH` | `api/subject/{idSubject}` 🆕 | Cập nhật hạn chót (Deadline) | `ApiResponse<CapNhatDeadlineData>` | ✅ |
| `POST` | `api/lichhoc/{idSubject}` | Tự động xếp lịch học | `ApiResponse<List<DanY>>` | ✅ |
| `GET` | `api/lichhoc/{ngay}` | Xem bài học theo ngày | `ApiResponse<List<LichHoc>>` | ✅ 🆕 |
| `PATCH` | `api/lichhoc/{idDanY}/hoanthanh` | Đánh dấu hoàn thành, trả tiến độ môn + streak | `ApiResponse<HoanThanhData>` | ✅ 🆕 |

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
- [x] ~~`TaoWorkspaceActivity` không còn dùng~~ → đã xóa file.

### 3.3. Tạo lịch – phần còn lại
- [x] ~~Backend chưa có API cập nhật Deadline~~ → đã thêm `PATCH api/subject/:idSubject` và làm xong `TaoLichActivity` (xem 2.9, 2.10).
- [ ] Chưa kiểm thử end-to-end với backend thật (cần khởi động lại server Node để nhận route mới).
- [ ] Nếu môn đã có deadline, màn Tạo lịch chưa hiện sẵn ngày cũ (chưa truyền deadline qua Intent).
- [ ] Đổi deadline **không xếp lại** các mục đã có lịch (backend chỉ xếp mục có `NgayLenLich = null`). Muốn "xếp lại toàn bộ" cần thêm API riêng.
- [x] ~~Model `Subject` đọc sai key deadline~~ → đã thêm `alternate = {"DeadLine"}`.

### 3.4. Lịch học – phần còn lại
- [x] ~~Lấy bài học theo ngày, `DailyTaskAdapter`, chuyển tháng, đánh dấu hoàn thành, hạn chót, dùng `idSubject`~~ → đã xong (xem 2.11, 2.12).
- [ ] Chưa kiểm thử end-to-end với backend thật (cần khởi động lại server Node).
- [ ] Không bỏ được trạng thái hoàn thành (backend chưa có API "bỏ tick").
- [ ] Lịch tháng chưa đánh dấu chấm cho ngày có bài học (cần API mới, VD `GET api/lichhoc/thang/{YYYY-MM}`).
- [ ] Nút ⋯ mới chỉ xem chi tiết, chưa có dời ngày / xóa mục.
- [ ] Đề xuất nâng cấp streak: thêm cột `ChuoiDaiNhat` (kỷ lục), cho phép 1 ngày "đóng băng" mỗi tuần.

### 3.5. Dàn ý & danh sách môn học
- [ ] Chưa cho phép chỉnh sửa / xóa / thêm mục dàn ý.
- [ ] Trang chủ lấy dàn ý của môn từ danh sách "nhật ký gần đây" nên có thể thiếu mục. Cần API lấy toàn bộ dàn ý theo môn.
- [ ] `DanhSachMonHocActivity`: bấm vào môn mới chỉ hiện Toast. Đang dùng lại API `api/home` thay vì API danh sách môn riêng.
- [ ] Giới hạn tên môn trong app là 100 ký tự (`Constants.MAX_TEN_SUBJECT`) nhưng backend chỉ nhận **tối đa 30** → nên đổi về 30.
- [ ] `TaoDanYActivity` chặn nút Back khi AI đang xử lý, nhưng vẫn có thể rời màn bằng thanh điều hướng.

### 3.6. Lớp tiện ích / adapter còn trống
- [ ] `TokenManager`: chưa có code (token đang đọc trực tiếp từ `SharedPreferences` ở nhiều nơi).
- [ ] `DateUtils`, `OutlineTreeBuilder`: lớp rỗng.
- [ ] `ActivityFeedAdapter`: lớp rỗng.
- [ ] `MonHocItem`, `NhatKyItem` chưa được sử dụng.
- [ ] `ZenTaskApplication` chưa khai báo trong Manifest.

### 3.7. Cấu hình
- [x] ~~Manifest: LAUNCHER sai~~ → đã là `WelcomeActivity`. Đã khai báo thêm `TaoLichActivity`, `LichHocActivity` 🆕.
- [ ] **Manifest:** chưa khai báo `HoSoActivity` → bấm tab "Thông tin" sẽ **crash** (`ActivityNotFoundException`).
- [ ] Chưa cấu hình timeout cho OkHttp (AI xử lý 1–2 phút có thể vượt timeout mặc định 10 giây).
- [ ] Gọi HTTP (không HTTPS) tới `10.0.2.2` cần bật `usesCleartextTraffic` hoặc `network_security_config`.
- [ ] `BASE_URL` đang viết cứng, chỉ dùng được trên Emulator.

### 3.8. Kiểm thử
- [ ] Mới có test mẫu (`ExampleUnitTest`, `ExampleInstrumentedTest`), chưa có test cho chức năng.
- [ ] Chưa kiểm thử end-to-end với backend.

---

## 🎯 4. Đề xuất thứ tự thực hiện tiếp theo

1. Sửa lỗi build ở `ApiService` (tạo model Auth hoặc tạm comment).
2. Sửa `AndroidManifest.xml` (khai báo `HoSoActivity`, cleartext) và thêm timeout OkHttp.
3. Hoàn thiện Đăng nhập / Đăng ký + `TokenManager`.
4. Kiểm thử luồng chính với backend: tạo dàn ý → chọn deadline → xếp lịch → tick hoàn thành → kiểm tra % và streak ở Trang chủ.
5. Làm `HoSoActivity` (và khai báo trong Manifest).
6. Bổ sung API bỏ hoàn thành / lịch theo tháng nếu còn thời gian.

---

## 📝 5. Nhật ký thay đổi

| Ngày | Nội dung |
| :-- | :-- |
| 25/09/2026 | Hoàn thiện màn **Lịch học**: `DailyTaskAdapter`, tải bài học theo ngày, chuyển tháng, tick hoàn thành, thẻ tiến độ ngày, badge streak, hạn chót. Backend: API hoàn thành trả thêm tiến độ môn + streak, streak hiển thị về 0 khi bỏ lỡ ngày. Trang chủ tự tải lại khi quay về. Thêm model `HoanThanhData`, sửa key deadline của `Subject`. |
| 25/09/2026 | Làm màn **Tạo lịch** (`TaoLichActivity` + `taolichactivity.xml`): chọn Deadline, xem trước khối lượng học, gọi 2 API nối tiếp. Backend thêm `PATCH api/subject/:idSubject`. Nút "Lên lịch" ở `DanYActivity` chuyển sang mở màn Tạo lịch. Khai báo `TaoLichActivity`, `LichHocActivity` trong Manifest. |
| 25/09/2026 | Kết nối API thật cho nút "Lên lịch" (`DanYActivity`). Đối chiếu và sửa toàn bộ endpoint trong `ApiService`. Thêm model `LichHoc`. Gom thanh điều hướng vào `ThanhDieuHuong`. Nút "Thêm môn học" chuyển sang `TaoDanYActivity`. |
