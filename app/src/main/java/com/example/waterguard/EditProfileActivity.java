package com.example.waterguard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterguard.model.User;
import com.example.waterguard.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView ivProfilePicture;
    private EditText etName, etEmail;
    private Button btnSave;
    private ExecutorService executorService;
    private SessionManager sessionManager;
    private String currentImageBase64;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        // Initialize ExecutorService and SessionManager
        executorService = Executors.newSingleThreadExecutor();
        sessionManager = new SessionManager(this);

        // Initialize views
        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        btnSave = findViewById(R.id.btn_save);

        // Load current data
        etName.setText(sessionManager.getUserName());
        etEmail.setText(sessionManager.getUserEmail());
        String profileImage = sessionManager.getProfileImage();
        currentImageBase64 = profileImage;

        if (profileImage != null && !profileImage.isEmpty()) {
            byte[] decodedString = Base64.decode(profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivProfilePicture.setImageBitmap(bitmap);
        }

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        // Resize bitmap
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                        // Convert to Base64
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        byte[] imageBytes = baos.toByteArray();
                        currentImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        ivProfilePicture.setImageBitmap(resizedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );

        ivProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String newName = etName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        long userId = sessionManager.getUserId();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);

        executorService.execute(() -> {
            // Create a new Realm instance for this thread
            Realm backgroundRealm = Realm.getDefaultInstance();
            try {
                // First check if the new email already exists (if it's different from current)
                if (!newEmail.equals(sessionManager.getUserEmail())) {
                    User existingUser = backgroundRealm.where(User.class)
                        .notEqualTo("id", userId) // Exclude current user
                        .equalTo("email", newEmail)
                        .findFirst();
                    
                    if (existingUser != null) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                            btnSave.setEnabled(true);
                        });
                        return;
                    }
                }

                // Find and update user
                backgroundRealm.executeTransaction(r -> {
                    User user = r.where(User.class)
                        .equalTo("id", userId)
                        .findFirst();

                    if (user != null) {
                        user.setEmail(newEmail);
                        user.setName(newName);
                        if (currentImageBase64 != null) {
                            user.setProfileImage(currentImageBase64);
                        }
                    }
                });

                // Update session after successful database update
                sessionManager.saveUserDetails(userId, newEmail, newName, currentImageBase64);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                });
            } finally {
                backgroundRealm.close();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 