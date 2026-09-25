package com.dh24tin04.zentask.network;

import com.dh24tin04.zentask.models.CapNhatDeadlineData;
import com.dh24tin04.zentask.models.CapNhatDeadlineRequest;
import com.dh24tin04.zentask.models.DanY;
import com.dh24tin04.zentask.models.HoanThanhData;
import com.dh24tin04.zentask.models.HomeResponse;
import com.dh24tin04.zentask.models.LichHoc;
import com.dh24tin04.zentask.models.TaoSubjectData;
import com.dh24tin04.zentask.models.TaoSubjectRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

     @POST("api/auth/register")
     Call<ApiResponse<User>> register(@Body RegisterRequest request);

    // Body: { email, password } -> data: { token }
    @POST("api/auth/login")
    Call<ApiResponse<LoginData>> login(@Body LoginRequest request);



    @GET("api/home")
    Call<HomeResponse> getHomeData(
            @Header("Authorization") String token
    );

    // Body: { ten, loaiMau } -> data: { idSubject }
    @POST("api/subject")
    Call<ApiResponse<TaoSubjectData>> taoSubject(
            @Header("Authorization") String token,
            @Body TaoSubjectRequest request
    );

    // Body: { deadline: "YYYY-MM-DD" } -> data: { idSubject, deadline }
    @PATCH("api/subject/{idSubject}")
    Call<ApiResponse<CapNhatDeadlineData>> capNhatDeadline(
            @Header("Authorization") String token,
            @Path("idSubject") int idSubject,
            @Body CapNhatDeadlineRequest request
    );


    // Multipart: idSubject + file -> data: danh sách DanY AI vừa tạo
    @Multipart
    @POST("api/input/process")
    Call<ApiResponse<List<DanY>>> processFile(
            @Header("Authorization") String token,
            @Part("idSubject") RequestBody idSubject,
            @Part MultipartBody.Part file
    );



    // Xếp lịch cho các mục chưa có ngày học -> data: danh sách DanY vừa được xếp
    @POST("api/lichhoc/{idSubject}")
    Call<ApiResponse<List<DanY>>> taoLich(
            @Header("Authorization") String token,
            @Path("idSubject") int idSubject
    );

    // ngay dạng "YYYY-MM-DD" -> data: danh sách DanY kèm Subject.Ten
    @GET("api/lichhoc/{ngay}")
    Call<ApiResponse<List<LichHoc>>> xemLich(
            @Header("Authorization") String token,
            @Path("ngay") String ngay
    );

    // -> data: { danY, tienDoMon: { phanTram, ... }, streak }
    @PATCH("api/lichhoc/{idDanY}/hoanthanh")
    Call<ApiResponse<HoanThanhData>> danhDauHoanThanh(
            @Header("Authorization") String token,
            @Path("idDanY") int idDanY
    );
}
