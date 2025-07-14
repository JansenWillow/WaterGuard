package com.example.waterguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterguard.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    
    private Button btnLogin;
    private Button btnSignUp;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize session manager
        sessionManager = new SessionManager(this);
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        
        // Find our buttons from the layout
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        
        // Set click listeners for our buttons
        btnLogin.setOnClickListener(v -> {
            // When login button is clicked, start LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        
        btnSignUp.setOnClickListener(v -> {
            // When sign up button is clicked, start SignUpActivity
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}