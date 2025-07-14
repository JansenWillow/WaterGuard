package com.example.waterguard.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "WaterGuardSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_PROFILE_IMAGE = "profileImage";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }
    
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void saveUserDetails(long userId, String email, String name, String profileImage) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_PROFILE_IMAGE, profileImage);
        editor.commit();
    }
    
    public long getUserId() {
        return pref.getLong(KEY_USER_ID, -1);
    }
    
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }
    
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }
    
    public String getProfileImage() {
        return pref.getString(KEY_PROFILE_IMAGE, "");
    }
    
    public void saveLanguagePreference(String languageCode) {
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.commit();
    }
    
    public String getLanguagePreference() {
        return pref.getString(KEY_LANGUAGE, "en"); // Default to English
    }
    
    public void logout() {
        // Keep language preference when logging out
        String language = getLanguagePreference();
        editor.clear();
        editor.putString(KEY_LANGUAGE, language);
        editor.commit();
    }

    public void setNotificationsEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled);
        editor.commit();
    }

    public boolean isNotificationsEnabled() {
        return pref.getBoolean(KEY_NOTIFICATIONS_ENABLED, false);
    }
} 