package com.example.waterguard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WaterTestingActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_testing);
        
        // Find and set click listener for the detail explanation button
        TextView btnPenjelasanDetail = findViewById(R.id.btn_detail);
        btnPenjelasanDetail.setOnClickListener(v -> {
            // Navigate to PH activity
            Intent intent = new Intent(WaterTestingActivity.this, PHActivity.class);
            startActivity(intent);
        });
    }
} 