# 📋 BÁO CÁO TỔNG HỢP CÁC HẠNG MỤC ĐÃ THỰC HIỆN - DỰ ÁN ZENTASK

## 🚀 1. Tổng Quan Dự Án
- **Tên ứng dụng:** ZenTask - Ứng dụng Quản lý & Lên lịch Học tập Tự động Hỗ trợ AI
- **Package Name:** `com.dh24tin04.zentask`
- **Nền tảng & Ngôn ngữ:** Android (Java), Android SDK 36 (Min SDK 28)
- **Thư viện chính:** Retrofit 2.11.0, Gson 2.11.0, Material Components 1.14.0, ConstraintLayout 2.2.2, OkHttp3

---

## 🏗️ 2. Kiến Trúc & Cấu Trúc Dự Án

### 2.1. Cấu Trúc Gói (Package Structure)
```text
com.dh24tin04.zentask
├── activities/            # Quản lý giao diện & Luồng xử lý UI
│   ├── WelcomeActivity.java          # Màn hình chào mừng & kiểm tra phiên đăng nhập
│   ├── DangNhapActivity.java         # Màn hình đăng nhập tài khoản
│   ├── DangKyActivity.java           # Màn hình đăng ký tài khoản
│   ├── HomeActivity.java             # Màn hình trang chủ chính (Streak, Môn học, Nhật ký)
│   ├── DanhSachMonHocActivity.java   # Màn hình danh sách môn học dạng lưới (Grid)
│   ├── TaoDanYActivity.java          # Màn hình chọn file & gửi tài liệu cho AI phân tích
│   ├── DanYActivity.java             # Màn hình hiển thị dàn ý học tập dạng cây AI
│   ├── LichHocActivity.java          # Màn hình theo dõi lịch học theo tháng
│   ├── HoSoActivity.java             # Màn hình quản lý thông tin cá nhân
│   ├── TaoLichActivity.java          # Màn hình lên lịch học mới
│   └── TaoWorkspaceActivity.java     # Màn hình tạo không gian học tập mới
├── adapters/              # Bộ điều phối dữ liệu cho RecyclerView
│   ├── MonHocAdapter.java            # Adapter hiển thị danh sách môn học (Lưới / Ngang)
│   ├── nhatKyAdapter.java            # Adapter hiển thị nhật ký / hoạt động gần đây
│   ├── OutlineTreeAdapter.java       # Adapter phân cấp cây dàn ý AI (Tree structure)
│   ├── LichThangAdapter.java         # Adapter tạo ma trận lịch tháng 7xN
│   ├── DailyTaskAdapter.java         # Adapter danh sách công việc theo ngày
│   ├── ActivityFeedAdapter.java      # Adapter dòng thời gian hoạt động
│   └── OnItemClickListener.java     # Interface bắt sự kiện click item chung
├── models/                # Các lớp đối tượng dữ liệu (DTOs & Entities)
│   ├── User.java                     # Thông tin người dùng
│   ├── Subject.java                  # Thông tin môn học / workspace
│   ├── DanY.java                     # Thông tin từng mục dàn ý bài học AI
│   ├── HomeData.java                 # Cụm dữ liệu tổng hợp trang chủ (Streak, Subjects, RecentActivities)
│   ├── HomeResponse.java             # Response bọc ngoài dữ liệu trang chủ
│   ├── TaoSubjectRequest.java        # Body request tạo môn học
│   ├── TaoSubjectData.java           # Response chứa ID môn học vừa tạo
│   ├── LoaiNhatKy.java               # Enum/Phân loại nhật ký
│   ├── MonHocItem.java               # Dữ liệu hiển thị môn học
│   └── NhatKyItem.java               # Dữ liệu hiển thị nhật ký
├── network/               # Tầng kết nối RESTful API
│   ├── RetrofitClient.java           # Singleton khởi tạo Retrofit kết nối Backend Node.js
│   ├── ApiService.java               # Định nghĩa các Endpoints HTTP (GET, POST, PATCH, Multipart)
│   └── ApiResponse.java              # Generic Response Wrapper (`success`, `data`, `message`)
└── util/                  # Các lớp tiện ích hỗ trợ
    ├── Constants.java                # Quản lý hằng số hệ thống (Max file size, Intent Keys, ...)
    ├── FileUtils.java                # Đọc tên, dung lượng, MIME type từ Uri
    ├── UriRequestBody.java           # RequestBody tùy biến truyền Stream trực tiếp cho OkHttp
    ├── ApiErrorParser.java           # Bóc tách câu báo lỗi từ Error Response Body
    ├── DateUtils.java                # Tiện ích xử lý định dạng ngày tháng
    ├── TokenManager.java             # Quản lý SharedPreferences lưu Token
    └── OutlineTreeBuilder.java       # Xây dựng cấu trúc cây dàn ý
```

---

## 📱 3. Chi Tiết Màn Hình & Chức Năng Đã Hoàn Thành

### 1️⃣ Màn hình Chào mừng (`WelcomeActivity` & `welcomeactivity.xml`)
- Kiếm tra trạng thái Token đăng nhập lưu trong `SharedPreferences` (`ZenTaskPrefs`).
- Nút **"Bắt đầu ngay"**: Tự động chuyển thẳng tới `HomeActivity` nếu đã đăng nhập, ngược lại điều hướng sang `DangKyActivity`.
- Nút **"Đăng nhập"**: Điều hướng tới `DangNhapActivity`.

