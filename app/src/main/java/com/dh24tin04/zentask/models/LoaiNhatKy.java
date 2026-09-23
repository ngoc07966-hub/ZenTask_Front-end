package com.dh24tin04.zentask.models;

import androidx.annotation.DrawableRes;
import com.dh24tin04.zentask.R;

public enum LoaiNhatKy {
    HOANTHANH(R.drawable.ic_gradient, "#4CAF7D");

    @DrawableRes
    private final int iconRes;
    private final String mauSac;

    LoaiNhatKy(@DrawableRes int iconRes, String mauSac) {
        this.iconRes = iconRes;
        this.mauSac = mauSac;
    }

    @DrawableRes
    public int getIconRes() { return iconRes; }

    public String getMauSac() { return mauSac; }
}