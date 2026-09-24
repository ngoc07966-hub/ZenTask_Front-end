package com.dh24tin04.zentask.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dh24tin04.zentask.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnBatDau, btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.welcomeactivity);
        addview();
        addevent();
    }

    private void addview() {
        btnBatDau = findViewById(R.id.btnBatDau);
        btnlogin = findViewById(R.id.btnlogin);
    }

    private void addevent() {
        btnBatDau.setOnClickListener(v -> {
            if (daDangNhap()) {
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(WelcomeActivity.this, DangKyActivity.class));
            }
            finish();
        });

        btnlogin.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, DangNhapActivity.class));
            finish();
        });
    }

    private boolean daDangNhap() {
        SharedPreferences sharedPreferences = getSharedPreferences("ZenTaskPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "");
        return !token.isEmpty();
    }
}