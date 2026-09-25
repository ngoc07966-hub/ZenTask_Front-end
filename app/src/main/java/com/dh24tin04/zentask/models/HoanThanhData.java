package com.dh24tin04.zentask.models;

import com.google.gson.annotations.SerializedName;

// data của PATCH api/lichhoc/{idDanY}/hoanthanh
public class HoanThanhData {

    @SerializedName("danY")
    private DanY danY;

    @SerializedName("tienDoMon")
    private TienDoMon tienDoMon;

    // Streak mới sau khi hoàn thành
    @SerializedName("streak")
    private int streak;

    public DanY getDanY() { return danY; }
    public TienDoMon getTienDoMon() { return tienDoMon; }
    public int getStreak() { return streak; }

    // Tiến độ mới của môn chứa mục vừa hoàn thành
    public static class TienDoMon {
        @SerializedName("idSubject")
        private int idSubject;

        @SerializedName("tenMon")
        private String tenMon;

        @SerializedName("soMucHoanThanh")
        private int soMucHoanThanh;

        @SerializedName("tongSoMuc")
        private int tongSoMuc;

        @SerializedName("phanTram")
        private int phanTram;

        public int getIdSubject() { return idSubject; }
        public String getTenMon() { return tenMon == null ? "" : tenMon; }
        public int getSoMucHoanThanh() { return soMucHoanThanh; }
        public int getTongSoMuc() { return tongSoMuc; }
        public int getPhanTram() { return phanTram; }
    }
}
