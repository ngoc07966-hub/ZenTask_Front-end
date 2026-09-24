package com.dh24tin04.zentask.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Response;
public final class ApiErrorParser {

    private ApiErrorParser() {}

    public static String layThongBao(Response<?> res) {
        try {
            if (res.errorBody() != null) {
                JsonObject o = JsonParser.parseString(res.errorBody().string()).getAsJsonObject();
                if (o.has("message") && !o.get("message").isJsonNull()) {
                    return o.get("message").getAsString();
                }
            }
        } catch (Exception ignored) {
            // rơi xuống thông báo mặc định
        }
        return "Lỗi máy chủ (" + res.code() + ")";
    }
}