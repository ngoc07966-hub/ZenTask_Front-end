package com.dh24tin04.zentask.models;

// phanTramTienDo và trangThai không lưu trực tiếp, DB không có 2 cột này
public class MonHocItem {

    private int idSubject;
    private String ten;
    private String deadline;
    private int tongSoMuc;
    private int soMucHoanThanh;
    private int loaiMau;
    private boolean taoTuAi; // true nếu môn được tạo từ tài liệu AI quét

    public MonHocItem() {
        // Gson cần constructor rỗng để deserialize JSON
    }

    public MonHocItem(int idSubject, String ten, int tongSoMuc, int soMucHoanThanh, int loaiMau, boolean taoTuAi) {
        this.idSubject = idSubject;
        this.ten = ten;
        this.tongSoMuc = tongSoMuc;
        this.soMucHoanThanh = soMucHoanThanh;
        this.loaiMau = loaiMau;
        this.taoTuAi = taoTuAi;
    }

    public int getIdSubject() { return idSubject; }
    public String getTen() { return ten; }
    public String getDeadline() { return deadline; }
    public int getTongSoMuc() { return tongSoMuc; }
    public int getSoMucHoanThanh() { return soMucHoanThanh; }
    public int getLoaiMau() { return loaiMau; }
    public boolean isTaoTuAi() { return taoTuAi; }

    public int getPhanTramTienDo() {
        if (tongSoMuc == 0) return 0;
        return (int) ((soMucHoanThanh * 100.0) / tongSoMuc);
    }

    public String getTrangThai() {
        return getPhanTramTienDo() >= 100 ? "Đã đạt" : "Đang học";
    }
}