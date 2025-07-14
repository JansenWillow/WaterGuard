package com.example.waterguard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterguard.model.User;
import com.example.waterguard.utils.SessionManager;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {
    
    private CircleImageView ivProfilePicture;
    private TextView tvName;
    private TextView tvEditProfile;
    private LinearLayout llPengaturan;
    private LinearLayout llChatbot;
    private LinearLayout llLogout;
    private ExecutorService executorService;
    private SessionManager sessionManager;
    private Realm realm;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        // Initialize Realm and ExecutorService
        realm = Realm.getDefaultInstance();
        executorService = Executors.newSingleThreadExecutor();
        sessionManager = new SessionManager(this);

        // Initialize views
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        tvName = findViewById(R.id.tv_name);
        tvEditProfile = findViewById(R.id.tv_edit_profil);
        llPengaturan = findViewById(R.id.ll_pengaturan);
        llChatbot = findViewById(R.id.ll_chatbot);
        llLogout = findViewById(R.id.ll_logout);

        // Load user data
        loadUserData();

        // Set click listeners
        tvEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        llPengaturan.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PengaturanDetailActivity.class);
            startActivity(intent);
        });

        llChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatBotActivity.class);
            startActivity(intent);
        });

        llLogout.setOnClickListener(v -> handleLogout());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning from edit profile
        loadUserData();
    }
    
    private void loadUserData() {
        // Set name from session
        tvName.setText(sessionManager.getUserName());
        
        // Load profile image from session
        String profileImage = sessionManager.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(profileImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivProfilePicture.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Get fresh data from database
        executorService.execute(() -> {
            Realm backgroundRealm = Realm.getDefaultInstance();
            try {
                User user = backgroundRealm.where(User.class)
                        .equalTo("email", sessionManager.getUserEmail())
                        .findFirst();
                
                if (user != null) {
                    final User userCopy = backgroundRealm.copyFromRealm(user);
                    runOnUiThread(() -> {
                        // Update session with latest data
                        sessionManager.saveUserDetails(
                            userCopy.getId(),
                            userCopy.getEmail(),
                            userCopy.getName(),
                            userCopy.getProfileImage()
                        );
                        
                        // Update UI
                        tvName.setText(userCopy.getName());
                        if (userCopy.getProfileImage() != null && !userCopy.getProfileImage().isEmpty()) {
                            try {
                                byte[] decodedString = Base64.decode(userCopy.getProfileImage(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                ivProfilePicture.setImageBitmap(decodedByte);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } finally {
                backgroundRealm.close();
            }
        });
    }
    
    private void handleLogout() {
        // Clear session
        sessionManager.logout();

        // Navigate to MainActivity and clear back stack
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        realm.close();
    }
}
