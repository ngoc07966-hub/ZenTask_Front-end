package com.dh24tin04.zentask.models;

import com.google.gson.annotations.SerializedName;

public class DanY {

    @SerializedName("IdDanY")
    private int idDanY;

    @SerializedName("IdSubject")
    private int idSubject;

    // Dùng Integer thay vì int để có thể nhận giá trị null từ Backend
    @SerializedName("ParentId")
    private Integer parentId;

    @SerializedName("TieuDe")
    private String tieuDe;

    @SerializedName("NoiDung")
    private String noiDung;

    @SerializedName("ThuTu")
    private int thuTu;

    @SerializedName("CapDo")
    private int capDo;

    @SerializedName("DoTinCay")
    private double doTinCay;

    @SerializedName("TrangThaiHoanThanh")
    private boolean trangThaiHoanThanh;

    @SerializedName("NgayNhap")
    private String ngayNhap;

    @SerializedName("NgayHoanThanh")
    private String ngayHoanThanh;

    @SerializedName("NgayLenLich")
    private String ngayLenLich;

    @SerializedName("NguonLoi")
    private String nguonLoi;

    // === Constructor rỗng ===
    public DanY() {
    }

    // === Constructor đầy đủ ===
    public DanY(int idDanY, int idSubject, Integer parentId, String tieuDe, String noiDung, int thuTu, int capDo, double doTinCay, boolean trangThaiHoanThanh, String ngayNhap, String ngayHoanThanh, String ngayLenLich, String nguonLoi) {
        this.idDanY = idDanY;
        this.idSubject = idSubject;
        this.parentId = parentId;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.thuTu = thuTu;
        this.capDo = capDo;
        this.doTinCay = doTinCay;
        this.trangThaiHoanThanh = trangThaiHoanThanh;
        this.ngayNhap = ngayNhap;
        this.ngayHoanThanh = ngayHoanThanh;
        this.ngayLenLich = ngayLenLich;
        this.nguonLoi = nguonLoi;
    }

    // === Getters và Setters ===
    public int getIdDanY() { return idDanY; }
    public void setIdDanY(int idDanY) { this.idDanY = idDanY; }

    public int getIdSubject() { return idSubject; }
    public void setIdSubject(int idSubject) { this.idSubject = idSubject; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public int getThuTu() { return thuTu; }
    public void setThuTu(int thuTu) { this.thuTu = thuTu; }

    public int getCapDo() { return capDo; }
    public void setCapDo(int capDo) { this.capDo = capDo; }

    public double getDoTinCay() { return doTinCay; }
    public void setDoTinCay(double doTinCay) { this.doTinCay = doTinCay; }

    public boolean isTrangThaiHoanThanh() { return trangThaiHoanThanh; }
    public void setTrangThaiHoanThanh(boolean trangThaiHoanThanh) { this.trangThaiHoanThanh = trangThaiHoanThanh; }

    public String getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(String ngayNhap) { this.ngayNhap = ngayNhap; }

    public String getNgayHoanThanh() { return ngayHoanThanh; }
    public void setNgayHoanThanh(String ngayHoanThanh) { this.ngayHoanThanh = ngayHoanThanh; }

    public String getNgayLenLich() { return ngayLenLich; }
    public void setNgayLenLich(String ngayLenLich) { this.ngayLenLich = ngayLenLich; }

    public String getNguonLoi() { return nguonLoi; }
    public void setNguonLoi(String nguonLoi) { this.nguonLoi = nguonLoi; }
}