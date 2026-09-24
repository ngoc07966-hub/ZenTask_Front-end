package com.dh24tin04.zentask.models;
import com.google.gson.annotations.SerializedName;

public class HomeResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private HomeData data;

    public boolean isSuccess() { return success; }
    public HomeData getData() { return data; }
}