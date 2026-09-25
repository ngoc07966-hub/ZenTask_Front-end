# ZenTask – Ứng dụng Quản lý & Lên lịch Học tập Hỗ trợ AI

ZenTask là ứng dụng Android giúp người học tải tài liệu (PDF/ảnh) lên, để AI tự động tách thành **dàn ý học tập dạng cây**, sau đó **xếp lịch học** và theo dõi tiến độ, chuỗi ngày học (streak).

- **Package:** `com.dh24tin04.zentask`
- **Ngôn ngữ:** Java 11 – Android SDK 36 (Min SDK 28)
- **Thư viện:** Retrofit 2.11.0, Gson 2.11.0, Material Components 1.14.0, ConstraintLayout 2.2.2, AppCompat 1.8.0
- **Backend:** Node.js (REST API) – mặc định `http://10.0.2.2:3000/` (localhost khi chạy trên Android Emulator)

> Tình trạng chi tiết các chức năng: xem [BAO_CAO_DU_AN_ZENTASK.md](BAO_CAO_DU_AN_ZENTASK.md).

---

## 📁 Cấu trúc thư mục

```text
ZenTask1/
├── README.md                          # Giới thiệu dự án & cấu trúc thư mục
├── BAO_CAO_DU_AN_ZENTASK.md           # Báo cáo tiến độ (đã / chưa hoàn thành)
├── build.gradle.kts                   # Cấu hình Gradle cấp dự án
├── settings.gradle.kts                # Khai báo module
├── gradle.properties
├── gradle/
│   └── libs.versions.toml             # Version catalog (quản lý phiên bản thư viện)
├── gradlew, gradlew.bat               # Gradle wrapper
└── app/
    ├── build.gradle.kts               # Cấu hình module app (SDK, dependencies)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml    # Khai báo Activity, quyền INTERNET
        │   ├── java/com/dh24tin04/zentask/
        │   │   ├── ZenTaskApplication.java       # Lớp Application
        │   │   ├── activities/                   # Các màn hình
        │   │   │   ├── WelcomeActivity.java          # Màn hình chào mừng
        │   │   │   ├── DangNhapActivity.java         # Đăng nhập
        │   │   │   ├── DangKyActivity.java           # Đăng ký
        │   │   │   ├── HomeActivity.java             # Trang chủ (streak, môn học, nhật ký)
        │   │   │   ├── DanhSachMonHocActivity.java   # Danh sách môn học dạng lưới
        │   │   │   ├── TaoDanYActivity.java          # Chọn tài liệu & gửi AI tạo dàn ý
        │   │   │   ├── DanYActivity.java             # Xem dàn ý dạng cây
        │   │   │   ├── LichHocActivity.java          # Lịch học theo tháng
        │   │   │   ├── TaoLichActivity.java          # Tạo lịch học
        │   │   │   ├── TaoWorkspaceActivity.java     # Tạo môn học / workspace
        │   │   │   └── HoSoActivity.java             # Hồ sơ cá nhân
        │   │   ├── adapters/                     # Adapter cho RecyclerView
        │   │   │   ├── MonHocAdapter.java            # Thẻ môn học (ngang / lưới)
        │   │   │   ├── nhatKyAdapter.java            # Nhật ký hoạt động gần đây
        │   │   │   ├── OutlineTreeAdapter.java       # Cây dàn ý (thụt lề theo cấp)
        │   │   │   ├── LichThangAdapter.java         # Lưới lịch tháng 7 cột
        │   │   │   ├── DailyTaskAdapter.java         # Công việc theo ngày
        │   │   │   ├── ActivityFeedAdapter.java      # Dòng thời gian hoạt động
        │   │   │   └── OnItemClickListener.java      # Interface click item dùng chung
        │   │   ├── models/                       # Lớp dữ liệu (DTO)
        │   │   │   ├── User.java, Subject.java, DanY.java
        │   │   │   ├── HomeData.java, HomeResponse.java
        │   │   │   ├── TaoSubjectRequest.java, TaoSubjectData.java
        │   │   │   ├── MonHocItem.java, NhatKyItem.java
        │   │   │   └── LoaiNhatKy.java               # Enum loại nhật ký
        │   │   ├── network/                      # Tầng gọi API
        │   │   │   ├── RetrofitClient.java           # Singleton Retrofit + BASE_URL
        │   │   │   ├── ApiService.java               # Khai báo các endpoint
        │   │   │   └── ApiResponse.java              # Wrapper {success, data, message}
        │   │   └── util/                         # Tiện ích
        │   │       ├── Constants.java                # Hằng số (giới hạn file, key Intent)
        │   │       ├── FileUtils.java                # Đọc tên / dung lượng / MIME từ Uri
        │   │       ├── UriRequestBody.java           # Upload file dạng stream
        │   │       ├── ApiErrorParser.java           # Lấy thông báo lỗi từ response
        │   │       ├── TokenManager.java             # Quản lý token đăng nhập
        │   │       ├── DateUtils.java                # Xử lý ngày tháng
        │   │       └── OutlineTreeBuilder.java       # Dựng cây dàn ý
        │   ├── res/
        │   │   ├── layout/                       # Giao diện XML
        │   │   │   ├── welcomeactivity.xml, dangnhap.xml, dangky.xml
        │   │   │   ├── homeactivity.xml, danhsachmonhocactivity.xml
        │   │   │   ├── taodanyactivity.xml, danyactivity.xml
        │   │   │   ├── lichhocactivity.xml, hoso.xml
        │   │   │   ├── thanhdieuhuong.xml            # Thanh điều hướng dưới (dùng <include>)
        │   │   │   └── item_monhoc.xml, item_nhatky.xml, item_dan_y.xml,
        │   │   │       item_lich.xml, item_daily_task.xml   # Layout từng item
        │   │   ├── drawable/                     # Nền, nút, icon (bg_*, btn_*, ic_*, nen_*)
        │   │   ├── color/                        # Color selector (stroke_login, box_stroke_selector)
        │   │   ├── mipmap-*/                     # Icon ứng dụng
        │   │   ├── values/                       # colors, strings, dimens, themes
        │   │   ├── values-night/                 # Theme chế độ tối
        │   │   └── xml/                          # Quy tắc backup dữ liệu
        │   └── ic_logo-playstore.png
        ├── test/                          # Unit test (JUnit)
        └── androidTest/                   # Instrumented test (Espresso)
```

---

## ▶️ Chạy dự án

1. Mở thư mục `ZenTask1` bằng **Android Studio**, chờ Gradle sync.
2. Khởi động Backend Node.js ở cổng `3000` trên máy tính.
3. Chạy app trên **Android Emulator** (địa chỉ `10.0.2.2` trỏ về localhost của máy).
   - Nếu chạy trên điện thoại thật, sửa `BASE_URL` trong [RetrofitClient.java](app/src/main/java/com/dh24tin04/zentask/network/RetrofitClient.java) thành IP LAN của máy chạy backend.
