package com.example.waterguard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waterguard.adapter.ChatBotAdapter;
import com.example.waterguard.model.ChatMessage;
import com.example.waterguard.utils.NotificationHelper;
import com.example.waterguard.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends AppCompatActivity implements ChatBotAdapter.OnOptionClickListener {
    
    private RecyclerView rvChat;
    private ChatBotAdapter adapter;
    private List<ChatMessage> messages;
    private SessionManager sessionManager;
    private Handler handler;
    private NotificationHelper notificationHelper;
    
    private final String[] OPTIONS = {
        "Bagaimana cara memantau kualitas air di lingkungan saya menggunakan aplikasi WASHGuard?",
        "Apakah aplikasi WASHGuard bisa digunakan untuk melaporkan kondisi sanitasi buruk di daerah saya?",
        "Di mana saya bisa melihat edukasi atau panduan tentang mengenali air bersih?"
    };
    
    private final String[] ANSWERS = {
        "Anda bisa menggunakan alat uji sederhana seperti strip pH untuk mengukur tingkat keasaman air. Setelah itu, masukkan hasilnya ke aplikasi WASHGuard, dan aplikasi akan menampilkan informasi apakah air tersebut aman digunakan.",
        "\"Ya, kemungkinan besar aplikasi WASHGuard dapat digunakan untuk melaporkan kondisi sanitasi buruk di daerah Anda. Aplikasi semacam ini biasanya dirancang untuk memungkinkan pengguna melaporkan berbagai isu terkait air, sanitasi, dan kebersihan (WASH), termasuk kondisi sanitasi yang kurang baik seperti fasilitas yang rusak, pencemaran, atau masalah drainase. Dengan melaporkan, Anda turut membantu pihak terkait untuk mengidentifikasi dan mengatasi masalah tersebut di komunitas Anda.\"",
        "Masuk ke menu \"Edukasi Visual\" di aplikasi. Di sana tersedia panduan interaktif berupa infografis dan video singkat yang mengajarkan cara mengecek kejernihan, bau, rasa, dan warna air secara mandiri."
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        
        // Initialize
        sessionManager = new SessionManager(this);
        handler = new Handler(Looper.getMainLooper());
        messages = new ArrayList<>();
        notificationHelper = new NotificationHelper(this);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Setup RecyclerView
        rvChat = findViewById(R.id.rv_chat);
        adapter = new ChatBotAdapter(messages, this);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);
        
        // Start conversation
        startConversation();
    }
    
    private void startConversation() {
        // Add welcome message
        String welcomeMessage = sessionManager.getLanguagePreference().equals("in") ?
            "Apa yang bisa saya bantu?" : "How can I help?";
        addBotMessage(welcomeMessage);
        
        // Add options after a short delay
        handler.postDelayed(this::showOptions, 500);
    }
    
    private void showOptions() {
        String optionsMessage = String.join("\n", OPTIONS);
        addMessage(new ChatMessage(optionsMessage, ChatMessage.TYPE_OPTIONS));
    }
    
    private void addBotMessage(String message) {
        addMessage(new ChatMessage(message, ChatMessage.TYPE_BOT));
    }
    
    private void addUserMessage(String message) {
        addMessage(new ChatMessage(message, ChatMessage.TYPE_USER));
    }
    
    private void addMessage(ChatMessage message) {
        messages.add(message);
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.smoothScrollToPosition(messages.size() - 1);
        
        // Show notification if enabled and message is from bot (not options)
        if (sessionManager.isNotificationsEnabled() && message.getType() == ChatMessage.TYPE_BOT) {
            notificationHelper.showChatBotNotification(message.getMessage());
        }
    }
    
    @Override
    public void onOptionClick(int optionIndex) {
        // Add user's selection as a message
        addUserMessage(OPTIONS[optionIndex]);
        
        // Add bot's response after a short delay
        handler.postDelayed(() -> {
            addBotMessage(ANSWERS[optionIndex]);
            
            // Show options again after response
            handler.postDelayed(() -> {
                showOptions();
            }, 500);
        }, 500);
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
} 