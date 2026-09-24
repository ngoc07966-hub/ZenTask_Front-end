package com.dh24tin04.zentask.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("IdUser")
    private int idUser;

    @SerializedName("HoTen")
    private String hoTen;

    @SerializedName("Email")
    private String email;

    @SerializedName("Avatar_Url")
    private String avatarUrl;

    @SerializedName("Ngay_Tao")
    private String ngayTao;

    @SerializedName("HoatDongLanCuoi")
    private String hoatDongLanCuoi;

    @SerializedName("ChuoiHienTai")
    private int chuoiHienTai;

    @SerializedName("HoatDongGanNhat")
    private String hoatDongGanNhat;

    public User() {
    }

    public User(int idUser, String hoTen, String email, String avatarUrl, String ngayTao, String hoatDongLanCuoi, int chuoiHienTai, String hoatDongGanNhat) {
        this.idUser = idUser;
        this.hoTen = hoTen;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.ngayTao = ngayTao;
        this.hoatDongLanCuoi = hoatDongLanCuoi;
        this.chuoiHienTai = chuoiHienTai;
        this.hoatDongGanNhat = hoatDongGanNhat;
    }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getNgayTao() { return ngayTao; }
    public void setNgayTao(String ngayTao) { this.ngayTao = ngayTao; }

    public String getHoatDongLanCuoi() { return hoatDongLanCuoi; }
    public void setHoatDongLanCuoi(String hoatDongLanCuoi) { this.hoatDongLanCuoi = hoatDongLanCuoi; }

    public int getChuoiHienTai() { return chuoiHienTai; }
    public void setChuoiHienTai(int chuoiHienTai) { this.chuoiHienTai = chuoiHienTai; }

    public String getHoatDongGanNhat() { return hoatDongGanNhat; }
    public void setHoatDongGanNhat(String hoatDongGanNhat) { this.hoatDongGanNhat = hoatDongGanNhat; }
}