package com.dh24tin04.zentask.models;

import com.google.gson.annotations.SerializedName;

public class TaoSubjectRequest {

    @SerializedName("ten")
    private final String ten;

    @SerializedName("loaiMau")
    private final int loaiMau;

    public TaoSubjectRequest(String ten, int loaiMau) {
        this.ten = ten;
        this.loaiMau = loaiMau;
    }
}