package com.example.waterguard;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Size;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterguard.adapter.CommentAdapter;
import com.example.waterguard.model.Comment;

public class VideoUploadActivity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int THUMBNAIL_DEFAULT_SIZE = 100;
    private static final int THUMBNAIL_SELECTED_WIDTH = 300;
    private static final int THUMBNAIL_SELECTED_HEIGHT = 200;

    private ImageView ivThumbnail;
    private Button btnUpload;
    private Button btnSend;
    private LinearLayout llQuestions;
    private RadioGroup rgVote;
    private TextView tvAnswer;
    private EditText etComments;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private Uri selectedVideoUri;
    private boolean isVideoSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        initViews();
        setupListeners();
        setupRecyclerView();
        setThumbnailSize(false);
        updateUploadButtonText(false);
    }

    private void initViews() {
        ivThumbnail = findViewById(R.id.iv_thumbnail);
        btnUpload = findViewById(R.id.btn_upload);
        btnSend = findViewById(R.id.btn_send);
        llQuestions = findViewById(R.id.ll_questions);
        rgVote = findViewById(R.id.rg_vote);
        tvAnswer = findViewById(R.id.tv_answer);
        etComments = findViewById(R.id.et_comments);
        rvComments = findViewById(R.id.rv_comments);
    }

    private void updateUploadButtonText(boolean isSelected) {
        btnUpload.setText(isSelected ? R.string.text_upload_video : R.string.text_select_video);
    }

    private void setThumbnailSize(boolean isSelected) {
        ViewGroup.LayoutParams params = ivThumbnail.getLayoutParams();
        if (isSelected) {
            params.width = THUMBNAIL_SELECTED_WIDTH;
            params.height = THUMBNAIL_SELECTED_HEIGHT;
        } else {
            params.width = THUMBNAIL_DEFAULT_SIZE;
            params.height = THUMBNAIL_DEFAULT_SIZE;
        }
        ivThumbnail.setLayoutParams(params);
    }

    private void setupListeners() {
        btnUpload.setOnClickListener(v -> {
            if (!isVideoSelected) {
                checkPermissionAndPickVideo();
            } else {
                // Here you would typically upload the video to your server
                // For now, we'll just simulate success
                Toast.makeText(this, "Video uploaded successfully!", Toast.LENGTH_SHORT).show();
                btnUpload.setVisibility(View.GONE);
                btnSend.setVisibility(View.VISIBLE);
            }
        });

        btnSend.setOnClickListener(v -> {
            btnSend.setVisibility(View.GONE);
            llQuestions.setVisibility(View.VISIBLE);
        });

        rgVote.setOnCheckedChangeListener((group, checkedId) -> {
            String answer;
            if (checkedId == R.id.rb_yes) {
                answer = getString(R.string.text_water_safe);
            } else if (checkedId == R.id.rb_filter) {
                answer = getString(R.string.text_water_needs_to_be_filtered);
            } else {
                answer = getString(R.string.text_water_is_not_safe);
            }
            
            rgVote.setVisibility(View.GONE);
            tvAnswer.setText(answer);
            tvAnswer.setVisibility(View.VISIBLE);
            etComments.setVisibility(View.VISIBLE);
            rvComments.setVisibility(View.VISIBLE);
        });

        etComments.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && 
                event.getAction() == KeyEvent.ACTION_DOWN)) {
                
                String commentText = etComments.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    commentAdapter.addComment(new Comment(commentText));
                    etComments.setText("");
                    return true;
                }
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        commentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
    }

    private void checkPermissionAndPickVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                        PERMISSION_REQUEST_CODE);
            } else {
                pickVideo();
            }
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                pickVideo();
            }
        }
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickVideo();
            } else {
                Toast.makeText(this, "Permission denied. Cannot select video.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                try {
                    // Take persistent permission to access the file
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(selectedVideoUri, takeFlags);
                    
                    // Generate thumbnail using MediaMetadataRetriever
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(this, selectedVideoUri);
                    
                    // Try multiple frames until we get a non-null thumbnail
                    Bitmap thumbnail = null;
                    long[] timeOffsets = {0, 1000000, 2000000, 3000000}; // Try at 0s, 1s, 2s, 3s
                    
                    for (long timeOffset : timeOffsets) {
                        thumbnail = retriever.getFrameAtTime(timeOffset, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        if (thumbnail != null && !isBlackFrame(thumbnail)) {
                            break;
                        }
                        if (thumbnail != null) {
                            thumbnail.recycle();
                            thumbnail = null;
                        }
                    }
                    
                    retriever.release();

                    if (thumbnail != null) {
                        // Set new size for thumbnail
                        setThumbnailSize(true);
                        
                        // Scale the thumbnail to match the new size
                        Bitmap scaledThumbnail = Bitmap.createScaledBitmap(
                            thumbnail, 
                            THUMBNAIL_SELECTED_WIDTH, 
                            THUMBNAIL_SELECTED_HEIGHT, 
                            true
                        );
                        ivThumbnail.setImageBitmap(scaledThumbnail);
                        thumbnail.recycle();
                        
                        // Update UI state
                        isVideoSelected = true;
                        updateUploadButtonText(true);
                    } else {
                        Toast.makeText(this, "Could not generate thumbnail", Toast.LENGTH_SHORT).show();
                        isVideoSelected = false;
                        updateUploadButtonText(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error creating thumbnail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    isVideoSelected = false;
                    updateUploadButtonText(false);
                }
            }
        }
    }

    private boolean isBlackFrame(Bitmap bitmap) {
        if (bitmap == null) return true;
        
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int totalPixels = width * height;
        int[] pixels = new int[totalPixels];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        
        int blackThreshold = 30; // RGB values below this are considered "black"
        int blackPixelCount = 0;
        
        for (int pixel : pixels) {
            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = pixel & 0xff;
            
            if (red < blackThreshold && green < blackThreshold && blue < blackThreshold) {
                blackPixelCount++;
            }
        }
        
        // If more than 95% of pixels are black, consider it a black frame
        return (blackPixelCount / (float) totalPixels) > 0.95;
    }
} 