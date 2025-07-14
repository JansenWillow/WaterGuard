package com.example.waterguard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterguard.model.User;
import com.example.waterguard.utils.SessionManager;

import io.realm.Realm;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {
    
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnSignUp;
    private ImageView ivBack;
    private ExecutorService executorService;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        
        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        // Find views from layout
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        ivBack = findViewById(R.id.iv_back);
        
        // Set click listener for back button
        ivBack.setOnClickListener(v -> finish());
        
        // Set click listener for sign up button
        btnSignUp.setOnClickListener(v -> performSignUp());
    }
    
    private void performSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // Simple validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Perform database operations in background thread
        handleSignUp(name, email, password);
    }
    
    private void handleSignUp(String name, String email, String password) {
        executorService.execute(() -> {
            Realm backgroundRealm = Realm.getDefaultInstance();
            try {
                // Check if email already exists
                User existingUser = backgroundRealm.where(User.class)
                    .equalTo("email", email)
                    .findFirst();

                if (existingUser != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                        btnSignUp.setEnabled(true);
                    });
                    return;
                }

                // Get next ID
                Number currentMaxId = backgroundRealm.where(User.class).max("id");
                long nextId = (currentMaxId == null) ? 1 : currentMaxId.longValue() + 1;

                // Create new user
                backgroundRealm.executeTransaction(r -> {
                    User newUser = r.createObject(User.class, nextId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setPassword(password);
                });

                // Save to session
                sessionManager.saveUserDetails(nextId, email, name, null);
                sessionManager.setLogin(true);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSignUp.setEnabled(true);
                });
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