### 2️⃣ Màn hình Trang Chủ (`HomeActivity` & `homeactivity.xml`)
- **Hiển thị Streak:** Lấy dữ liệu số ngày học liên tục 🔥 từ Backend.
- **Danh sách Môn học (Cuộn ngang):** Dùng `RecyclerView` kết hợp `MonHocAdapter` hiển thị phần trăm tiến độ hoàn thành bài học.
- **Nhật ký hoạt động (Cuộn dọc):** Hiển thị danh sách các bài học / dàn ý vừa hoàn thành gần đây.
- **Tích hợp API (`GET /home`):** Tự động gửi Bearer Token đính kèm Header để tải dữ liệu trang chủ.

### 3️⃣ Màn hình Danh Sách Môn Học (`DanhSachMonHocActivity` & `danhsachmonhocactivity.xml`)
- Hiển thị toàn bộ môn học dưới dạng **Lưới 2 cột (`GridLayoutManager`)**.
- Nút Quay lại (`btn_back`) và Nút Tạo thêm môn học (`btn_them_monhoc`) điều hướng tới `TaoWorkspaceActivity`.

### 4️⃣ Màn hình Tạo Dàn Ý AI (`TaoDanYActivity` & `taodanyactivity.xml`)
- **Chọn Loại Mẫu:** 4 dạng mẫu học tập (Đề án, Ôn thi, Báo cáo, Tự do) thiết kế dạng Chips tương tác đổi màu sắc.
- **Chọn Tệp Tài Liệu:** Tích hợp `ActivityResultLauncher` mở tệp PDF, JPG, PNG với giới hạn dung lượng 20MB (`Constants.MAX_FILE_BYTES`).
- **Tùy biến Upload Stream (`UriRequestBody`):** Tối ưu hóa bộ nhớ RAM khi upload tệp lớn trực tiếp qua `ContentResolver`.
- **Gọi API 2 Bước:**
  1. Gọi `POST /subject/create` để tạo mới môn học và nhận về `idSubject`.
  2. Gọi `POST /input/process` (Multipart Upload) truyền `idSubject` và `file` tài liệu cho AI xử lý tách dàn ý.
- **Quản lý Trạng Thái:** Hiển thị mượt mà hiệu ứng Đang xử lý, khóa nút thao tác, tự động chặn Back khi AI đang phân tích.

### 5️⃣ Màn hình Xem Dàn Ý Cây AI (`DanYActivity` & `danyactivity.xml`)
- Nhận dữ liệu JSON danh sách dàn ý phân cấp qua `Intent`.
- **Hiển thị Cây Học Tập (`OutlineTreeAdapter`):**
  - Tự động tính toán độ sâu của từng mục (`doSau`) dựa trên quan hệ `ParentId`.
  - Tự động thụt lề theo cấp độ (16dp mỗi cấp).
  - Phân biệt định dạng tiêu đề mục gốc (In đậm) và mục con.
  - Tự động gắn nhãn cảnh báo **"Cần thêm tài liệu"** nếu độ tin cậy AI (`DoTinCay`) < 0.6.

### 6️⃣ Màn hình Lịch Học (`LichHocActivity` & `lichhocactivity.xml`)
- **Thuật toán Tạo Lưới Tháng (`LichThangAdapter`):** Tự động tính toán số ngày đệm tháng trước/tháng sau theo chuẩn ISO-8601 (Thứ 2 đến Chủ nhật).
- Cho phép chọn ngày linh hoạt và đổi giao diện làm nổi bật ngày được chọn.

---

## 🌐 4. Tầng Mạng & Tích Hợp API (`network/`)

### Cấu hình RetrofitClient
- **Base URL:** `http://10.0.2.2:3000/` (Địa chỉ IP Loopback tới Localhost Backend Node.js từ Android Emulator).
- **Converter:** `GsonConverterFactory`.

### Danh sách API Endpoints (`ApiService.java`):
| HTTP Method | Endpoint Path | Mô tả Chức năng |
| :--- | :--- | :--- |
| `GET` | `home` | Lấy dữ liệu tổng quan trang chủ (Streak, Danh sách môn học, Nhật ký gần đây) |
| `POST` | `subject/create` | Khởi tạo một môn học / chủ đề mới |
| `POST` | `input/process` | Upload tệp tài liệu (Multipart) + ID môn học để AI phân tích và tạo dàn ý |
| `POST` | `lichhoc/{idSubject}` | Lên lịch học tự động cho môn học |
| `GET` | `lichhoc/{ngay}` | Xem danh sách bài học cần hoàn thành theo ngày |
| `PATCH` | `lichhoc/{idDanY}/hoanthanh` | Cập nhật trạng thái đánh dấu hoàn thành một mục dàn ý |

---

## 🎨 5. Thiết Kế Giao Diện & Tài Nguyên (`res/`)

- **Giao diện Hiện đại:** Thiết kế theo phong cách hiện đại với dải màu Gradient (`bg_gradient.xml`), các nút bấm Bo tròn chuẩn Material Design (`btn_primary.xml`).
- **Thanh Điều Hướng Chung (`thanhdieuhuong.xml`):** Tích hợp dưới dạng `<include>` để tái sử dụng thống nhất trên tất cả các màn hình chính.
- **Biểu tượng & Badges:** Tích hợp các huy hiệu "AI GENERATED", nhãn trạng thái tiến độ bài học.

---

## ✅ 6. Kết Luận
Tất cả các thành phần giao diện, luồng dữ liệu, thuật toán xử lý cây dàn ý AI, ma trận lịch tháng và tầng kết nối RESTful API của dự án **ZenTask** đã được xây dựng hoàn chỉnh, chạy thành công và sẵn sàng phục vụ kiểm thử end-to-end với Backend server.
