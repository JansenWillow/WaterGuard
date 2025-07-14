package com.example.waterguard;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterguard.utils.SessionManager;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    
    private ImageView ivMenu;
    private CircleImageView ivProfile;
    private TextView btnVideoEdukasi;
    private TextView btnPengujian;
    private TextView btnInformasiWilayah;
    private TextView btnCommunity;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sessionManager = new SessionManager(this);

        if (sessionManager.getLanguagePreference().isBlank()) {
            Locale locale = Locale.getDefault();

            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());

            // Save language preference
            sessionManager.saveLanguagePreference(locale.getLanguage());
        }
        
        // Initialize views
        ivMenu = findViewById(R.id.iv_menu);
        ivProfile = findViewById(R.id.iv_profile);
        btnVideoEdukasi = findViewById(R.id.btn_video_edukasi);
        btnPengujian = findViewById(R.id.btn_pengujian);
        btnInformasiWilayah = findViewById(R.id.btn_informasi_wilayah);
        btnCommunity = findViewById(R.id.btn_community);
        
        // Set click listeners for both menu and profile icons to go to profile
        View.OnClickListener profileClickListener = v -> {
            // Navigate to profile activity
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        };

        ivProfile.setOnClickListener(profileClickListener);
        ivMenu.setOnClickListener(profileClickListener);
        
        btnVideoEdukasi.setOnClickListener(v -> {
            // Navigate to video education activity
            Intent intent = new Intent(HomeActivity.this, VideoEducationActivity.class);
            startActivity(intent);
        });
        
        btnPengujian.setOnClickListener(v -> {
            // Navigate to water testing activity
            Intent intent = new Intent(HomeActivity.this, WaterTestingActivity.class);
            startActivity(intent);
        });

        btnInformasiWilayah.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, InformationActivity.class);
            startActivity(intent);
        });

        btnCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, VideoUploadActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String profileImage = sessionManager.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(profileImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivProfile.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}