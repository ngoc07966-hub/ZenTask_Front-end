package com.dh24tin04.zentask.models;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class NhatKyItem {

    private int idDanY;
    private String tieuDe;
    private String ngayHoanThanh;
    private LoaiNhatKy loai = LoaiNhatKy.HOANTHANH;

    public NhatKyItem() {
    }

    public NhatKyItem(int idDanY, String tieuDe, String ngayHoanThanh) {
        this.idDanY = idDanY;
        this.tieuDe = tieuDe;
        this.ngayHoanThanh = ngayHoanThanh;
    }

    public int getIdDanY() { return idDanY; }
    public String getTieuDe() { return tieuDe; }
    public String getNgayHoanThanh() { return ngayHoanThanh; }
    public LoaiNhatKy getLoai() { return loai; }

    public String getThoiGianHienThi() {
        if (ngayHoanThanh == null) return "";
        try {
            Instant thoiDiem = Instant.parse(ngayHoanThanh);
            Duration khoangCach = Duration.between(thoiDiem, Instant.now());

            long phut = khoangCach.toMinutes();
            long gio = khoangCach.toHours();
            long ngay = khoangCach.toDays();

            if (phut < 1) return "Vừa xong";
            if (phut < 60) return phut + " phút trước";
            if (gio < 24) return gio + " giờ trước";
            if (ngay == 1) return "Hôm qua";
            return ngay + " ngày trước";
        } catch (DateTimeParseException e) {
            return "";
        }
    }
}