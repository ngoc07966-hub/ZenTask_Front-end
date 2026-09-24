package com.dh24tin04.zentask.network;

import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.models.HomeResponse;
import com.dh24tin04.zentask.models.TaoSubjectData;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import okhttp3.MultipartBody;

public interface ApiService {

    // =========================================
    // 1. NHÓM AUTH (Đăng nhập / Đăng ký)
    // Đường dẫn gốc: auth.routes.js
    // =========================================

    // (Bạn sẽ bổ sung LoginRequest, LoginResponse sau)
    // @POST("auth/register")
    // Call<RegisterResponse> register(...);

    // @POST("auth/login")
    // Call<LoginResponse> login(...);


    @GET("api/home")
    Call<HomeResponse> getHomeData(
            @Header("Authorization") String token
    );

    @Multipart
    @POST("input/process")
    Call<ApiResponse<List<DanY>>> processFile(
            @Header("Authorization") String token,
            @Part("idSubject") RequestBody idSubject, // Thêm biến nhận ID môn học
            @Part MultipartBody.Part file
    );

    @POST("subject/create")
    Call<ApiResponse<TaoSubjectData>> taoSubject(
            @Header("Authorization") String token,
            @Body com.dh24tin04.zentask.models.TaoSubjectRequest request
    );
    @POST("lichhoc/{idSubject}")
    Call<Object> taoLich(
            @Header("Authorization") String token,
            @Path("idSubject") int idSubject
    );

    @GET("lichhoc/{ngay}")
    Call<Object> xemLich(
            @Header("Authorization") String token,
            @Path("ngay") String ngay
    );
    @PATCH("lichhoc/{idDanY}/hoanthanh")
    Call<Object> danhDauHoanThanh(
            @Header("Authorization") String token,
            @Path("idDanY") int idDanY
    );
}