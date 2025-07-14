package com.example.waterguard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.waterguard.utils.SessionManager;
import java.util.Locale;

public class PengaturanDetailActivity extends AppCompatActivity {
    
    private CheckBox cbLanguage;
    private CheckBox cbNotifications;
    private SessionManager sessionManager;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_detail);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // Permission granted, enable notifications
                    sessionManager.setNotificationsEnabled(true);
                    cbNotifications.setChecked(true);
                } else {
                    // Permission denied, keep notifications disabled
                    sessionManager.setNotificationsEnabled(false);
                    cbNotifications.setChecked(false);
                    Toast.makeText(this, "Notification permission is required for ChatBot notifications", Toast.LENGTH_LONG).show();
                }
            }
        );
        
        // Initialize views
        cbLanguage = findViewById(R.id.cb_language);
        cbNotifications = findViewById(R.id.cb_notification);
        
        // Set initial states
        String currentLanguage = sessionManager.getLanguagePreference();
        cbLanguage.setChecked(currentLanguage.equals("in"));
        
        // Check if notification permission is granted for Android 13+
        boolean hasNotificationPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        
        // Set initial notification state based on both permission and saved preference
        cbNotifications.setChecked(hasNotificationPermission && sessionManager.isNotificationsEnabled());
        
        // Handle language change
        cbLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newLanguage = isChecked ? "in" : "en";
            if (!newLanguage.equals(currentLanguage)) {
                setLocale(newLanguage);
                // Restart app to apply language change
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        
        // Handle notifications
        cbNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // For Android 13 and above, check and request notification permission
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        // Don't update checkbox yet - wait for permission result
                        buttonView.setChecked(false);
                        return;
                    }
                }
                // Permission already granted or not needed (Android < 13)
                sessionManager.setNotificationsEnabled(true);
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                sessionManager.setNotificationsEnabled(false);
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        
        // Save language preference
        sessionManager.saveLanguagePreference(languageCode);
    }
} 