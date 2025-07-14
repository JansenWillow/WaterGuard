package com.example.waterguard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterguard.model.User;
import com.example.waterguard.utils.SessionManager;
import io.realm.Realm;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private ImageView ivBack;
    private ExecutorService executorService;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize session manager
        sessionManager = new SessionManager(this);
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_login);
        
        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();
        
        // Find views from layout
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);
        ivBack = findViewById(R.id.iv_back);
        
        // Set click listener for back button
        ivBack.setOnClickListener(v -> finish());
        
        // Set click listener for login button
        btnLogin.setOnClickListener(v -> performLogin());
        
        // Set click listener for sign up text
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish(); // Close login activity
        });
    }
    
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // Simple validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Perform database operation in background thread
        handleLogin(email, password);
    }
    
    private void handleLogin(String email, String password) {
        executorService.execute(() -> {
            Realm backgroundRealm = Realm.getDefaultInstance();
            try {
                User user = backgroundRealm.where(User.class)
                    .equalTo("email", email)
                    .findFirst();

                if (user != null && user.getPassword().equals(password)) {
                    // Save user details to session
                    sessionManager.saveUserDetails(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        user.getProfileImage()
                    );
                    sessionManager.setLogin(true);

                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    });
                }
            } finally {
                backgroundRealm.close();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown executor
        executorService.shutdown();
    }
} 