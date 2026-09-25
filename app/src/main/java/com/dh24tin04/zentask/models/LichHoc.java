package com.dh24tin04.zentask.models;
import com.google.gson.annotations.SerializedName;
public class LichHoc {
    
    @SerializedName("IdDanY")
    private int idDanY;
    public int getIdDanY() {return idDanY;}

    @SerializedName("IdSubject")
    private int idSubject;
    public int getIdSubject() {return idSubject;}

    @SerializedName("TieuDe")
    private String tieuDe;
    public String getTieuDe(){return tieuDe;}

    @SerializedName("TrangThaiHoanThanh")
    private boolean trangThaiHoanThanh;
    public boolean isTrangThaiHoanThanh(){return trangThaiHoanThanh;}
    public void setTrangThaiHoanThanh(boolean b){trangThaiHoanThanh = b;}

    @SerializedName("NoiDung")
    private String noiDung;
    public String getNoiDung() {return noiDung;}

    @SerializedName("NgayLenLich")
    private String ngayLenLich;
    public String getNgayLenLich() {return ngayLenLich;}

    @SerializedName("Subject")
    private MonHoc subject;

    public static class MonHoc {         
        @SerializedName("Ten")
        private String ten;             
    }
    public String getTenMon() {
    if ( subject == null ) {
        return "";
    }
        return subject.ten;
    }
    
}
