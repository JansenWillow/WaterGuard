package com.example.waterguard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterguard.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class InformationDetailActivity extends AppCompatActivity {

    private CircleImageView ivProfile;
    private ImageView ivMenu;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize views
        ivProfile = findViewById(R.id.iv_profile);
        ivMenu = findViewById(R.id.iv_menu);

        // Load profile image
        loadProfileImage();

        // Set click listeners
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

    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
} 