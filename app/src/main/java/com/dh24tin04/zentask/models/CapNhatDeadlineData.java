package com.dh24tin04.zentask.models;

import com.google.gson.annotations.SerializedName;

public class CapNhatDeadlineData {

    @SerializedName("idSubject")
    private int idSubject;

    @SerializedName("deadline")
    private String deadline;

    public int getIdSubject() { return idSubject; }

    public String getDeadline() { return deadline; }
}
