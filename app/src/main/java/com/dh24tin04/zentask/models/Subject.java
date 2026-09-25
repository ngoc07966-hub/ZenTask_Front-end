package com.dh24tin04.zentask.models;

import com.google.gson.annotations.SerializedName;
public class Subject {

    // @SerializedName sẽ map đúng key "IdSubject" từ API Node.js trả về
    @SerializedName("IdSubject")
    private int idSubject;

    @SerializedName("IdUser")
    private int idUser;

    @SerializedName("LoaiMau")
    private int loaiMau;

    @SerializedName("Ten")
    private String ten;

    // Backend DateOnly được hứng bằng String để xử lý hiển thị sau
    // Sequelize trả key theo tên thuộc tính model là "DeadLine"
    @SerializedName(value = "Deadline", alternate = {"DeadLine"})
    private String deadline;

    @SerializedName("TongSoMuc")
    private int tongSoMuc;

    @SerializedName("SoMucHoanThanh")
    private int soMucHoanThanh;

    // constructor rỗng
    public Subject() {
    }

    // constructor đầy đủ
    public Subject(int idSubject, int idUser, int loaiMau, String ten, String deadline, int tongSoMuc, int soMucHoanThanh) {
        this.idSubject = idSubject;
        this.idUser = idUser;
        this.loaiMau = loaiMau;
        this.ten = ten;
        this.deadline = deadline;
        this.tongSoMuc = tongSoMuc;
        this.soMucHoanThanh = soMucHoanThanh;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getLoaiMau() {
        return loaiMau;
    }

    public void setLoaiMau(int loaiMau) {
        this.loaiMau = loaiMau;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getTongSoMuc() {
        return tongSoMuc;
    }

    public void setTongSoMuc(int tongSoMuc) {
        this.tongSoMuc = tongSoMuc;
    }

    public int getSoMucHoanThanh() {
        return soMucHoanThanh;
    }

    public void setSoMucHoanThanh(int soMucHoanThanh) {
        this.soMucHoanThanh = soMucHoanThanh;
    }
}
