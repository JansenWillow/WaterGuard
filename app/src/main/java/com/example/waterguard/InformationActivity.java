package com.example.waterguard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterguard.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class InformationActivity extends AppCompatActivity {

    private ImageView ivGoToDetail;
    private TextView tvGoToInformationDetail;
    private CircleImageView ivProfile;
    private ImageView ivMenu;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize views
        ivGoToDetail = findViewById(R.id.iv_go_to_detail);
        tvGoToInformationDetail = findViewById(R.id.tv_go_to_information_detail);
        ivProfile = findViewById(R.id.iv_profile);
        ivMenu = findViewById(R.id.iv_menu);

        // Load profile image
        loadProfileImage();

        // Set click listeners
        ivGoToDetail.setOnClickListener(v -> navigateToDetail());
        tvGoToInformationDetail.setOnClickListener(v -> navigateToDetail());
        ivProfile.setOnClickListener(v -> navigateToProfile());
        ivMenu.setOnClickListener(v -> navigateToProfile());
    }

    private void loadProfileImage() {
        String profileImageBase64 = sessionManager.getProfileImage();
        if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivProfile.setImageBitmap(bitmap);
        }
    }

    private void navigateToDetail() {
        Intent intent = new Intent(this, InformationDetailActivity.class);
        startActivity(intent);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
} 