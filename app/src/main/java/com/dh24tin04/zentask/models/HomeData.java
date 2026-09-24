package com.dh24tin04.zentask.models;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HomeData {
    @SerializedName("streak")
    private int streak;

    @SerializedName("subjects")
    private List<Subject> subjects;

    @SerializedName("recentActivities")
    private List<DanY> recentActivities;

    public int getStreak() { return streak; }
    public List<Subject> getSubjects() { return subjects; }
    public List<DanY> getRecentActivities() { return recentActivities; }
}