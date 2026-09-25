package com.dh24tin04.zentask.models;

import com.google.gson.annotations.SerializedName;

public class CapNhatDeadlineRequest {

    // Dạng "YYYY-MM-DD"
    @SerializedName("deadline")
    private final String deadline;

    public CapNhatDeadlineRequest(String deadline) {
        this.deadline = deadline;
    }
}
